package com.apium.legacy_kata_real;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LiftPassControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void should_return_a_price() throws Exception {
    mockMvc.perform(get("/prices")
            .param("type", "night")
            .param("age", "23")
            .param("date", "2019-02-18"))
        .andExpect(status().isOk())
        // Add more assertions as needed
        .andExpect(jsonPath("$.cost").value("19"));
  }
}