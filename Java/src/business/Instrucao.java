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

    public int getDestino() {
        String[] splited= this.caminho.split("->");
        return Integer.parseInt(splited[splited.length-1]);
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public void setiDpalete(int iDpalete) {
        this.iDpalete = iDpalete;
    }
}
