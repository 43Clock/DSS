package business;

import data.PaleteDAO;
import data.PrateleiraDAO;

import java.util.*;
import java.util.stream.Collectors;

public class SistemaLNFacade implements ISistemaLNFacade{
    private double[][] localizacoes;
    private Map<Integer, Palete> paletes;
    private Map<Integer, Prateleira> prateleira;
    private Map<Integer, Robot> robot;
    private LeitorQrCode leitorQrCode;


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
        this.robot = new HashMap<>();
        this.leitorQrCode = new LeitorQrCode();
    }

    public Collection<Palete> getPaletes() {
        return new ArrayList<>(this.paletes.values());
    }

    public Collection<Prateleira> getPrateleiras() {
        return new ArrayList<>(this.prateleira.values());
    }

    public Collection<Robot> getRobot() {
        return new ArrayList<>(this.robot.values());
    }

    private String dijkstra(int startVertex,int endVertex) {
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
        return printSolution(startVertex, endVertex, parents);
    }

    private static String printSolution(int startVertex, int endVertex, int[] parents) {
        StringBuilder s = new StringBuilder();
        printPath(endVertex, parents,s);
        s.deleteCharAt(s.length()-1);
        s.deleteCharAt(s.length()-1);
        return s.toString();
    }

    private static void printPath(int currentVertex, int[] parents,StringBuilder s) {
        if (currentVertex == -1)
        {
            return;
        }
        printPath(parents[currentVertex], parents,s);
        s.append(currentVertex).append("->");
    }

    public static void main(String[] args) {
        SistemaLNFacade s = new SistemaLNFacade();
        s.lerCodigoQr(new QrCode("Arroz/39/32"));
        s.prateleira.put(1,new Prateleira(7,1));
        s.robot.put(1,new Robot(1));
        s.comunicaOrdemDeTransporte();
    }

    public void lerCodigoQr(QrCode code) throws NumberFormatException,ArrayIndexOutOfBoundsException {
        Palete novo = this.leitorQrCode.lerCodigo(code);
        this.paletes.put(novo.getIdentificador(), novo);
    }

    public boolean comunicaOrdemDeTransporte(){
        List<Palete> emEspera = this.paletes.values().stream().filter(Palete::getEspera).collect(Collectors.toList());
        if(!emEspera.isEmpty()){
            Palete p = emEspera.get(0);
            if (p.getLocalizacao() == 0) {
                //@TODO caso em que não ha prateleiras disponivies
                Prateleira prateleira = this.prateleira.values().stream().filter(e -> !e.isOcupada()).collect(Collectors.toList()).get(0);
                //@TODO caso em que não ha robots disponivies
                Robot robot = this.robot.values().stream().filter(e -> e.getInstrucao() == null).collect(Collectors.toList()).get(0);
                String s = dijkstra(robot.getLocalizacao(),prateleira.getLocalizacao());
                Instrucao i = new Instrucao(s,p.getIdentificador());
                robot.setInstrucao(i);
                robot.fazIntrucao();
                //@TODO notificar as shits e atualizar prateleira mas nas outras funcoes e atualizar a localizacao da palete e o estado
            }
            //@TODO O outro caso de ir de prateileiras para a zona de entrega
        }
        return true;
    }

}
