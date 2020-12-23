package business;

import Exceptions.*;
import business.Paletes.QrCode;

import java.util.List;

public interface ISistemaLNFacade {
    /**
     * Método usado para criação de uma nova palete a partir da leitura de um codigo Qr.
     * @param code CodigoQr lido
     * @throws NumberFormatException Exeception para quando um dos parametros do QrCode está incorreto
     * @throws ArrayIndexOutOfBoundsException Exeception para quando o QrCode não tem argumentos suficientes
     */
    void lerCodigoQr(QrCode code);

    /**
     * Metodo que atribui uma instrução a um robot, escolhendo uma palete para transportar, o robot mais proximo desta e a prateleira mais proxima disponivel,
     * ou no caso em que é para remover a palete do armazem, escolhe apenas o robot mais proximo da palete.
     * @return Identificador do robot ao qual foi atribuido a instrução
     * @throws PrateleiraIndisponivelException Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws RobotIndisponivelException Exception para quando não existe nenhum robot disponivel para fazer o transporte
     * @throws PaletesIndisponiveisException Exception para quando não existe nenhuma palete que precisa de ser transportada
     */
    int comunicaOrdemDeTransporte() throws PrateleiraIndisponivelException, RobotIndisponivelException, PaletesIndisponiveisException;

    /**
     * Método que adiciona um novo robot
     */
    void criaRobot();

    /**
     * Metodo que faz com que um robot, caso tenha uma instrução atribuida, recolha a palete e posteriormente notifica o sistema dessa recolha.
     * @param robot Identificador do robot a fazer a recolha
     * @throws RobotNaoTemInstrucaoException Exception para quando o robot não tem nenhuma instrução atribuida.
     * @throws ArrayIndexOutOfBoundsException Exception para quando é fornecido um identificador de robot que não existe
     */
    void notificaRecolha(int robot) throws RobotNaoTemInstrucaoException,ArrayIndexOutOfBoundsException;

    /**
     * Metodo que faz com que um robot, caso tenha uma instrução atribuida, entregue a palete e posteriormente notifica o sistema dessa entrega.
     * @param robot Identificador do robot a fazer a entrega
     * @throws RobotNaoTemInstrucaoException Exception para quando o robot não tem nenhuma instrução atribuida.
     * @throws RobotNaoRecolheuPaleteException Exception para quando o robot ainda não recolheu a palete
     * @throws ArrayIndexOutOfBoundsException Exception para quando é fornecido um identificador de robot que não existe
     */
    void notificaEntrega(int robot) throws RobotNaoTemInstrucaoException, RobotNaoRecolheuPaleteException,ArrayIndexOutOfBoundsException;

    /**
     * Metodo que retorna uma lista com as localizações de todos os robots, paletes e prateleiras.
     * @return Lista de String com todas as localizações.
     */
    List<String> listagemDeLocalizacao();

    /**
     * Método que faz atribuição da instrução a um robot, a recolha e a entrega de uma Palete.
     *
     * @return Identificador do Robot que fez a instrução
     * @throws PaletesIndisponiveisException   Exception para quando não existe nenhuma palete que precisa de ser transportada
     * @throws PrateleiraIndisponivelException Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws RobotIndisponivelException      Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws RobotNaoTemInstrucaoException   Exception para quando o robot não tem nenhuma instrução atribuida
     * @throws RobotNaoRecolheuPaleteException Exception para quando o robot ainda não recolheu a palete
     */
    int fazEntregas() throws PaletesIndisponiveisException, PrateleiraIndisponivelException, RobotIndisponivelException, RobotNaoTemInstrucaoException, RobotNaoRecolheuPaleteException;

    /**
     * Metodo que faz a requisição de uma palete
     * @param i Identificador da palete a requisitar
     * @throws PaletesIndisponiveisException Exception para o caso de não existir palete com o identificador dado
     */
    void requisitaPalete(int i) throws PaletesIndisponiveisException;
}
