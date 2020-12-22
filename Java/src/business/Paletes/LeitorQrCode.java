package business.Paletes;

import business.Paletes.Palete;
import business.Paletes.QrCode;

public class LeitorQrCode {

    public Palete lerCodigo(QrCode code, int id) throws NumberFormatException{
        String s = code.getCode();
        String[] parsed = s.split("/");
        String nome = parsed[0];
        double peso = Double.parseDouble(parsed[1]);
        double preco = Double.parseDouble(parsed[2]);
        return new Palete(0, nome, peso, preco, true, id,code);
    }

}
