package com.example.loja;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LojaApplicationTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void compraRetornaProdutoETotalCalculado() throws Exception {
        mvc.perform(get("/loja/compra").param("produto", "FRANGO").param("quantidade", "2"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Produto: FRANGO")))
            .andExpect(content().string(org.hamcrest.Matchers.anyOf(
                containsString("Total: R$ 20.00"), containsString("Total: R$ 20,00"))));
    }

    @Test
    void parametroObrigatorioAusenteRetorna400() throws Exception {
        mvc.perform(get("/loja/compra").param("produto", "FRANGO"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void quantidadeNaoNumericaRetorna400() throws Exception {
        mvc.perform(get("/loja/compra").param("produto", "FRANGO").param("quantidade", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void healthConfirmaAplicacaoDisponivel() throws Exception {
        mvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}
