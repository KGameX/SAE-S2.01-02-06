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

    Scanner consoleIn;
    boolean firstPlayer;
    int computerMode;
    boolean[] rotation;
    int nbr_align;

    public OrbitoController(Model model, View view, int computerMode, boolean[] rotation, int nbr_align, Scanner scanner) {
        super(model, view);
        firstPlayer = true;
        this.computerMode = computerMode;
        this.rotation = rotation;
        this.nbr_align = nbr_align;
        this.consoleIn = scanner;
    }

    /**
     * 
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        //consoleIn = new Scanner(System.in);
        OrbitoStageModel stageModel = (OrbitoStageModel) model.getGameStage();
        stageModel.setRotation(rotation);
        stageModel.setNbr_align(nbr_align);
        update();
        while(! model.isEndStage()) {
            playTurn();
            endOfTurn();
            rotateBoard();
            stageModel.computePartyResult();
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
                while (!"YyNn".contains(ans)) {
                    ans = "N";
                    System.out.print(p.getName() + " (y/N) > ");
                    ans = consoleIn.next();
                    if (ans.length() > 1) {
                        ans = "x";
                    }
                }

                if ((ans.equals("Y")) || (ans.equals("y"))) {
                    System.out.println("Enter the coordinates of the marble you want to move, and the destination cell.");
                    while (!ok) {
                        System.out.print(p.getName() + " > ");
                        String line = consoleIn.next();
                        if (line.length() == 4) {
                            ok = moveMarble(line);
                        }

                        if (!ok) {
                            System.out.println("Incorrect move, coordinates or already occupied cell. retry !");
                        }
                    }
                    update();
                }
            }

            ok = false;

            System.out.println("Place a marble in the space of your choice.");
            while (!ok) {
                System.out.print(p.getName()+ " > ");
                String line = consoleIn.next();
                if (line.length() == 2) {
                    ok = analyseAndPlay(line);
                }

                if (!ok) {
                    System.out.println("Incorrect coordinates or already occupied cell. retry !");
                }
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
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = gameStage.getBoard();
        int nbr_cols = board.getNbCols();
        int nbr_rows = board.getNbRows();

        // rotate the board
        for (int i = 0; i < rotation.length; i++) {
            if (rotation[i]) {
                rotateRingClockwise(i);
            } else {
                rotateRingCounterClockwise(i);
            }
        }
    }

    public void rotateRingClockwise(int ring_index) {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = gameStage.getBoard();

        int size = board.getNbRows() - 1; // Assuming square board, so rows == cols

        List<Pawn> topList = new ArrayList<>();
        List<Pawn> leftList = new ArrayList<>();
        List<Pawn> rightList = new ArrayList<>();
        List<Pawn> bottomList = new ArrayList<>();

        for (int i = ring_index; i < size - ring_index; i++) {
            // Top row
            Pawn topPawn = (Pawn) board.getElement(ring_index, i + 1);
            topList.add(topPawn);

            // Left column
            Pawn leftPawn = (Pawn) board.getElement(i, ring_index);
            leftList.add(leftPawn);

            // Right column
            Pawn rightPawn = (Pawn) board.getElement(i + 1, size - ring_index);
            rightList.add(rightPawn);

            // Bottom row
            Pawn bottomPawn = (Pawn) board.getElement(size - ring_index, i);
            bottomList.add(bottomPawn);
        }

        Pawn topLeftPawn = leftList.removeFirst();
        Pawn topRightPawn = topList.removeLast();
        Pawn bottomLeftPawn = bottomList.removeFirst();
        Pawn bottomRightPawn = rightList.removeLast();

        topList.addFirst(topLeftPawn);
        leftList.addLast(bottomLeftPawn);
        rightList.addFirst(topRightPawn);
        bottomList.addLast(bottomRightPawn);

        for (int i = ring_index, counter = 0; i < size - ring_index; i++, counter++) {
            Pawn topPawn = topList.get(counter);
            if (topPawn != null) board.moveElement(topPawn, ring_index, i + 1);

            Pawn leftPawn = leftList.get(counter);
            if (leftPawn != null)  board.moveElement(leftPawn, i, ring_index);

            Pawn rightPawn = rightList.get(counter);
            if (rightPawn != null) board.moveElement(rightPawn, i + 1, size - ring_index);

            Pawn bottomPawn = bottomList.get(counter);
            if (bottomPawn != null) board.moveElement(bottomPawn, size - ring_index, i);
        }
    }

    public void rotateRingCounterClockwise(int ring_index) {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = gameStage.getBoard();

        int size = board.getNbRows() - 1; // Assuming square board, so rows == cols

        List<Pawn> topList = new ArrayList<>();
        List<Pawn> leftList = new ArrayList<>();
        List<Pawn> rightList = new ArrayList<>();
        List<Pawn> bottomList = new ArrayList<>();

        for (int i = ring_index; i < size - ring_index; i++) {
            // Top row
            Pawn topPawn = (Pawn) board.getElement(ring_index, i);
            topList.add(topPawn);

            // Left column
            Pawn leftPawn = (Pawn) board.getElement(i + 1, ring_index);
            leftList.add(leftPawn);

            // Right column
            Pawn rightPawn = (Pawn) board.getElement(i, size - ring_index);
            rightList.add(rightPawn);

            // Bottom row
            Pawn bottomPawn = (Pawn) board.getElement(size - ring_index, i + 1);
            bottomList.add(bottomPawn);
        }

        Pawn topLeftPawn = topList.removeFirst();
        Pawn topRightPawn = rightList.removeFirst();
        Pawn bottomLeftPawn = leftList.removeLast();
        Pawn bottomRightPawn = bottomList.removeLast();

        topList.addLast(topRightPawn);
        leftList.addFirst(topLeftPawn);
        rightList.addLast(bottomRightPawn);
        bottomList.addFirst(bottomLeftPawn);

        for (int i = ring_index, counter = 0; i < size - ring_index; i++, counter++) {
            Pawn topPawn = topList.get(counter);
            if (topPawn != null) board.moveElement(topPawn, ring_index, i);

            Pawn leftPawn = leftList.get(counter);
            if (leftPawn != null)  board.moveElement(leftPawn, i + 1, ring_index);

            Pawn rightPawn = rightList.get(counter);
            if (rightPawn != null) board.moveElement(rightPawn, i, size - ring_index);

            Pawn bottomPawn = bottomList.get(counter);
            if (bottomPawn != null) board.moveElement(bottomPawn, size - ring_index, i + 1);
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

        Pawn pawn = (Pawn) board.getElement(rowSrc, colSrc);
        if (pawn == null) return false;
        if (board.isElementAt(rowDest, colDest)) return false;

        int playerID = model.getIdPlayer();
        System.out.println(playerID);
        System.out.println(pawn.getColor());
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