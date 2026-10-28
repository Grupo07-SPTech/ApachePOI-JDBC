package com.growthmap.processamento;

import com.growthmap.banco.DesempenhoDao;
import com.growthmap.leitura.LeitorEnem;
import com.growthmap.modelo.Acumulador;
import com.growthmap.modelo.Agregacao;
import com.growthmap.logs.LogSistema;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProcessamentoEnem {
    private final JdbcTemplate banco;
    private final LogSistema log;

    public ProcessamentoEnem(JdbcTemplate banco, LogSistema log) {
        this.banco = banco;
        this.log = log;
    }

    public void processar(int idUsuario, String nomeArquivo) throws Exception {
        log.registrar(idUsuario, "INICIO_LEITURA: " + nomeArquivo);
        List<Agregacao> agregacoes = new LeitorEnem().extrairDados(nomeArquivo);

        if (agregacoes.isEmpty()) {
            log.registrar(idUsuario, "LEITURA_SEM_DADOS");
            System.out.println("Nenhum dado foi encontrado na planilha.");
            return;
        }
        log.registrar(idUsuario, "LEITURA_CONCLUIDA");

        DesempenhoDao dao = new DesempenhoDao(banco);
        for (Agregacao agregacao : agregacoes) {
            int ano = agregacao.getAno();
            if (dao.existeAno(ano)) {
                log.registrar(idUsuario, "ANO_JA_EXISTE: " + ano);
                System.out.println("O ano " + ano + " já existe no banco.");
                return;
            }
        }

        List<Acumulador> municipiosNovos = buscarMunicipiosNovos(dao, agregacoes);
        dao.inserirMunicipios(municipiosNovos);
        log.registrar(idUsuario, "MUNICIPIOS_INSERIDOS: " + municipiosNovos.size());

        for (Agregacao agregacao : agregacoes) {
            int ano = agregacao.getAno();
            dao.inserirRegional(ano, agregacao.getMunicipios());
            dao.inserirEstadual(ano, agregacao.getEstados());
            log.registrar(idUsuario, "ANO_DOS_DADOS_INSERIDOS: " + ano);
            log.registrar(idUsuario, "PARTICIPANTES_VALIDOS_INSERIDOS: " + agregacao.getTotalParticipantes());
            System.out.println("Dados de " + ano + " inseridos com sucesso.");
        }

        log.registrar(idUsuario, "PROCESSAMENTO_CONCLUIDO");
    }

    private List<Acumulador> buscarMunicipiosNovos(DesempenhoDao dao, List<Agregacao> agregacoes) {
        List<Integer> codigosBanco = dao.buscarCodigosCadastrados();
        List<Acumulador> municipiosNovos = new ArrayList<>();

        for (Agregacao agregacao : agregacoes) {
            for (Acumulador municipio : agregacao.getMunicipios()) {
                boolean jaAdicionado = false;
                for (Acumulador municipioAdicionado : municipiosNovos) {
                    if (municipioAdicionado.getCodigo() == municipio.getCodigo()) {
                        jaAdicionado = true;
                        break;
                    }
                }
                if (!codigosBanco.contains(municipio.getCodigo()) && !jaAdicionado) {
                    municipiosNovos.add(municipio);
                }
            }
        }
        return municipiosNovos;
    }
}
