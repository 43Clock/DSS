/*
 *  DISCLAIMER: Este código foi criado para discussão e edição durante as aulas práticas de DSS, representando
 *  uma solução em construção. Como tal, não deverá ser visto como uma solução canónica, ou mesmo acabada.
 *  É disponibilizado para auxiliar o processo de estudo. Os alunos são encorajados a testar adequadamente o
 *  código fornecido e a procurar soluções alternativas, à medida que forem adquirindo mais conhecimentos.
 */
package ui;

import business.ISistemaLNFacade;
import business.QrCode;
import business.SistemaLNFacade;

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
            }
        } while (menu.getOpcao()!=0); // A opção 0 é usada para sair do menu.
        System.out.println("Até breve!...");
    }

    private void adicionaPalete(){
        try {
            System.out.println("Inserir Codigo Qr");
            String code = scin.nextLine();
            model.lerCodigoQr(new QrCode(code));
            System.out.println("\nCodigo registado com sucesso!");
        }catch (ArrayIndexOutOfBoundsException|NumberFormatException e){
            System.out.println("\nCodigo Qr Inválido");
        }

    }
}
