package business.Robot;

public class Robot {
    private int localizacao;
    private int identificador;
    private Boolean recolhida;
    private Instrucao instrucao;

    public Robot(int identificador) {
        this.localizacao  = 0;
        this.identificador = identificador;
        this.recolhida = false;
        this.instrucao = null;
    }

    public Robot(int localizacao, int identificador, Boolean recolhida, Instrucao instrucao) {
        this.localizacao = localizacao;
        this.identificador = identificador;
        this.recolhida = recolhida;
        this.instrucao = instrucao;
    }

    public int getLocalizacao() {
        return localizacao;
    }

    public int getIdentificador() {
        return identificador;
    }

    public Instrucao getInstrucao() {
        return instrucao;
    }

    public Boolean getRecolhida() {
        return recolhida;
    }

    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public void setInstrucao(Instrucao instrucao) {
        this.instrucao = instrucao;
    }

    public void setRecolhida(Boolean recolhida) {
        this.recolhida = recolhida;
    }

    /**
     * Metodo que remove a instrucao de um robot e altera a sua localização para a final presente na Instrução
     */
    public void acabaInstrucao(){
        this.localizacao = this.instrucao.getDestino();
        this.instrucao = null;
    }


}
