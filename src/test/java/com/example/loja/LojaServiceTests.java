package com.example.loja;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LojaServiceTests {
    private final LojaService service = new LojaService();

    // Aceita os separadores de pt-BR e en-US sem alterar o locale global.
    private String compra(String produto, double quantidade) {
        return service.calcularCompra(produto, quantidade).replace(',', '.');
    }

    @ParameterizedTest
    @CsvSource({"FRANGO, 10.00", "BIFE, 12.99", "PERU, 18.00",
        "PICANHA, 69.00", "CHESTER, 22.00", "BISTECA, 19.99",
        "FEIJÃO COM FARINHA, 67.00"})
    void calculaPrecoPorQuilo(String produto, String preco) {
        assertTrue(compra(produto, 1).contains("Total: R$ " + preco));
    }

    @Test
    void aceitaProdutoEmMinusculasEQuantidadeFracionaria() {
        assertTrue(compra("frango", 2.5).contains("Total: R$ 25.00"));
    }

    @Test
    void aplicaDescontoSomenteAcimaDeDezQuilos() {
        assertTrue(compra("FRANGO", 10).contains("Total: R$ 100.00"));
        assertTrue(compra("FRANGO", 11).contains("Total: R$ 104.50"));
    }

    @Test
    void rejeitaNomeAusenteOuVazio() {
        assertTrue(compra(null, 1).contains("Erro:"));
        assertTrue(compra("   ", 1).contains("Erro:"));
    }

    @Test
    void rejeitaQuantidadeNaoPositiva() {
        assertTrue(compra("FRANGO", 0).contains("Erro:"));
        assertTrue(compra("FRANGO", -1).contains("Erro:"));
    }

    @Test
    void informaProdutoDesconhecido() {
        assertTrue(compra("INEXISTENTE", 1).contains("Produto inválido"));
    }
}
