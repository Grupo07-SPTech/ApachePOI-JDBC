package com.growthmap;

import com.growthmap.conexao.Conexao;
import com.growthmap.autenticacao.Autenticacao;
import com.growthmap.logs.LogSistema;
import com.growthmap.processamento.ProcessamentoEnem;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import com.growthmap.client.S3Provider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import java.io.File;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        
        S3Client s3Client = new S3Provider().getS3Client();

        String bucketName = "dadosexcelgrowthmap-365333651479-us-east-1-an";
        String nomeArquivo = "/app/arquivos/RESULTADOS_2025.xlsx";

        File arquivo = new File(nomeArquivo);

        if (!arquivo.exists()) {
            try {
                GetObjectRequest requisicao = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key("RESULTADOS_2025.xlsx")
                        .build();

                s3Client.getObject(
                        requisicao,
                        ResponseTransformer.toFile(arquivo.toPath())
                );

                System.out.println("Arquivo baixado do S3!");

            } catch (Exception e) {
                System.err.println("Erro ao baixar arquivo do S3: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("O arquivo já existe. Download ignorado.");
        }

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
