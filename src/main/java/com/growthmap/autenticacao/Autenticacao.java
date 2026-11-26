package com.growthmap.autenticacao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Autenticacao {
    private final JdbcTemplate banco;

    public Autenticacao(JdbcTemplate banco) {
        this.banco = banco;
    }

    public Integer autenticar(String email, String senha) {
        try {
            return banco.queryForObject(
                    "SELECT id FROM usuario WHERE email = ? AND senha = ?",
                    Integer.class, email, senha);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
