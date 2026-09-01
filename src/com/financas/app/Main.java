package com.financas.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Transacao> lista = new ArrayList<>();

        lista.add(new Receita("Salário Mensal", 2000, LocalDate.now(), categoria.SALARIO, "Empresa"));
        lista.add(new Despesa("Aluguel", 800, LocalDate.now(), categoria.MORADIA, Despesa.FormaPagamento.PIX));

        for (Transacao t : lista) {
            System.out.println(t);
        }
    }

}
