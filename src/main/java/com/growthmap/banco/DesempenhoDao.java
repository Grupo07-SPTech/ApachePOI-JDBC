package com.growthmap.banco;

import com.growthmap.modelo.Acumulador;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DesempenhoDao {

    private final String sqlContarRegional =
            "SELECT COUNT(*) FROM desempenho_regional WHERE ano = ?";

    private final String sqlContarEstadual =
            "SELECT COUNT(*) FROM desempenho_estadual WHERE ano = ?";

    private final String sqlCodigosMunicipios =
            "SELECT co_municipio FROM municipio";

    private final String sqlInserirMunicipio = """
            INSERT INTO municipio
            (co_municipio, nome_municipio, uf)
            VALUES (?, ?, ?)
            """;

    private final String sqlInserirRegional = """
            INSERT INTO desempenho_regional
            (co_municipio, ano, media_cn, media_ch, media_lc, media_mt, media_redacao, qtd_participantes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private final String sqlInserirEstadual = """
            INSERT INTO desempenho_estadual
            (uf, ano, media_cn, media_ch, media_lc, media_mt, media_redacao, qtd_participantes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private final JdbcTemplate template;

    public DesempenhoDao(JdbcTemplate template) {
        this.template = template;
    }

    public boolean existeAno(int ano) {
        return contar(sqlContarRegional, ano) > 0 || contar(sqlContarEstadual, ano) > 0;
    }

    private int contar(String sql, int ano) {
        Integer total = template.queryForObject(sql, Integer.class, ano);
        return total == null ? 0 : total;
    }

    public List<Integer> buscarCodigosCadastrados() {
        return template.queryForList(sqlCodigosMunicipios, Integer.class);
    }

    public void inserirMunicipios(List<Acumulador> municipios) {
        if (municipios.isEmpty()) {
            return;
        }
        for (Acumulador municipio : municipios) {
            template.update(sqlInserirMunicipio,
                    municipio.getCodigo(), municipio.getNome(), municipio.getUf());
        }
    }

    public void inserirRegional(int ano, List<Acumulador> municipios) {
        for (Acumulador municipio : municipios) {
            template.update(sqlInserirRegional,
                    municipio.getCodigo(), ano, municipio.getMediaCn(), municipio.getMediaCh(),
                    municipio.getMediaLc(), municipio.getMediaMt(), municipio.getMediaRedacao(),
                    municipio.getQuantidade());
        }
    }

    public void inserirEstadual(int ano, List<Acumulador> estados) {
        for (Acumulador estado : estados) {
            template.update(sqlInserirEstadual,
                    estado.getUf(), ano, estado.getMediaCn(), estado.getMediaCh(),
                    estado.getMediaLc(), estado.getMediaMt(), estado.getMediaRedacao(),
                    estado.getQuantidade());
        }
    }
}
