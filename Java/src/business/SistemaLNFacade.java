package business;

import Exceptions.*;
import business.Armazenamento.Prateleira;
import business.Paletes.LeitorQrCode;
import business.Paletes.Palete;
import business.Paletes.QrCode;
import business.Robot.Instrucao;
import business.Robot.Robot;
import data.PaleteDAO;
import data.PrateleiraDAO;
import data.RobotDAO;

import java.util.*;
import java.util.stream.Collectors;

public class SistemaLNFacade implements ISistemaLNFacade{
    private double[][] localizacoes;
    private Map<Integer, Palete> paletes;
    private Map<Integer, Prateleira> prateleira;
    private Map<Integer, Robot> robot;
    private LeitorQrCode leitorQrCode;

    private static final int ZONA_ENTREGA = 11;
    private static final int ZONA_RECECAO = 0;

    public SistemaLNFacade() {
        this.localizacoes = new double[][]
                            {{0.0,2.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
                             {2.0,0.0,5.0,0.0,0.0,0.0,5.0,0.0,0.0,0.0,0.0,0.0},
                             {0.0,5.0,0.0,5.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
                             {0.0,0.0,5.0,0.0,5.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
                             {0.0,0.0,0.0,5.0,0.0,5.0,0.0,0.0,0.0,0.0,0.0,0.0},
                             {0.0,0.0,0.0,0.0,5.0,0.0,0.0,0.0,0.0,0.0,0.0,2.5},
                             {0.0,5.0,0.0,0.0,0.0,0.0,0.0,5.0,0.0,0.0,0.0,0.0},
                             {0.0,0.0,0.0,0.0,0.0,0.0,5.0,0.0,5.0,0.0,0.0,0.0},
                             {0.0,0.0,0.0,0.0,0.0,0.0,0.0,5.0,0.0,5.0,0.0,0.0},
                             {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,5.0,0.0,5.0,0.0},
                             {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,5.0,0.0,2.5},
                             {0.0,0.0,0.0,0.0,0.0,2.5,0.0,0.0,0.0,0.0,2.5,0.0}};
        this.paletes = PaleteDAO.getInstance();
        this.prateleira = PrateleiraDAO.getInstance();
        this.robot = RobotDAO.getInstance();
        this.leitorQrCode = new LeitorQrCode();
    }

    /**
     * Metodo que devolve todas as paletes registadas.
     * @return todas as paletes registadas
     */
    public Collection<Palete> getPaletes() {
        return new ArrayList<>(this.paletes.values());
    }

    /**
     * Metodo que devolve todas as prateleiras registadas.
     * @return todas as prateleiras registadas
     */
    public Collection<Prateleira> getPrateleiras() {
        return new ArrayList<>(this.prateleira.values());
    }

    /**
     * Metodo que devolve todos os robots registados.
     * @return todos os robots registados
     */
    public Collection<Robot> getRobot() {
        return new ArrayList<>(this.robot.values());
    }

    /**
     * Método que use o algoritmos de dijkstra para calcula o caminho mas curto entre duas localizações
     * @param startVertex Localização inicial
     * @param endVertex Localização final
     * @return String que contem o caminho que o robot tem de percorrer
     */
    private String calculaPercurso(int startVertex, int endVertex) {
        int nVertices = this.localizacoes[0].length;
        double[] shortestDistances = new double[nVertices];
        boolean[] added = new boolean[nVertices];
        for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
            shortestDistances[vertexIndex] = Double.MAX_VALUE;
            added[vertexIndex] = false;
        }
        shortestDistances[startVertex] = 0;
        int[] parents = new int[nVertices];
        parents[startVertex] = -1;
        for (int i = 1; i < nVertices; i++) {
            int nearestVertex = -1;
            double shortestDistance = Double.MAX_VALUE;
            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
                if (!added[vertexIndex] && shortestDistances[vertexIndex] < shortestDistance) {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }
            added[nearestVertex] = true;
            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
                double edgeDistance = this.localizacoes[nearestVertex][vertexIndex];
                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance + edgeDistance;
                }
            }
        }
        return printSolution(endVertex, parents);
    }

    /**
     * Métodos auxiliar para criação do caminho mais curto
     */
    private static String printSolution(int endVertex, int[] parents) {
        StringBuilder s = new StringBuilder();
        printPath(endVertex, parents,s);
        s.deleteCharAt(s.length()-1);
        s.deleteCharAt(s.length()-1);
        return s.toString();
    }

    /**
     * Métodos auxiliar para criação do caminho mais curto
     */
    private static void printPath(int currentVertex, int[] parents,StringBuilder s) {
        if (currentVertex == -1)
        {
            return;
        }
        printPath(parents[currentVertex], parents,s);
        s.append(currentVertex).append("->");
    }

    /**
     * Método usado para criação de uma nova palete a partir da leitura de um codigo Qr.
     * @param code CodigoQr lido
     * @throws NumberFormatException Exeception para quando um dos parametros do QrCode está incorreto
     * @throws ArrayIndexOutOfBoundsException Exeception para quando o QrCode não tem argumentos suficientes
     */
    public void lerCodigoQr(QrCode code) throws NumberFormatException,ArrayIndexOutOfBoundsException {
        List<Palete> paletes = this.paletes.values().stream().sorted(Comparator.comparing(Palete::getIdentificador)).collect(Collectors.toList());
        Palete novo;
        String[] s = this.leitorQrCode.lerCodigo(code);
        double peso = Double.parseDouble(s[1]);
        double preco = Double.parseDouble(s[2]);
        if(this.paletes.isEmpty()){
            novo = new Palete(0, s[0], peso, preco, true, 1,code);
        }
        else {
            novo = new Palete(0, s[0], peso, preco, true, paletes.get(paletes.size()-1).getIdentificador()+1,code);
        }
        this.paletes.put(novo.getIdentificador(), novo);
    }

    /**
     * Método que adiciona um novo robot
     */
    public void criaRobot() {
        this.robot.put(robot.size()+1, new Robot(robot.size()+1));
    }

    /**
     * Método que faz atribuição da instrução a um robot, a recolha e a entrega de uma Palete.
     * @return Identificador do Robot que fez a instrução
     * @throws PaletesIndisponiveisException Exception para quando não existe nenhuma palete que precisa de ser transportada
     * @throws PrateleiraIndisponivelException Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws RobotIndisponivelException Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws RobotNaoTemInstrucaoException Exception para quando o robot não tem nenhuma instrução atribuida
     * @throws RobotNaoRecolheuPaleteException Exception para quando o robot ainda não recolheu a palete
     */
    public int fazEntregas() throws PaletesIndisponiveisException, PrateleiraIndisponivelException, RobotIndisponivelException, RobotNaoTemInstrucaoException, RobotNaoRecolheuPaleteException {
        int robot = comunicaOrdemDeTransporte();
        notificaRecolha(robot);
        notificaEntrega(robot);
        return robot;
    }

    /**
     * Metodo que escolhe o robot mais perto de uma certa localização
     * @param localizacao Localizacao para onde o robot tem de ir
     * @return Identificador do robot escolhido
     */
    private int escolheRobot(int localizacao) {
        List<Robot> robots = this.robot.values().stream().filter(a -> a.getInstrucao() == null).collect(Collectors.toList());
        List<Map.Entry<Integer, Double>> distancias = new ArrayList<>();
        for (Robot r : robots) {
            String path = calculaPercurso(r.getLocalizacao(), localizacao);
            String[] splited = path.split("->");
            int[] vertices = Arrays.stream(splited).mapToInt(Integer::parseInt).toArray();
            double distance = 0.0;
            for (int i = 0; i < vertices.length-2; i++) {
                distance += this.localizacoes[i][i+1];
            }
            distancias.add(new AbstractMap.SimpleEntry<>(r.getIdentificador(), distance));
        }
        List<Map.Entry<Integer, Double>> ordered = distancias.stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        return ordered.get(0).getKey();
    }

    /**
     * Metodo que escolhe a prateleira não ocupada que esta mais perto da Zona de Receção.
     * @return Identificador da prateleira escolhida
     */
    private int escolhePrateleira() {
        //Lista para ver os destinos de todos os robots para n haver robots a colocar paletes nas mesmas prateleiras
        List<Integer> destinosRobots = this.robot.values().stream().filter(a->a.getInstrucao()!=null).map(a->a.getInstrucao().getDestino()).collect(Collectors.toList());
        List<Prateleira> prateleiras = this.prateleira.values().stream().filter(a -> !a.isOcupada() && !destinosRobots.contains(a.getLocalizacao())).collect(Collectors.toList());
        List<Map.Entry<Integer, Double>> distancias = new ArrayList<>();
        for (Prateleira p : prateleiras) {
            String path = calculaPercurso(0, p.getLocalizacao());
            String[] splited = path.split("->");
            int[] vertices = Arrays.stream(splited).mapToInt(Integer::parseInt).toArray();
            double distance = 0.0;
            for (int i = 0; i < vertices.length-2; i++) {
                distance += this.localizacoes[i][i+1];
            }
            distancias.add(new AbstractMap.SimpleEntry<>(p.getIdentificador(), distance));
        }
        List<Map.Entry<Integer, Double>> ordered = distancias.stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        return ordered.get(0).getKey();
    }

    /**
      * @return Lista de todas as poletes em espera, por ordem do identificador.
     */
    private List<Palete> emEspera() {
        return this.paletes.values().stream().filter(Palete::getEspera).sorted(Comparator.comparing(Palete::getIdentificador)).collect(Collectors.toList());
    }

    /**
     * Método que retorna a Prateleira onde está guardada uma certa palete
     * @param palete Identificador da palete
     * @return Prateleira onde está a guardada a palete.
     */
    private Prateleira getPrateleira(int palete) {
        return this.prateleira.values().stream().filter(Prateleira::isOcupada).filter(e -> e.getPalete().getIdentificador() == palete).collect(Collectors.toList()).get(0);
    }

    /**
     * Metodo que atribui uma instrução a um robot, escolhendo uma palete para transportar, o robot mais proximo desta e a prateleira mais proxima disponivel,
     * ou no caso em que é para remover a palete do armazem, escolhe apenas o robot mais proximo da palete.
     * @return Identificador do robot ao qual foi atribuido a instrução
     * @throws PrateleiraIndisponivelException Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws RobotIndisponivelException Exception para quando não existe nenhuma prateleira para armazenar as paletes
     * @throws PaletesIndisponiveisException Exception para quando não existe nenhuma palete que precisa de ser transportada
     */
    public int comunicaOrdemDeTransporte() throws PrateleiraIndisponivelException, RobotIndisponivelException, PaletesIndisponiveisException {
        List<Palete> emEspera = emEspera();
        if(!emEspera.isEmpty()){
            Palete p = emEspera.get(0);
            if (p.getLocalizacao() == ZONA_RECECAO) {
                List<Prateleira> prateleiras = this.prateleira.values().stream().filter(e -> !e.isOcupada()).collect(Collectors.toList());
                if (prateleiras.size() == 0) {
                    throw new PrateleiraIndisponivelException("\nNão Existem Prateleiras Disponiveis\n");
                }
                Prateleira prateleira = this.prateleira.get(escolhePrateleira());
                List<Robot> robots = this.robot.values().stream().filter(e -> e.getInstrucao() == null).collect(Collectors.toList());
                if (robots.size() == 0) {
                    throw new RobotIndisponivelException("\nNão Existem Robots Disponiveis\n");
                }
                Robot robot = this.robot.get(escolheRobot(ZONA_RECECAO));
                String res;
                if(robot.getLocalizacao() == ZONA_RECECAO) {
                    res = calculaPercurso(robot.getLocalizacao(), prateleira.getLocalizacao());
                }
                else {
                    String s1 = calculaPercurso(robot.getLocalizacao(),ZONA_RECECAO);
                    String s2 = calculaPercurso(ZONA_RECECAO, prateleira.getLocalizacao());
                    res = s1 + "->" + s2;
                }
                Instrucao i = new Instrucao(res,p.getIdentificador());
                robot.setInstrucao(i);
                p.setEspera(false);
                this.paletes.put(p.getIdentificador(), p);
                this.robot.put(robot.getIdentificador(), robot);
                return robot.getIdentificador();
            }
            else {
                Prateleira prateleira = getPrateleira(p.getIdentificador());
                List<Robot> robots = this.robot.values().stream().filter(e -> e.getInstrucao() == null).collect(Collectors.toList());
                if (robots.size() == 0) {
                    throw new RobotIndisponivelException("\nNão Existem Robots Disponiveis\n");
                }
                Robot robot = this.robot.get(escolheRobot(prateleira.getLocalizacao()));
                String res;
                if (robot.getLocalizacao() == prateleira.getLocalizacao()) {
                    res = calculaPercurso(robot.getLocalizacao(), ZONA_ENTREGA);
                } else {
                    String s1 = calculaPercurso(robot.getLocalizacao(), prateleira.getLocalizacao());
                    String s2 = calculaPercurso(prateleira.getLocalizacao(),ZONA_ENTREGA);
                    res = s1 + "->" + s2;
                }
                Instrucao i = new Instrucao(res, p.getIdentificador());
                robot.setInstrucao(i);
                p.setEspera(false);
                this.paletes.put(p.getIdentificador(), p);
                this.robot.put(robot.getIdentificador(), robot);
                return robot.getIdentificador();
            }
        }
        else {
            throw new PaletesIndisponiveisException("\nNão existem Paletes Disponiveis\n");
        }
    }

    /**
     * Metodo que faz com que um robot, caso tenha uma instrução atribuida, recolha a palete e posteriormente notifica o sistema dessa recolha.
     * @param robot Identificador do robot a fazer a recolha
     * @throws RobotNaoTemInstrucaoException Exception para quando o robot não tem nenhuma instrução atribuida.
     * @throws ArrayIndexOutOfBoundsException Exception para quando é fornecido um identificador de robot que não existe
     */
    public void notificaRecolha(int robot) throws RobotNaoTemInstrucaoException,ArrayIndexOutOfBoundsException {
        Robot r = this.robot.get(robot);
        if (robot > this.robot.size()) throw new ArrayIndexOutOfBoundsException("Não existe Robot com identificador " + robot +".");
        if (r.getInstrucao() != null) {
            Palete p = this.paletes.get(r.getInstrucao().getiDpalete());
            if (p.getLocalizacao() != ZONA_RECECAO) {
                Prateleira pr = this.prateleira.values().stream().filter(Prateleira::isOcupada).filter(a -> (a.getPalete().getLocalizacao() == p.getLocalizacao())).collect(Collectors.toList()).get(0);
                pr.setPalete(null);
                pr.setOcupada(false);
                this.prateleira.put(pr.getIdentificador(), pr);
            }
            r.setRecolhida(true);
            r.setLocalizacao(p.getLocalizacao());
            this.robot.put(r.getIdentificador(), r);
        }
        else {
            throw new RobotNaoTemInstrucaoException("\nRobot não tem instrucao atribuida\n");
        }
    }

    /**
     * Metodo que faz com que um robot, caso tenha uma instrução atribuida, entregue a palete e posteriormente notifica o sistema dessa entrega.
     * @param robot Identificador do robot a fazer a entrega
     * @throws RobotNaoTemInstrucaoException Exception para quando o robot não tem nenhuma instrução atribuida.
     * @throws RobotNaoRecolheuPaleteException Exception para quando o robot ainda não recolheu a palete
     * @throws ArrayIndexOutOfBoundsException Exception para quando é fornecido um identificador de robot que não existe
     */
    public void notificaEntrega(int robot) throws RobotNaoTemInstrucaoException, RobotNaoRecolheuPaleteException,ArrayIndexOutOfBoundsException {
        Robot r = this.robot.get(robot);
        if (robot > this.robot.size()) throw new ArrayIndexOutOfBoundsException("Não existe Robot com identificador " + robot +".");
        if (!r.getRecolhida()) throw new RobotNaoRecolheuPaleteException("\nRobot ainda não fez a recolha da palete\n");
        if (r.getInstrucao() != null) {
            Palete p = this.paletes.get(r.getInstrucao().getiDpalete());
            if (p.getLocalizacao() == ZONA_RECECAO) {
                Prateleira prateleira = this.prateleira.values().stream().filter(a -> (a.getLocalizacao() == r.getInstrucao().getDestino())).collect(Collectors.toList()).get(0);
                prateleira.setPalete(p);
                prateleira.setOcupada(true);
                p.setLocalizacao(r.getInstrucao().getDestino());
                this.paletes.put(p.getIdentificador(), p);
                this.prateleira.put(prateleira.getIdentificador(), prateleira);
            } else {
                this.paletes.remove(p.getIdentificador());
            }
            r.setRecolhida(false);
            r.acabaInstrucao();
            this.robot.put(r.getIdentificador(), r);
        }
        else {
            throw new RobotNaoTemInstrucaoException("\nRobot não tem instrucao atribuida\n");
        }
    }

    /**
     * Metodo que retorna uma lista com as localizações de todos os robots, paletes e prateleiras.
     * @return Lista de String com todas as localizações.
     */
    public List<String> listagemDeLocalizacao() {
        List<String> res = new ArrayList<>();
        Collection<Robot> robots = this.robot.values().stream().sorted(Comparator.comparing(Robot::getIdentificador)).collect(Collectors.toList());
        Collection<Prateleira> prateleiras = this.prateleira.values().stream().sorted(Comparator.comparing(Prateleira::getIdentificador)).collect(Collectors.toList());
        Collection<Palete> paletes = this.paletes.values().stream().sorted(Comparator.comparing(Palete::getIdentificador)).collect(Collectors.toList());
        for (Palete r : paletes) {
            StringBuilder s = new StringBuilder();
            s.append("Palete ").append(r.getIdentificador()).append(" -> ").append(r.getLocalizacao());
            res.add(s.toString());
        }
        for (Prateleira r : prateleiras) {
            StringBuilder s = new StringBuilder();
            s.append("Prateleira ").append(r.getIdentificador()).append(" -> ").append(r.getLocalizacao());
            res.add(s.toString());
        }
        for (Robot r : robots) {
            StringBuilder s = new StringBuilder();
            s.append("Robot ").append(r.getIdentificador()).append(" -> ").append(r.getLocalizacao());
            res.add(s.toString());
        }
        return res;
    }

    /**
     * Metodo que faz a requisição de uma palete
     * @param i Identificador da palete a requisitar
     * @throws PaletesIndisponiveisException Exception para o caso de não existir palete com o identificador dado
     */
    public void requisitaPalete(int i) throws PaletesIndisponiveisException{
        Palete p = this.paletes.get(i);
        if(p == null) throw new PaletesIndisponiveisException("Não existe palete com identificador "+i);
        p.setEspera(true);
        this.paletes.put(p.getIdentificador(),p);
    }
}
