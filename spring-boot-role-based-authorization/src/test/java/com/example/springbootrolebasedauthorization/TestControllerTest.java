package com.example.springbootrolebasedauthorization;

import com.example.springbootrolebasedauthorization.web.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(TestController.class)
 class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAuthenticatedUser_AdminRole_Success() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("user", "password", "ADMIN");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String auth = "{\"authorities\":[{\"authority\":\"ADMIN\"}],\"details\":null,\"authenticated\":true,\"credentials\":\"password\",\"principal\":\"user\",\"name\":\"user\"}";
        mockMvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(auth));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getAuthenticatedUser_UserRole_AccessDenied() throws Exception {
        Authentication authentication = new TestingAuthenticationToken("user", "password", "USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void getAuthenticatedUser_UnauthenticatedUser_AccessDenied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
