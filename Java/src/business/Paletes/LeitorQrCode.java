package business.Paletes;

import business.Paletes.Palete;
import business.Paletes.QrCode;

public class LeitorQrCode {

    public String[] lerCodigo(QrCode code) throws NumberFormatException{
        String s = code.getCode();
        String[] parsed = s.split("/");
        return parsed;
    }

}
