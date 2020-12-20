package business;

import Exceptions.*;

import java.util.List;

public interface ISistemaLNFacade {
    void lerCodigoQr(QrCode code);
    void comunicaOrdemDeTransporte() throws PrateleiraIndisponivelException, RobotIndisponivelException, PaletesIndisponiveisException;
    void criaRobot();
    void notificaRecolha(int robot) throws RobotNaoTemInstrucaoException,ArrayIndexOutOfBoundsException;
    void notificaEntrega(int robot) throws RobotNaoTemInstrucaoException, RobotNaoRecolheuPaleteException,ArrayIndexOutOfBoundsException;
    List<String> listagemDeLocalizacao();

}
