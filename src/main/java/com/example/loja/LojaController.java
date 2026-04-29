package com.example.loja;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/loja")
public class LojaController {
    @Autowired private LojaService lojaService;

    @GetMapping("/compra")
    public String realizarCompra(
        @RequestParam String produto, 
        @RequestParam double quantidade) {
        return lojaService.calcularCompra(produto, quantidade);
    }
}