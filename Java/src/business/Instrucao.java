package business;

public class Instrucao {
    private String caminho;
    private int iDpalete;

    public Instrucao(String caminho, int iDpalete) {
        this.caminho = caminho;
        this.iDpalete = iDpalete;
    }

    public String getCaminho() {
        return caminho;
    }

    public int getiDpalete() {
        return iDpalete;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public void setiDpalete(int iDpalete) {
        this.iDpalete = iDpalete;
    }
}
