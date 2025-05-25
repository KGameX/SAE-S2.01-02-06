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
import model.OrbitoStageModel;
import model.Pawn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.*;

public class OrbitoController extends Controller {

    BufferedReader consoleIn;
    boolean firstPlayer;

    public OrbitoController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
    }

    /**
     * 
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        update();
        while(! model.isEndStage()) {
            playTurn();
            endOfTurn();
            update();
        }
        endGame();
    }

    private void playTurn() {
        // get the new player
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            OrbitoDecider decider = new OrbitoDecider(model,this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        } else {
            System.out.println("It's your turn.\nDo you want to move an opponent's marble ?");
            String ans = "x";
            try {
                while (!"YyNn".contains(ans)) {
                    System.out.print(p.getName() + " (y/N) > ");
                    ans = consoleIn.readLine();
                }
            } catch (IOException e) {/*Something went wrong ?*/}

            boolean ok = false;

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

            ok = false;

            System.out.println("Now, place a marble in the space of your choice.");
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
        rotateBoard();
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
        for (int ring = 0; ring < rings; ring++) {
            int start = ring;
            int end = n - 1 - ring;
            if (start >= end) break;

            List<Pawn> elements = new ArrayList<>();

            // Haut
            for (int col = start; col <= end; col++)
                elements.add((Pawn) board.getElement(start, col));
            // Droite
            for (int row = start + 1; row < end; row++)
                elements.add((Pawn) board.getElement(row, end));
            // Bas
            for (int col = end; col >= start; col--)
                elements.add((Pawn) board.getElement(end, col));
            // Gauche
            for (int row = end - 1; row > start; row--)
                elements.add((Pawn) board.getElement(row, start));

            // Rotation
            if (rotations[ring]) {
                elements.add(0, elements.remove(elements.size() - 1));
            } else {
                elements.add(elements.remove(0));
            }

            int idx = 0;
            // Réinjecter les éléments dans l'anneau
            // Haut
            for (int col = start; col <= end; col++)
                board.addElement(elements.get(idx++), start, col);
            // Droite
            for (int row = start + 1; row < end; row++)
                board.addElement(elements.get(idx++), row, end);
            // Bas
            for (int col = end; col >= start; col--)
                board.addElement(elements.get(idx++), end, col);
            // Gauche
            for (int row = end - 1; row > start; row--)
                board.addElement(elements.get(idx++), row, start);
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