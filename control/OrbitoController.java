package control;

import control.OrbitoDecider;
import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.ContainerElement;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import model.OrbitoBoard;
import model.OrbitoMarblePot;
import model.OrbitoStageModel;
import model.Pawn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.*;

public class OrbitoController extends Controller {

    BufferedReader consoleIn;
    boolean firstPlayer;
    int computerMode;
    boolean[] rotation;

    public OrbitoController(Model model, View view, int computerMode, boolean[] rotation) {
        super(model, view);
        firstPlayer = true;
        this.computerMode = computerMode;
        this.rotation = rotation;
    }

    /**
     * 
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        OrbitoStageModel stageModel = (OrbitoStageModel) model.getGameStage();
        stageModel.setRotation(rotation);
        update();
        while(! model.isEndStage()) {
            playTurn();
            endOfTurn();
            rotateBoard();
            update();
        }
        endGame();
    }

    private void playTurn() {
        // get the new player
        Player p = model.getCurrentPlayer();
        boolean ok = false;
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            OrbitoDecider decider = new OrbitoDecider(model,this, computerMode);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        } else {
            if (firstPlayer) {
                firstPlayer = false;
            } else {
                System.out.println("It's your turn.\nDo you want to move an opponent's marble ?");
                String ans = "x";
                try {
                    while (!"YyNn".contains(ans)) {
                        ans = "N";
                        System.out.print(p.getName() + " (y/N) > ");
                        ans = consoleIn.readLine();
                        if (ans.length() > 1) {
                            ans = "x";
                        }
                    }
                } catch (IOException e) {/*Something went wrong ?*/}

                if ((ans.equals("Y")) || (ans.equals("y"))) {
                    System.out.println("Enter the coordinates of the marble you want to move, and the destination cell.");
                    while (!ok) {
                        System.out.print(p.getName() + " > ");
                        try {
                            String line = consoleIn.readLine();
                            if (line.length() == 4) {
                                ok = moveMarble(line);
                            }

                            if (!ok) {
                                System.out.println("Incorrect coordinates or already occupied cell. retry !");
                            }
                        } catch (IOException e) {/*Something went wrong ?*/}
                    }
                }
            }

            ok = false;

            System.out.println("Place a marble in the space of your choice.");
            while (!ok) {
                System.out.print(p.getName()+ " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 2) {
                        ok = analyseAndPlay(line);
                    }

                    if (!ok) {
                        System.out.println("Incorrect coordinates or already occupied cell. retry !");
                    }
                } catch (IOException e) {/*Something went wrong ?*/}
            }
        }
    }

    public void endOfTurn() {
        model.setNextPlayer();
        // get the new player to display its name
        Player p = model.getCurrentPlayer();
        OrbitoStageModel stageModel = (OrbitoStageModel) model.getGameStage();
        stageModel.getPlayerName().setText(p.getName());
    }

    public void rotateBoard() {
        OrbitoStageModel stageModel = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = stageModel.getBoard();
        boolean[] rotations = stageModel.getRotation();
        int n = board.getNbRows();

        int rings = rotations.length;

        OrbitoMarblePot outerRingStocker = new OrbitoMarblePot(0, 0, 1, 20, stageModel);
        OrbitoMarblePot middleRingStocker = new OrbitoMarblePot(0, 0, 1, 12, stageModel);
        OrbitoMarblePot innerRingStocker = new OrbitoMarblePot(0, 0, 1, 4, stageModel);

        int offset = 0;
        GameElement marble = null;

        if (rings == 3) {
            offset = 1;
            for (int x = 0; x < 5; x++) {
                marble = board.getElement(x, 0);
                if (marble != null) {
                    board.removeElement(marble);
                    outerRingStocker.addElement(marble, 0, x);
                }
            }
            for (int y = 0; y < 5; y++) {
                marble = board.getElement(5, y);
                if (marble != null) {
                    board.removeElement(marble);
                    outerRingStocker.addElement(marble, 0, y + 5);
                }
            }
            for (int x = 5; x >= 1; x--) {
                marble = board.getElement(x, 5);
                if (marble != null) {
                    board.removeElement(marble);
                    outerRingStocker.addElement(marble, 0, (5 - x) + 10);
                }
            }
            for (int y = 5; y >= 1; y--) {
                marble = board.getElement(0, y);
                if (marble != null) {
                    board.removeElement(marble);
                    outerRingStocker.addElement(marble, 0, (5 - y) + 15);
                }
            }
        }

        for (int x = 0; x < 3; x++) {
            marble = board.getElement(x, offset);
            if (marble != null) {
                board.removeElement(marble);
                middleRingStocker.addElement(marble, 0, x);
            }
        }
        for (int y = 0; y < 3; y++) {
            marble = board.getElement(3 + offset, y);
            if (marble != null) {
                board.removeElement(marble);
                middleRingStocker.addElement(marble, 0, y + 3);
            }
        }
        for (int x = 2; x >= 1; x--) {
            marble = board.getElement(x, 3 + offset);
            if (marble != null) {
                board.removeElement(marble);
                middleRingStocker.addElement(marble, 0, (3 - x) + 6);
            }
        }
        for (int y = 2; y >= 1; y--) {
            marble = board.getElement(offset, y);
            if (marble != null) {
                board.removeElement(marble);
                middleRingStocker.addElement(marble, 0, (3 - y) + 9);
            }
        }

        marble = board.getElement(1 + offset, 1 + offset);
        if (marble != null) {
            board.removeElement(marble);
            innerRingStocker.addElement(marble, 0, 0);
        }
        marble = board.getElement(1 + offset, 2 + offset);
        if (marble != null) {
            board.removeElement(marble);
            innerRingStocker.addElement(marble, 0, 1);
        }
        marble = board.getElement(2 + offset, 2 + offset);
        if (marble != null) {
            board.removeElement(marble);
            innerRingStocker.addElement(marble, 0, 2);
        }
        marble = board.getElement(2 + offset, 1 + offset);
        if (marble != null) {
            board.removeElement(marble);
            innerRingStocker.addElement(marble, 0, 3);
        }


        if (rotations[0]) {
            if (rings == 3) rotateClockwise(outerRingStocker);
            rotateClockwise(middleRingStocker);
            rotateClockwise(innerRingStocker);
        } else {
            if (rings == 3) rotateCounterClockwise(outerRingStocker);
            rotateCounterClockwise(middleRingStocker);
            rotateCounterClockwise(innerRingStocker);
        }

        if (rings == 3) {
            for (int x = 0; x < 5; x++) {
                marble = outerRingStocker.getElement(0, x);
                if (marble != null) {
                    outerRingStocker.removeElement(marble);
                    board.addElement(marble, x, 0);
                }
            }
            for (int y = 0; y < 5; y++) {
                marble = outerRingStocker.getElement(0, y + 5);
                if (marble != null) {
                    outerRingStocker.removeElement(marble);
                    board.addElement(marble, 5, y);
                }
            }
            for (int x = 5; x >= 1; x--) {
                marble = outerRingStocker.getElement(0, (5 - x) + 10);
                if (marble != null) {
                    outerRingStocker.removeElement(marble);
                    board.addElement(marble, x, 5);
                }
            }
            for (int y = 5; y >= 1; y--) {
                marble = outerRingStocker.getElement(0, (5 - y) + 15);
                if (marble != null) {
                    outerRingStocker.removeElement(marble);
                    board.addElement(marble, 0, y);
                }
            }
        }

        for (int x = 0; x < 3; x++) {
            marble = middleRingStocker.getElement(0, x);
            if (marble != null) {
                middleRingStocker.removeElement(marble);
                board.addElement(marble, x, offset);
            }
        }
        for (int y = 0; y < 3; y++) {
            marble = middleRingStocker.getElement(0, y + 3);
            if (marble != null) {
                middleRingStocker.removeElement(marble);
                board.addElement(marble, 3 + offset, y);
            }
        }
        for (int x = 2; x >= 1; x--) {
            marble = middleRingStocker.getElement(0, (3 - x) + 6);
            if (marble != null) {
                middleRingStocker.removeElement(marble);
                board.addElement(marble, x, 3 + offset);
            }
        }
        for (int y = 2; y >= 1; y--) {
            marble = middleRingStocker.getElement(0, (3 - y) + 9);
            if (marble != null) {
                middleRingStocker.removeElement(marble);
                board.addElement(marble, offset, y);
            }
        }

        marble = innerRingStocker.getElement(0, 0);
        if (marble != null) {
            innerRingStocker.removeElement(marble);
            board.addElement(marble, 1 + offset, 1 + offset);
        }
        marble = innerRingStocker.getElement(0, 1);
        if (marble != null) {
            innerRingStocker.removeElement(marble);
            board.addElement(marble, 1 + offset, 2 + offset);
        }
        marble = innerRingStocker.getElement(0, 2);
        if (marble != null) {
            innerRingStocker.removeElement(marble);
            board.addElement(marble, 2 + offset, 2 + offset);
        }
        marble = innerRingStocker.getElement(0, 3);
        if (marble != null) {
            innerRingStocker.removeElement(marble);
            board.addElement(marble, 2 + offset, 1 + offset);
        }
    }

    private void rotateClockwise(OrbitoMarblePot stocker) {
        int size = stocker.getNbCols();
        GameElement lastElement = stocker.getElement(0, size - 1);
        if (lastElement != null) {
            stocker.removeElement(lastElement);
        }
        for (int i = size - 1; i > 0; i--) {
            GameElement element = stocker.getElement(0, i - 1);
            if (element != null) {
                stocker.removeElement(element);
                stocker.addElement(element, 0, i);
            }
        }
        if (lastElement != null) {
            stocker.addElement(lastElement, 0, 0);
        }
    }

    private void rotateCounterClockwise(OrbitoMarblePot stocker) {
        int size = stocker.getNbCols();
        GameElement firstElement = stocker.getElement(0, 0);
        if (firstElement != null) {
            stocker.removeElement(firstElement);
        }
        for (int i = 0; i < size - 1; i++) {
            GameElement element = stocker.getElement(0, i + 1);
            if (element != null) {
                stocker.removeElement(element);
                stocker.addElement(element, 0, i);
            }
        }
        if (firstElement != null) {
            stocker.addElement(firstElement, 0, size - 1);
        }
    }

    public boolean moveMarble(String line) {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = gameStage.getBoard();
        int nbr_cols = board.getNbCols();
        int nbr_rows = board.getNbRows();

        int colSrc = (int) (line.charAt(0) - 'A');
        int rowSrc = (int) (line.charAt(1) - '1');
        int colDest = (int) (line.charAt(2) - 'A');
        int rowDest = (int) (line.charAt(3) - '1');

        if ((colSrc < 0) || (colSrc > nbr_cols - 1)) return false;
        if ((rowSrc < 0) || (rowSrc > nbr_rows - 1)) return false;
        if ((colDest < 0) || (colDest > nbr_cols - 1)) return false;
        if ((rowDest < 0) || (rowDest > nbr_rows - 1)) return false;

        Pawn pawn = (Pawn) board.getElement(colSrc, rowSrc);
        if (pawn == null) return false;
        if (board.isElementAt(rowDest, colDest)) return false;

        int playerID = model.getIdPlayer();
        if (playerID == pawn.getColor()) return false;

        // Checks if the move is legal by calculating the length between the two cells.
        if (Math.sqrt(Math.pow(colDest - colSrc, 2) + Math.pow(rowDest - rowSrc, 2)) != 1) return false;

        ActionList actions = ActionFactory.generateMoveWithinContainer(model, pawn, rowDest, colDest);
        actions.setDoEndOfTurn(false); // after playing this action list, it won't be the end of turn for current player.
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();

        return true;
    }

    private boolean analyseAndPlay(String line) {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = gameStage.getBoard();
        int nbr_cols = board.getNbCols();
        int nbr_rows = board.getNbRows();

        int col = (int) (line.charAt(0) - 'A');
        int row = (int) (line.charAt(1) - '1');

        if ((row < 0) || (row > nbr_rows - 1)) return false;
        if ((col < 0) || (col > nbr_cols - 1)) return false;

        ContainerElement pot = null;
        if (model.getIdPlayer() == 0) {
            pot = gameStage.getWhitePot();
        } else {
            pot = gameStage.getBlackPot();
        }

        if (board.isElementAt(row, col)) return false;

        int pawnIndex = 0;
        while (pot.isEmptyAt(pawnIndex, 0)) {
            pawnIndex++;
        }
        Pawn pawn = (Pawn) pot.getElement(pawnIndex, 0);

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "Orbitoboard", row, col);
        actions.setDoEndOfTurn(true); // after playing this action list, it will be the end of turn for current player.
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();

        return true;
    }
}