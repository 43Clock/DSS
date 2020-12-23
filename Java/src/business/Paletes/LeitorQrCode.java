package business.Paletes;

public class LeitorQrCode {

    /**
     * Método que lê um codigo Qr e faz parse dos seus argumentos
     * @param code Codigo Qr da palete
     * @return Array com o codigo Qr Parsed
     */
    public String[] lerCodigo(QrCode code){
        String s = code.getCode();
        String[] parsed = s.split("/");
        return parsed;
    }

}
