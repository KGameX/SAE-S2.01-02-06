import boardifier.control.*;
import boardifier.model.*;
import boardifier.view.*;
import control.OrbitoController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logger.setLevel(Logger.LOGGER_TRACE);
        Logger.setVerbosity(Logger.VERBOSE_HIGH);
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

        StageFactory.registerModelAndView("orbito", "model.OrbitoStageModel", "view.OrbitoStageView");
        View orbitoView = new View(model);
        OrbitoController control = new OrbitoController(model, orbitoView, computerMode);
        control.setFirstStageName("orbito");
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
