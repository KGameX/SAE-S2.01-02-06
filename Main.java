import boardifier.control.*;
import boardifier.model.*;
import boardifier.view.*;
import control.OrbitoController;
import model.OrbitoStageFactory;
import model.OrbitoStageModel;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //Logger.setLevel(Logger.LOGGER_TRACE);
        //Logger.setVerbosity(Logger.VERBOSE_HIGH);
        int mode = 0;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if ((mode <0) || (mode>2)) mode = 0;
            }
            catch(NumberFormatException e) {
                mode = 0;
            }
        }

        System.out.println("Welcome to Orbito++.");
        System.out.println("Choose the size of the board :");
        System.out.println("4 - 4×4 board (8 marbles per player)");
        System.out.println("6 - 6×6 board (18 marbles per player)");
        System.out.print("> ");

        int taille = 4; // Valeur par défaut
        try {
            int choix = scanner.nextInt();
            if (choix == 4 || choix == 6) {
                taille = choix;
            } else {
                System.out.println("Invalid choice, defaulting to 4×4 board.");
            }
        } catch (Exception e) {
            System.out.println("Invalid choice, defaulting to 4×4 board.");
        }

        System.out.println(taille + "×" + taille + " board has been selected.");

        System.out.println("Choose the rotation direction for each ring,");
        System.out.println("1 = clockwise, 0 = counter-clockwise.");
        System.out.println("There are " + taille / 2 + " rings.");
        System.out.print("> ");
        String input = scanner.next();

        boolean[] rotation = new boolean[taille / 2];
        for (int i = 0; i < taille / 2; i++) {
            rotation[i] = true;
        }

        boolean inputValid = true;

        if (input.length() == taille / 2) {
            for (int i = 0; i < taille / 2; i++) {
                if (!"01".contains(String.valueOf(input.charAt(i)))) {
                    inputValid = false;
                    break;
                }
            }
        } else {
            inputValid = false;
        }

        if (inputValid) {
            for (int i = 0; i < taille / 2; i++) {
                rotation[i] = input.charAt(i) == '1';
            }
        } else {
            System.out.println("Invalid input, defaulting to clockwise rotation for all rings.");
        }

        Model model = new Model();
        if (mode == 0) {
            model.addHumanPlayer("player1");
            model.addHumanPlayer("player2");
        } else if (mode == 1) {
            model.addHumanPlayer("player");
            model.addComputerPlayer("computer");
        } else if (mode == 2) {
            model.addComputerPlayer("computer1");
            model.addComputerPlayer("computer2");
        }

        int computerMode = 0;
        if (mode != 0){
            System.out.println("Quel mode l'ordinateur doit-il jouer ? (0 = greedy / 1 = center control)");
            computerMode = scanner.nextInt();
        }

        OrbitoStageFactory.setDefaultSize(taille);

        System.out.println("Quel est le nombre de billes nécessaire à aligner pour gagner?");
        int nbr_aligner = scanner.nextInt();
        OrbitoStageFactory.setNbr_align(nbr_aligner);

        StageFactory.registerModelAndView("orbito++", "model.OrbitoStageModel", "view.OrbitoStageView");
        View orbitoView = new View(model);
        OrbitoController control = new OrbitoController(model, orbitoView, computerMode, rotation);
        control.setFirstStageName("orbito++");
        try {
            control.startGame();
            control.stageLoop();
        }
        catch(GameException e) {
            System.out.println(e.getMessage());
            System.out.println("Cannot start the game. Abort");
        }
    }
}
