package com.edupulse.support;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiClient {

    private final MockMvc mvc;

    public ApiClient(MockMvc mvc) {
        this.mvc = mvc;
    }

    public String registerAdmin() throws Exception {
        String email = "admin-" + UUID.randomUUID() + "@example.com";
        String json = mvc.perform(MockMvcRequestBuilders.post("/api/auth/register-institute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instituteName":"Inst","fullName":"Admin","email":"%s","password":"password123"}"""
                                .formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return field(json, "accessToken");
    }

    public String createStaffToken(String adminToken, String role) throws Exception {
        String email = role.toLowerCase() + "-" + UUID.randomUUID() + "@example.com";
        post(adminToken, "/api/users", """
                {"fullName":"Staff","email":"%s","password":"password123","role":"%s"}"""
                .formatted(email, role)).andExpect(status().isCreated());
        String json = mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123"}""".formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return field(json, "accessToken");
    }

    public String createBatch(String token, int monthlyFee) throws Exception {
        return post(token, "/api/batches", """
                {"name":"Physics","subject":"Physics","monthlyFee":%d,"dayOfWeek":"SATURDAY","startTime":"08:00","endTime":"10:00"}"""
                .formatted(monthlyFee))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    public String createStudent(String token) throws Exception {
        return post(token, "/api/students", """
                {"fullName":"Nimal Perera","parentName":"Kamal Perera","parentPhone":"0771234567"}""")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    public ResultActions enroll(String token, String studentId, String batchId) throws Exception {
        return post(token, "/api/enrollments", """
                {"studentId":"%s","batchId":"%s"}""".formatted(studentId, batchId));
    }

    public ResultActions post(String token, String path, String body) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(path).header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    public ResultActions get(String token, String pathWithQuery) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(pathWithQuery).header("Authorization", bearer(token)));
    }

    public static String bearer(String token) {
        return "Bearer " + token;
    }

    public static String field(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        if (!m.find()) {
            throw new AssertionError(name + " not found in: " + json);
        }
        return m.group(1);
    }
}