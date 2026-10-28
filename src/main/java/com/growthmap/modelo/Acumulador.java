package com.growthmap.modelo;

public class Acumulador {

    private int codigo;
    private String nome;
    private String uf;

    private double somaCn;
    private double somaCh;
    private double somaLc;
    private double somaMt;
    private double somaRedacao;
    private int quantidade;

    public Acumulador(int codigo, String nome, String uf) {
        this.codigo = codigo;
        this.nome = nome;
        this.uf = uf;
    }

    public void adicionar(double cn, double ch, double lc, double mt, double redacao) {
        somaCn += cn;
        somaCh += ch;
        somaLc += lc;
        somaMt += mt;
        somaRedacao += redacao;
        quantidade++;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getUf() {
        return uf;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public Double getMediaCn() {
        return media(somaCn);
    }

    public Double getMediaCh() {
        return media(somaCh);
    }

    public Double getMediaLc() {
        return media(somaLc);
    }

    public Double getMediaMt() {
        return media(somaMt);
    }

    public Double getMediaRedacao() {
        return media(somaRedacao);
    }

    private Double media(double soma) {
        return Math.round(soma / quantidade * 100.0) / 100.0;
    }
}
