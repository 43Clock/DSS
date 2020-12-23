/*
 *  DISCLAIMER: Este código foi criado para discussão e edição durante as aulas práticas de DSS, representando
 *  uma solução em construção. Como tal, não deverá ser visto como uma solução canónica, ou mesmo acabada.
 *  É disponibilizado para auxiliar o processo de estudo. Os alunos são encorajados a testar adequadamente o
 *  código fornecido e a procurar soluções alternativas, à medida que forem adquirindo mais conhecimentos.
 */
package ui;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta classe implementa um menu em modo texto.
 *
 * @author Josá Creissac Campos
 * @version v2.2 (20201208)
 */
public class Menu {
    // Varíavel de classe para suportar leitura
    private static Scanner is = new Scanner(System.in);
    // variáveis de instância
    private List<String> opcoes;
    private int op;

    /**
     * Constructor for objects of class Menu
     */
    public Menu(String[] opcoes) {
        this.opcoes = Arrays.asList(opcoes);
        this.op = 0;
    }

    /**
     * Método para apresentar o menu e ler uma opção.
     */
    public void executa() {
        do {
            showMenu();
            this.op = lerOpcao();
        } while (this.op == -1);
    }

    /** Apresentar o menu */
    private void showMenu() {
        System.out.println("\n *** Menu *** ");
        for (int i=0; i<this.opcoes.size(); i++) {
            System.out.print(i+1);
            System.out.print(" - ");
            System.out.println(this.opcoes.get(i));
        }
        System.out.println("0 - Sair");
    }

    /** Ler uma opção válida */
    private int lerOpcao() {
        int op;
        //Scanner is = new Scanner(System.in);

        System.out.print("Opção: ");
        try {
            String s = is.nextLine();
            op = Integer.parseInt(s);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            //System.out.println("Opção Inválida!!!");
            op = -1;
        }
        if (op<0 || op>this.opcoes.size()) {
            System.out.println("Opção Inválida!!!");
            op = -1;
        }
        return op;
    }

    /**
     * Método para obter a última opção lida
     */
    public int getOpcao() {
        return this.op;
    }
}
