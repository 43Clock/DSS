package business;

public class Robot {
    private int localizacao;
    private int identificador;
    private Instrucao instrucao;

    public Robot(int identificador) {
        this.localizacao  = 0;
        this.identificador = identificador;
        this.instrucao = null;
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

    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public void setInstrucao(Instrucao instrucao) {
        this.instrucao = instrucao;
    }

    public void fazIntrucao(){
        String[] splited= this.instrucao.getCaminho().split("->");
        int local = Integer.parseInt(splited[splited.length-1]);
        this.localizacao = local;
        this.instrucao = null;
    }
}
