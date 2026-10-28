package com.growthmap.modelo;

import java.util.ArrayList;
import java.util.List;

public class Agregacao {

    private int ano;
    private List<Acumulador> municipios = new ArrayList<>();
    private List<Acumulador> estados = new ArrayList<>();
    private int totalParticipantes = 0;

    public Agregacao(int ano) {
        this.ano = ano;
    }

    public int getAno() {
        return ano;
    }

    public void adicionar(int coMunicipio, String nomeMunicipio, String uf, double cn, double ch, double lc, double mt, double redacao) {
        buscarOuCriarMunicipio(coMunicipio, nomeMunicipio, uf).adicionar(cn, ch, lc, mt, redacao);
        buscarOuCriarEstado(uf).adicionar(cn, ch, lc, mt, redacao);
        totalParticipantes++;
    }

    public List<Acumulador> getMunicipios() {
        return municipios;
    }

    public List<Acumulador> getEstados() {
        return estados;
    }

    public int getTotalParticipantes() {
        return totalParticipantes;
    }

    private Acumulador buscarOuCriarMunicipio(int coMunicipio, String nome, String uf) {
        for (Acumulador municipio : municipios) {
            if (municipio.getCodigo() == coMunicipio) {
                return municipio;
            }
        }

        Acumulador novo = new Acumulador(coMunicipio, nome, uf);
        municipios.add(novo);
        return novo;
    }

    private Acumulador buscarOuCriarEstado(String uf) {
        for (Acumulador estado : estados) {
            if (estado.getUf().equals(uf)) {
                return estado;
            }
        }
        Acumulador novo = new Acumulador(0, "", uf);
        estados.add(novo);
        return novo;
    }
}
