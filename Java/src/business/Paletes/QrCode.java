package business.Paletes;

import java.util.Objects;

public class QrCode {
    private String code;


    public QrCode(String code) {
        this.code = code;
    }

    public QrCode(QrCode qrCode) {
        this.code = qrCode.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QrCode qrCode = (QrCode) o;
        return Objects.equals(code, qrCode.code);
    }

    public QrCode clone() {
        return new QrCode(this);
    }

}

