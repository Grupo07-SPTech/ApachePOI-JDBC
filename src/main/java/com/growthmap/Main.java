package com.growthmap;

import com.growthmap.conexao.Conexao;
import com.growthmap.autenticacao.Autenticacao;
import com.growthmap.logs.LogSistema;
import com.growthmap.processamento.ProcessamentoEnem;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.sync.ResponseTransformer;

import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String bucketName = "dadosexcelgrowthmap-365333651479-us-east-1-an";
        String[] arquivos = {"RESULTADOS_2025.xlsx", "RESULTADOS_2024.xlsx"};
        Path diretorioArquivos = Paths.get("/app/arquivos");

        try {
            Files.createDirectories(diretorioArquivos);

            try (S3Client s3Client = S3Client.builder()
                    .region(Region.US_EAST_1)
                    .build()) {
                for (String arquivo : arquivos) {
                    Path caminhoArquivo = diretorioArquivos.resolve(arquivo);
                    if (Files.notExists(caminhoArquivo)) {
                        GetObjectRequest requisicao = GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(arquivo)
                                .build();

                        try {
                            s3Client.getObject(requisicao, ResponseTransformer.toFile(caminhoArquivo));
                            System.out.println("Arquivo " + arquivo + " baixado do S3!");
                        } catch (Exception e) {
                            Files.deleteIfExists(caminhoArquivo);
                            throw e;
                        }
                    } else {
                        System.out.println("O arquivo " + arquivo + " ja existe. Download ignorado.");
                    }
                }
            }

            JdbcTemplate banco = new JdbcTemplate(new Conexao().getConexao());
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("E-mail: ");
                String email = scanner.nextLine();
                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                Autenticacao autenticacao = new Autenticacao(banco);
                Integer idUsuario = autenticacao.autenticar(email, senha);

                if (idUsuario == null) {
                    System.out.println("E-mail ou senha invalidos.");
                    return;
                }

                System.out.println("Login realizado com sucesso!");
                LogSistema log = new LogSistema(banco);
                log.registrar(idUsuario, "LOGIN_REALIZADO");

                ProcessamentoEnem processamento = new ProcessamentoEnem(banco, log);
                for (String arquivo : arquivos) {
                    Path caminhoArquivo = diretorioArquivos.resolve(arquivo);
                    processamento.processar(idUsuario, caminhoArquivo.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
