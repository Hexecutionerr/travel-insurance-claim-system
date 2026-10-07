package com.academic.travelinsurance;

import com.academic.travelinsurance.dto.ClaimSubmitRequest;
import com.academic.travelinsurance.entity.ClaimStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for claim validation (Task 3 — Claim Validation feature).
 *
 * Tests cover:
 *  TEST 1 — Valid claim submission
 *  TEST 2 — Empty customer name
 *  TEST 3 — Empty policy number
 *  TEST 4 — Empty destination
 *  TEST 5 — Claim amount = 0
 *  TEST 6 — Negative claim amount
 *  TEST 7 — Empty description
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClaimValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------------------------------------------------
    // Helper: build a fully valid ClaimSubmitRequest
    // -----------------------------------------------------------------------
    private ClaimSubmitRequest validRequest() {
        ClaimSubmitRequest req = new ClaimSubmitRequest();
        req.setPolicyNumber("POL-2024-001");
        req.setCustomerName("Hasnain Ahmed");
        req.setDestination("Dubai, UAE");
        req.setTravelDate(LocalDate.of(2024, 10, 15));
        req.setClaimType("Medical Emergency");
        req.setClaimAmount(new BigDecimal("15000.00"));
        req.setDescription("Fell ill during the trip and required hospitalisation.");
        req.setDocumentReference("DR-2024-001");
        return req;
    }

    // -----------------------------------------------------------------------
    // TEST 1 — Valid claim: expect 201 Created, claimId generated, status PENDING
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 1 — Valid claim submission succeeds with 201 and PENDING status")
    void test1_validClaimSubmission_returns201WithPendingStatus() throws Exception {
        ClaimSubmitRequest req = validRequest();

        MvcResult result = mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimId").isNumber())
                .andExpect(jsonPath("$.status").value(ClaimStatus.PENDING.name()))
                .andExpect(jsonPath("$.customerName").value("Hasnain Ahmed"))
                .andExpect(jsonPath("$.policyNumber").value("POL-2024-001"))
                .andReturn();

        System.out.println("[TEST 1 PASSED] Valid claim submitted. Response: "
                + result.getResponse().getContentAsString());
    }

    // -----------------------------------------------------------------------
    // TEST 2 — Empty customer name: expect 400 Bad Request with fieldErrors
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 2 — Empty customer name returns 400 Validation Failed")
    void test2_emptyCustomerName_returns400() throws Exception {
        ClaimSubmitRequest req = validRequest();
        req.setCustomerName("");   // blank

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.customerName").exists());

        System.out.println("[TEST 2 PASSED] Empty customer name correctly rejected with 400.");
    }

    // -----------------------------------------------------------------------
    // TEST 3 — Empty policy number: expect 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 3 — Empty policy number returns 400 Validation Failed")
    void test3_emptyPolicyNumber_returns400() throws Exception {
        ClaimSubmitRequest req = validRequest();
        req.setPolicyNumber("");   // blank

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.policyNumber").exists());

        System.out.println("[TEST 3 PASSED] Empty policy number correctly rejected with 400.");
    }

    // -----------------------------------------------------------------------
    // TEST 4 — Empty destination: expect 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 4 — Empty destination returns 400 Validation Failed")
    void test4_emptyDestination_returns400() throws Exception {
        ClaimSubmitRequest req = validRequest();
        req.setDestination("");    // blank

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.destination").exists());

        System.out.println("[TEST 4 PASSED] Empty destination correctly rejected with 400.");
    }

    // -----------------------------------------------------------------------
    // TEST 5 — Claim amount = 0: expect 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 5 — Claim amount = 0 returns 400 Validation Failed")
    void test5_claimAmountZero_returns400() throws Exception {
        ClaimSubmitRequest req = validRequest();
        req.setClaimAmount(BigDecimal.ZERO);   // 0 — must be > 0

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.claimAmount").exists());

        System.out.println("[TEST 5 PASSED] Claim amount = 0 correctly rejected with 400.");
    }

    // -----------------------------------------------------------------------
    // TEST 6 — Negative claim amount: expect 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 6 — Negative claim amount returns 400 Validation Failed")
    void test6_negativeClaimAmount_returns400() throws Exception {
        ClaimSubmitRequest req = validRequest();
        req.setClaimAmount(new BigDecimal("-500.00"));   // negative

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.claimAmount").exists());

        System.out.println("[TEST 6 PASSED] Negative claim amount correctly rejected with 400.");
    }

    // -----------------------------------------------------------------------
    // TEST 7 — Empty description: expect 400 Bad Request
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TEST 7 — Empty description returns 400 Validation Failed")
    void test7_emptyDescription_returns400() throws Exception {
        ClaimSubmitRequest req = validRequest();
        req.setDescription("");    // blank

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.description").exists());

        System.out.println("[TEST 7 PASSED] Empty description correctly rejected with 400.");
    }
}
