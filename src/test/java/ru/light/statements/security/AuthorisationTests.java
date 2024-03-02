package ru.light.statements.security;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ru.light.statements.controllers.AuthController;
import ru.light.statements.entities.User;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorisationTests {

    @Autowired
    private MockMvc mvc;

    User admin = User.builder()
                    .id(1L)
                    .login("admin")
                    .password("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8")
                    .build();

    @Test
    @WithMockUser(value = "spring")
    public void givenRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/api/auth/hi").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void authirizeAdminUser_shouldSucceedWith200() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
}
