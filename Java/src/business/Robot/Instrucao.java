package business.Robot;

public class Instrucao {
    private String caminho;
    private int iDpalete;

    public Instrucao(String caminho, int iDpalete) {
        this.caminho = caminho;
        this.iDpalete = iDpalete;
    }

    /**
     * Getter do caminho
     * @return Caminho da instrução
     */
    public String getCaminho() {
        return caminho;
    }

    /**
     * Getter do identificador da palete na instrução
     * @return identificador da palete na instrução.
     */
    public int getiDpalete() {
        return iDpalete;
    }

    /**
     * Metodo que a partir do caminho da instrução calcula o destino
     * @return Destino da instrução
     */
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
