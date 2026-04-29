package com.example.loja;

import org.springframework.stereotype.Service; // Precisa disso para ser possível criar um Service

@Service // Indica que a classe abaixo é um Service
public class LojaService {
    public String calcularCompra(String produto, double quantidade) { // Método para calcular o valor total da compra
        
        // Tratamento de (algumas) exceções
        if (produto == null || produto.trim().isEmpty()) {
            return "<span style='color: red; font-weight: bold;'>Erro:</span> O nome do produto não pode ser nulo.";
        }

        if (quantidade <= 0) {
            return "<span style='color: red; font-weight: bold;'>Erro:</span> A quantidade do produto deve ser informada e maior que 0.";
        }

        double precoKg;

        switch (produto.toUpperCase()) { // Confirmar o nome do produto e o preço atrelado a ele
            case "FRANGO":
                precoKg = 10.00;
                break;
            case "BIFE":
                precoKg = 12.99;
                break;
            case "PERU":
                precoKg = 18.00;
                break;
            case "PICANHA":
                precoKg = 69.00;
                break;
            case "CHESTER":
                precoKg = 22.00;
                break;
            case "BISTECA":
                precoKg = 19.99;
                break;
            case "FEIJÃO COM FARINHA":
                precoKg = 67.00;
                break;
            case "JACKPOT": // Easter egg e ja da pra colocar um retorno direto aqui
                return """
                        <strong style='font-size: 100px; color: gold;'>777</strong> <br>
                        <img src='https://i.ytimg.com/vi/S6W6jhGL8Ow/mqdefault.jpg' width='500'> <br>
                        <strong style='font-size: 30px;'>Jackpot.</strong>
                        """;
            default:
                return """
                        Produto inválido, digite algum dos seguintes produtos: <br>
                        FRANGO; <br>
                        BIFE; <br>
                        PERU; <br>
                        PICANHA; <br>
                        CHESTER; <br>
                        BISTECA; <br>
                        <strong style='color: #ff0000;'>FEIJÃO COM FARINHA.</strong> <br>
                        <img src='https://upload.wikimedia.org/wikipedia/pt/0/0d/Resident_Evil_Tyrant.png' width='200'>
                        """;
        }
        double valorCompra = precoKg * quantidade; // Valor total da compra.

        if (quantidade > 10) { // Desconto de 5% caso a quantidade seja > que 10kg.
            valorCompra *= 0.95;
        }

        return String.format(
                "Produto: %s | Quantidade: %.1fkg | Total: R$ %.2f <br> <img src='https://upload.wikimedia.org/wikipedia/pt/7/7d/RE9_-_Leon_Render.png' width='200'>",
                produto.toUpperCase(), quantidade, valorCompra);
    }
}
