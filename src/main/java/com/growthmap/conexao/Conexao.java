package com.growthmap.conexao;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

public class Conexao {

    private DataSource conexao;

    public Conexao() {
        DriverManagerDataSource driver = new DriverManagerDataSource();

        driver.setUsername(System.getenv("DB_USERNAME"));
        driver.setPassword(System.getenv("DB_PASSWORD"));
        driver.setUrl(System.getenv("DB_URL"));
        driver.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.conexao = driver;
    try {
        driver.getConnection().close();
        System.out.println("Conexao com MySQL estabelecida!");
    } catch (Exception e) {
        e.printStackTrace();
    }
 }

    public DataSource getConexao() {
        return this.conexao;
    }
}
