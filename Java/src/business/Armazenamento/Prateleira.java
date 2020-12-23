package business.Armazenamento;

import business.Paletes.Palete;

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

    /**
     * Getter da localização
     * @return Localização da Prateleira
     */
    public int getLocalizacao() {
        return localizacao;
    }

    /**
     * Getter da Palete
     * @return Palete que esta da Prateleira
     */
    public Palete getPalete() {
        return palete;
    }

    /**
     * Getter da Ocupacao
     * @return Boolean que indica se esta ou não ocupada
     */
    public boolean isOcupada() {
        return ocupada;
    }

    /**
     * Getter do Identificador
     * @return Identificador da Prateleira
     */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Setter da Localização
     * @param localizacao Nova localização
     */
    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }

    /**
     * Setter da Palete
     * @param localizacao Nova Palete
     */
    public void setPalete(Palete palete) {
        this.palete = palete;
    }

    /**
     * Setter da Ocupaçao
     * @param localizacao Nova Ocupação
     */
    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    /**
     * Setter do Identificador
     * @param localizacao Novo Identificador
     */
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
