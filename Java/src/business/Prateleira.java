package business;

import java.util.Objects;

public class Prateleira {
    private int localizacao;
    private Palete palete;
    private boolean ocupada;
    private int identificador;

    public Prateleira(int localizacao, int identificador) {
        this.localizacao = localizacao;
        this.identificador = identificador;
        this.palete = null;
        this.ocupada = false;
    }

    public Prateleira(int localizacao, Palete palete, boolean ocupada, int identificador) {
        this.localizacao = localizacao;
        this.palete = palete;
        this.ocupada = ocupada;
        this.identificador = identificador;
    }

    public int getLocalizacao() {
        return localizacao;
    }

    public Palete getPalete() {
        return palete.clone();
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public int getIdentificador() {
        return identificador;
    }

    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }

    public void setPalete(Palete palete) {
        this.palete = palete.clone();
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prateleira that = (Prateleira) o;
        return localizacao == that.localizacao && ocupada == that.ocupada && identificador == that.identificador && Objects.equals(palete, that.palete);
    }

}
