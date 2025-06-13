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


        int mode = 0;

        System.out.println("Which mode do you want to play ?");
        System.out.println("0 - Play against another human player");
        System.out.println("1 - Play against a computer");
        System.out.println("2 - Computer plays against another computer");
        System.out.print("> ");

        try {
            int choix = scanner.nextInt();
            if (choix == 0 || choix == 1 || choix == 2) {
                mode = choix;
            } else {
                System.out.println("Invalid choice, defaulting to player vs player.");
            }
        } catch (Exception e) {
            System.out.println("Invalid choice, defaulting to player vs player.");
        }

        int computerMode = 0;
        if (mode != 0) {
            System.out.println("Which mode should the computer play ? (0 = random / 1 = best move)");

            System.out.print("> ");

            try {
                int choix = scanner.nextInt();
                if (choix == 0 || choix == 1) {
                    computerMode = choix;
                } else {
                    System.out.println("Invalid choice, defaulting to random move.");
                }
            } catch (Exception e) {
                System.out.println("Invalid choice, defaulting to random move.");
            }
        }


        int nbr_aligner = 4;
        if (taille == 6) {
            System.out.println("How many marbles need to be aligned to win ? (4 or 6)");
            System.out.print("> ");

            try {
                int choix = scanner.nextInt();
                if (choix == 4 || choix == 6) {
                    nbr_aligner = choix;
                } else {
                    System.out.println("Invalid choice, defaulting to 4 marbles.");
                }
            } catch (Exception e) {
                System.out.println("Invalid choice, defaulting to 4 marbles.");
            }
        }

        Model model = new Model();
        OrbitoStageFactory.setDefaultSize(taille);
        OrbitoStageFactory.setNbr_align(nbr_aligner);

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

        StageFactory.registerModelAndView("orbito++", "model.OrbitoStageModel", "view.OrbitoStageView");
        View orbitoView = new View(model);
        OrbitoController control = new OrbitoController(model, orbitoView, computerMode, rotation, nbr_aligner);
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
