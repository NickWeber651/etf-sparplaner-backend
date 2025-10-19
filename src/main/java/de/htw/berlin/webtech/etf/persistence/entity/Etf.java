package de.htw.berlin.webtech.etf.persistence.entity;

public class Etf {
    private Long id;
    private String name;
    private String isin;
    private double ter;

    public Etf(Long id, String name, String isin, double ter) {
        this.id = id;
        this.name = name;
        this.isin = isin;
        this.ter = ter;
    }

    public Etf() {} // leerer Konstruktor (wichtig für später)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public double getTer() {
        return ter;
    }

    public void setTer(double ter) {
        this.ter = ter;
    }
}