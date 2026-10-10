package com.edupulse.auth;

import com.edupulse.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
class AuthFlowTest {

    private static final Pattern TOKEN = Pattern.compile("\"accessToken\"\\s*:\\s*\"([^\"]+)\"");
    private static final String STUDENT = """
            {"fullName":"Nimal Perera","parentName":"Kamal Perera","parentPhone":"0771234567"}""";

    @Autowired
    MockMvc mvc;

    @Test
    void requestsWithoutTokenAreRejected() throws Exception {
        mvc.perform(get("/api/students")).andExpect(status().isUnauthorized());
    }

    @Test
    void tenantComesFromTokenAndDataIsIsolated() throws Exception {
        String tokenA = register("Institute A", uniqueEmail("a"));
        String tokenB = register("Institute B", uniqueEmail("b"));

        mvc.perform(post("/api/students").header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/students").header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(get("/api/students").header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void teacherCannotCreateStudents() throws Exception {
        String adminToken = register("Institute C", uniqueEmail("c"));
        String teacherEmail = uniqueEmail("teacher");

        mvc.perform(post("/api/users").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Teacher One","email":"%s","password":"password123","role":"TEACHER"}"""
                                .formatted(teacherEmail)))
                .andExpect(status().isCreated());

        String teacherToken = login(teacherEmail, "password123");

        mvc.perform(post("/api/students").header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
                .andExpect(status().isForbidden());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        String email = uniqueEmail("d");
        register("Institute D", email);

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"wrong-password"}""".formatted(email)))
                .andExpect(status().isUnauthorized());
    }

    private String register(String institute, String email) throws Exception {
        String json = mvc.perform(post("/api/auth/register-institute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instituteName":"%s","fullName":"Admin","email":"%s","password":"password123"}"""
                                .formatted(institute, email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return extractToken(json);
    }

    private String login(String email, String password) throws Exception {
        String json = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}""".formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return extractToken(json);
    }

    private static String extractToken(String json) {
        Matcher m = TOKEN.matcher(json);
        assertThat(m.find()).as("accessToken in response: " + json).isTrue();
        return m.group(1);
    }

    private static String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@example.com";
    }
}