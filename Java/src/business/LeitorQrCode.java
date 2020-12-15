package business;

public class LeitorQrCode {
    private int counter;

    public LeitorQrCode() {
        this.counter = 1;
    }

    public Palete lerCodigo(QrCode code) throws NumberFormatException{
        String s = code.getCode();
        String[] parsed = s.split("/");
        try {
            String nome = parsed[0];
            double peso = Double.parseDouble(parsed[1]);
            double preco = Double.parseDouble(parsed[2]);
            return new Palete(0, nome, peso, preco, true, this.counter,code);
        }finally {
            this.counter++;
        }
    }

}
