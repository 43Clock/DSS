package business.Paletes;

import java.util.Objects;

public class Palete {
    private int localizacao;
    private String material;
    private double peso;
    private double preco;
    private Boolean espera;
    private int identificador;
    private QrCode qrCode;

    public Palete(int localizacao, String material, double peso, double preco, Boolean espera, int identificador, QrCode qrCode) {
        this.localizacao = localizacao;
        this.material = material;
        this.peso = peso;
        this.preco = preco;
        this.espera = espera;
        this.identificador = identificador;
        this.qrCode = qrCode;
    }

    public Palete(Palete palete) {
        this.localizacao = palete.getLocalizacao();
        this.material = palete.getMaterial();
        this.peso = palete.getPeso();
        this.preco = palete.getPreco();
        this.espera = palete.getEspera();
        this.identificador = palete.getIdentificador();
        this.qrCode = palete.getQrCode();
    }

    public int getLocalizacao() {
        return localizacao;
    }

    public String getMaterial() {
        return material;
    }

    public double getPeso() {
        return peso;
    }

    public double getPreco() {
        return preco;
    }

    public Boolean getEspera() {
        return espera;
    }

    public int getIdentificador() {
        return identificador;
    }

    public QrCode getQrCode() {
        return qrCode.clone();
    }

    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setEspera(Boolean espera) {
        this.espera = espera;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public void setQrCode(QrCode qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Palete palete = (Palete) o;
        return localizacao == palete.localizacao && Double.compare(palete.peso, peso) == 0 && Double.compare(palete.preco, preco) == 0 &&
                identificador == palete.identificador && Objects.equals(material, palete.material) && Objects.equals(espera, palete.espera) &&
                Objects.equals(qrCode, palete.qrCode);
    }

    public Palete clone() {
        return new Palete(this);
    }

}
