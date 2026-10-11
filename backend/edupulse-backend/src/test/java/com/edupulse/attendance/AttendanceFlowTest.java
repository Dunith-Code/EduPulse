package com.edupulse.attendance;

import com.edupulse.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AttendanceFlowTest {

    private static final String DATE = "2026-10-12";

    @Autowired
    MockMvc mvc;

    @Test
    void scanMarksPresentAndIsIdempotent() throws Exception {
        String admin = register();
        String batchId = field(createBatch(admin), "id");
        String student = createStudent(admin);
        enroll(admin, field(student, "id"), batchId).andExpect(status().isCreated());
        String qr = field(student, "qrCode");

        scan(admin, batchId, qr).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRESENT"))
                .andExpect(jsonPath("$.method").value("QR"));
        scan(admin, batchId, qr).andExpect(status().isOk());

        mvc.perform(get("/api/attendance").param("batchId", batchId).param("date", DATE)
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void manualMarkCorrectsAnExistingRecord() throws Exception {
        String admin = register();
        String batchId = field(createBatch(admin), "id");
        String student = createStudent(admin);
        String studentId = field(student, "id");
        enroll(admin, studentId, batchId).andExpect(status().isCreated());

        manual(admin, batchId, studentId, "ABSENT").andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABSENT"));
        scan(admin, batchId, field(student, "qrCode")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRESENT"));
    }

    @Test
    void notEnrolledStudentCannotBeMarked() throws Exception {
        String admin = register();
        String batchId = field(createBatch(admin), "id");
        String student = createStudent(admin);

        scan(admin, batchId, field(student, "qrCode")).andExpect(status().isConflict());
    }

    @Test
    void anotherInstituteCannotUseOurStudents() throws Exception {
        String adminA = register();
        String studentA = createStudent(adminA);

        String adminB = register();
        String batchB = field(createBatch(adminB), "id");

        scan(adminB, batchB, field(studentA, "qrCode")).andExpect(status().isNotFound());
        enroll(adminB, field(studentA, "id"), batchB).andExpect(status().isNotFound());
    }

    @Test
    void teacherCanMarkAttendanceButCannotEnroll() throws Exception {
        String admin = register();
        String batchId = field(createBatch(admin), "id");
        String student = createStudent(admin);
        enroll(admin, field(student, "id"), batchId).andExpect(status().isCreated());

        String teacherEmail = "teacher-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/api/users").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Teacher","email":"%s","password":"password123","role":"TEACHER"}"""
                                .formatted(teacherEmail)))
                .andExpect(status().isCreated());
        String teacher = field(mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123"}""".formatted(teacherEmail)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                "accessToken");

        scan(teacher, batchId, field(student, "qrCode")).andExpect(status().isOk());
        enroll(teacher, field(student, "id"), batchId).andExpect(status().isForbidden());
    }

    // ---- helpers ----

    private String register() throws Exception {
        String email = "admin-" + UUID.randomUUID() + "@example.com";
        String json = mvc.perform(post("/api/auth/register-institute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instituteName":"Inst","fullName":"Admin","email":"%s","password":"password123"}"""
                                .formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return field(json, "accessToken");
    }

    private String createBatch(String token) throws Exception {
        return mvc.perform(post("/api/batches").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Physics","subject":"Physics","monthlyFee":3500,"dayOfWeek":"SATURDAY","startTime":"08:00","endTime":"10:00"}"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String createStudent(String token) throws Exception {
        return mvc.perform(post("/api/students").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Nimal Perera","parentName":"Kamal Perera","parentPhone":"0771234567"}"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private ResultActions enroll(String token, String studentId, String batchId) throws Exception {
        return mvc.perform(post("/api/enrollments").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"studentId":"%s","batchId":"%s"}""".formatted(studentId, batchId)));
    }

    private ResultActions scan(String token, String batchId, String qr) throws Exception {
        return mvc.perform(post("/api/attendance/scan").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"batchId":"%s","qrCode":"%s","sessionDate":"%s"}"""
                        .formatted(batchId, qr, DATE)));
    }

    private ResultActions manual(String token, String batchId, String studentId, String status)
            throws Exception {
        return mvc.perform(post("/api/attendance/manual").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"batchId":"%s","studentId":"%s","status":"%s","sessionDate":"%s"}"""
                        .formatted(batchId, studentId, status, DATE)));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    private static String field(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        assertThat(m.find()).as(name + " in: " + json).isTrue();
        return m.group(1);
    }
}