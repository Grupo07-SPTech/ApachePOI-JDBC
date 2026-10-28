package com.growthmap;

import com.growthmap.conexao.Conexao;
import com.growthmap.autenticacao.Autenticacao;
import com.growthmap.logs.LogSistema;
import com.growthmap.processamento.ProcessamentoEnem;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String nomeArquivo = "RESULTADOS_2025.xlsx";

        try {
            JdbcTemplate banco = new JdbcTemplate(new Conexao().getConexao());
            Scanner scanner = new Scanner(System.in);

            System.out.print("E-mail: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            Autenticacao autenticacao = new Autenticacao(banco);
            Integer idUsuario = autenticacao.autenticar(email, senha);

            if (idUsuario == null) {
                System.out.println("E-mail ou senha inválidos.");
                return;
            }

            System.out.println("Login realizado com sucesso!");
            LogSistema log = new LogSistema(banco);
            log.registrar(idUsuario, "LOGIN_REALIZADO");

            ProcessamentoEnem processamento = new ProcessamentoEnem(banco, log);
            processamento.processar(idUsuario, nomeArquivo);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
