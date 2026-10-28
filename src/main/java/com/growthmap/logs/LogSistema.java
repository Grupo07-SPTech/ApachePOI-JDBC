package com.growthmap.logs;

import org.springframework.jdbc.core.JdbcTemplate;

public class LogSistema {
    private final JdbcTemplate banco;

    public LogSistema(JdbcTemplate banco) {
        this.banco = banco;
    }

    public void registrar(int idUsuario, String acao) {
        banco.update(
                "INSERT INTO logs_sistema (id_usuario, acao, dataHora) VALUES (?, ?, NOW())",
                idUsuario, acao);
    }
}
