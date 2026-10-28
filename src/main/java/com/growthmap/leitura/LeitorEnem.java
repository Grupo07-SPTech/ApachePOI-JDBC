package com.growthmap.leitura;

import com.growthmap.modelo.Agregacao;

import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LeitorEnem {

    private final int COL_ANO = 1;
    private final int COL_CODIGO_MUNICIPIO = 10;
    private final int COL_NOME_MUNICIPIO = 11;
    private final int COL_UF = 13;
    private final int COL_NOTA_CN = 22;
    private final int COL_NOTA_CH = 23;
    private final int COL_NOTA_LC = 24;
    private final int COL_NOTA_MT = 25;
    private final int COL_NOTA_REDACAO = 41;

    public List<Agregacao> extrairDados(String nomeArquivo) throws Exception {
        List<Agregacao> agregacoes = new ArrayList<>();
        int linhasIgnoradas = 0;
        int linhasSemMunicipio = 0;
        int totalPlanilhas = 0;

        try (InputStream arquivo = new FileInputStream(nomeArquivo);
             Workbook workbook = StreamingReader.builder().open(arquivo)) {

            System.out.println("Iniciando leitura...");

            for (Sheet planilha : workbook) {
                totalPlanilhas++;
                int participantesValidos = 0;
                System.out.println("Lendo planilha " + totalPlanilhas + ": " + planilha.getSheetName());

                for (Row linha : planilha) {
                    if (linha.getRowNum() == 0) {
                        continue;
                    }

                    Double valorAno = lerNumero(linha.getCell(COL_ANO));
                    if (valorAno == null) {
                        linhasIgnoradas++;
                        continue;
                    }
                    int ano = valorAno.intValue();

                    Cell celulaNome = linha.getCell(COL_NOME_MUNICIPIO);
                    String nomeMunicipio = celulaNome != null && celulaNome.getCellType() == CellType.STRING
                            ? celulaNome.getStringCellValue().trim() : null;
                    if (nomeMunicipio == null || nomeMunicipio.isEmpty()) {
                        linhasSemMunicipio++;
                        continue;
                    }

                    Double valorCodigo = lerNumero(linha.getCell(COL_CODIGO_MUNICIPIO));
                    Cell celulaUf = linha.getCell(COL_UF);
                    String uf = celulaUf != null && celulaUf.getCellType() == CellType.STRING
                            ? celulaUf.getStringCellValue().trim() : null;

                    Double cn = lerNumero(linha.getCell(COL_NOTA_CN));
                    Double ch = lerNumero(linha.getCell(COL_NOTA_CH));
                    Double lc = lerNumero(linha.getCell(COL_NOTA_LC));
                    Double mt = lerNumero(linha.getCell(COL_NOTA_MT));
                    Double redacao = lerNumero(linha.getCell(COL_NOTA_REDACAO));

                    if (valorCodigo == null || uf == null || uf.length() != 2
                            || cn == null || ch == null || lc == null || mt == null || redacao == null) {
                        linhasIgnoradas++;
                        continue;
                    }

                    Agregacao agregacao = null;
                    for (Agregacao existente : agregacoes) {
                        if (existente.getAno() == ano) {
                            agregacao = existente;
                            break;
                        }
                    }
                    if (agregacao == null) {
                        agregacao = new Agregacao(ano);
                        agregacoes.add(agregacao);
                    }

                    agregacao.adicionar(valorCodigo.intValue(), nomeMunicipio, uf.toUpperCase(),
                            cn, ch, lc, mt, redacao);
                    participantesValidos++;
                }

                System.out.println("  participantes válidos nesta planilha: " + participantesValidos);
            }
        }

        int totalParticipantes = 0;
        System.out.print("Leitura concluída. Planilhas lidas: " + totalPlanilhas + " | Anos encontrados: ");
        for (Agregacao agregacao : agregacoes) {
            System.out.print(agregacao.getAno() + " ");
            totalParticipantes += agregacao.getTotalParticipantes();
        }
        System.out.println("| Participantes válidos: " + totalParticipantes);
        System.out.println("Linhas sem nome de município (null/vazio): " + linhasSemMunicipio);
        System.out.println("Linhas com dados incompletos ou inválidos: " + linhasIgnoradas);
        return agregacoes;
    }

    private Double lerNumero(Cell celula) {
        if (celula == null) {
            return null;
        }
        try {
            if (celula.getCellType() == CellType.NUMERIC) {
                return celula.getNumericCellValue();
            }
            if (celula.getCellType() == CellType.STRING) {
                String texto = celula.getStringCellValue().trim().replace(',', '.');
                return texto.isEmpty() ? null : Double.parseDouble(texto);
            }
        } catch (NumberFormatException e) {
            // Valor inválido: a linha será ignorada.
        }
        return null;
    }
}
