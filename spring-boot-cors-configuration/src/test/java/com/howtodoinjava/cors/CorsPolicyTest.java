package com.howtodoinjava.cors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CorsPolicyTest {
    @Autowired MockMvc mvc;

    @Test void allowedActualGetHasCorsHeader() throws Exception {
        mvc.perform(get("/api/stock/BOOK-42").header("Origin", "https://shop.example"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://shop.example"))
                .andExpect(jsonPath("$.available").value(7));
    }

    @Test void rejectedOriginCannotReadStock() throws Exception {
        mvc.perform(get("/api/stock/BOOK-42").header("Origin", "https://other.example"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test void publicPreflightAllowsShopOrigin() throws Exception {
        mvc.perform(options("/api/stock/BOOK-42")
                        .header("Origin", "https://shop.example")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://shop.example"));
    }

    @Test void securedReportPreflightSucceedsWithoutAuthentication() throws Exception {
        mvc.perform(options("/api/reports/2026-10-28")
                        .header("Origin", "https://admin.example")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "X-Request-Id"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://admin.example"))
                .andExpect(header().string("Access-Control-Allow-Headers", "X-Request-Id"));
    }

    @Test void securedReportStillNeedsAuthentication() throws Exception {
        mvc.perform(get("/api/reports/2026-10-28").header("Origin", "https://admin.example"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://admin.example"));
    }

    @Test void noOriginHeaderIsOrdinaryHttpGet() throws Exception {
        mvc.perform(get("/api/stock/BOOK-42"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
