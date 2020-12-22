/*
 *  DISCLAIMER: Este código foi criado para discussão e edição durante as aulas práticas de DSS, representando
 *  uma solução em construção. Como tal, não deverá ser visto como uma solução canónica, ou mesmo acabada.
 *  É disponibilizado para auxiliar o processo de estudo. Os alunos são encorajados a testar adequadamente o
 *  código fornecido e a procurar soluções alternativas, à medida que forem adquirindo mais conhecimentos.
 */
package ui;

import Exceptions.*;
import business.ISistemaLNFacade;
import business.Paletes.QrCode;
import business.SistemaLNFacade;

import java.util.List;
import java.util.Scanner;

/**
 * Exemplo de interface em modo texto.
 *
 * @author JFC
 * @version 20201208
 */
public class TextUI {
    // O model tem a 'lógica de negócio'.
    private ISistemaLNFacade model;

    // Menus da aplicação
    private Menu menu;

    // Scanner para leitura
    private Scanner scin;

    /**
     * Construtor.
     *
     * Cria os menus e a camada de negócio.
     */
    public TextUI() {
        // Criar o menu
        String[] opcoes = {
                "Ler e Registar CodigoQR",
                "Adicionar Robots",
                "Comunica Ordem de Transporte",
                "Notifica Recolha",
                "Notifica Entrega",
                "Faz Transporte de Paletes",
                "Listagem de Localizacoes",
                "Requisita Palete"
                };
        this.menu = new Menu(opcoes);
        this.model = new SistemaLNFacade();
        scin = new Scanner(System.in);
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
     */
    public void run() {
        do {
            menu.executa();
            switch (menu.getOpcao()) {
                case 1:
                    adicionaPalete();
                    break;
                case 2:
                    adicionaRobot();
                    break;
                case 3:
                    comunicaOrdemTransporte();
                    break;
                case 4:
                    notificaRecolha();
                    break;
                case 5:
                    notificaEntrega();
                    break;
                case 6:
                    fazTransporteCompleto();
                    break;
                case 7:
                    listagemDeLocalizacoes();
                    break;
                case 8:
                    requisitaPaletes();
                    break;
            }
        } while (menu.getOpcao()!=0); // A opção 0 é usada para sair do menu.
        System.out.println("Até breve!...");
    }

    private void adicionaPalete(){
        try {
            System.out.println("\nInserir Codigo Qr");
            String code = scin.nextLine();
            model.lerCodigoQr(new QrCode(code));
            System.out.println("\nCodigo registado com sucesso!");
        }catch (ArrayIndexOutOfBoundsException|NumberFormatException e){
            System.out.println("\nCodigo Qr Inválido");
        }

    }

    private void adicionaRobot() {
        this.model.criaRobot();
        System.out.println("\nNovo Robot adicionado ao Sistema");
    }

    private void comunicaOrdemTransporte() {
        try {
            int r = model.comunicaOrdemDeTransporte();
            System.out.println("\nInstrucao foi atribuida ao Robot "+ r);
        } catch (PrateleiraIndisponivelException | RobotIndisponivelException | PaletesIndisponiveisException e) {
            System.out.println(e.getMessage());
        }
    }

    public void notificaRecolha() {
        try {
            System.out.println("\nInserir Codigo Robot");
            String code = scin.nextLine();
            model.notificaRecolha(Integer.parseInt(code));
            System.out.println("\nRecolha feita com sucesso!");
        } catch (RobotNaoTemInstrucaoException |ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }catch (NumberFormatException e) {
            System.out.println("Nenhum Input fornecido");
        }
    }

    public void notificaEntrega() {
        try {
            System.out.println("\nInserir Codigo Robot");
            String code = scin.nextLine();
            model.notificaEntrega(Integer.parseInt(code));
            System.out.println("\nEntrega feita com sucesso!");
        } catch (RobotNaoTemInstrucaoException | RobotNaoRecolheuPaleteException | ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Nenhum Input fornecido");
        }
    }

    public void fazTransporteCompleto() {
        try {
            int r = this.model.fazEntregas();
            System.out.println("Instrucao foi atribuida ao Robot " + r);
            System.out.println("Recolha feita com sucesso!");
            System.out.println("Entrega feita com sucesso!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void listagemDeLocalizacoes() {
        List<String> localizacoes = this.model.listagemDeLocalizacao();
        for (String s : localizacoes) {
            System.out.println(s);
        }
    }

    public void requisitaPaletes() {
        try {
            System.out.println("\nInserir Identificador da Palete:");
            String code = scin.nextLine();
            this.model.requisitaPalete(Integer.parseInt(code));
        } catch (PaletesIndisponiveisException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Nenhum Input fornecido");
        }
    }
}
