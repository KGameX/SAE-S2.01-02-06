package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.ContainerElement;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.OrbitoBoard;
import model.OrbitoMarblePot;
import model.OrbitoStageModel;
import model.Pawn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrbitoDecider extends Decider {
    private int computerMode;

    public OrbitoDecider(Model model, Controller control, int computerMode) {
        super(model, control);
        this.computerMode = computerMode;
    }

    @Override
    public ActionList decide() {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = gameStage.getBoard();

        long startTime = Profiler.timestamp();

        if (computerMode == 0) {
            return decideRandom();
        } else if (computerMode == 1) {
            return decideBestMove();
        }

        long executionTime = Profiler.timestamp() - startTime;
        Profiler.globalTime += executionTime;
        Profiler.nbExec++;
        System.out.println("[Profiler] OrbitoDecider.decide() : " + executionTime + " ns");

        return decideRandom();
    }

    public static double averageExecutionTime() {
        if (Profiler.nbExec == 0) return 0.0;
        return (double) Profiler.globalTime / Profiler.nbExec;
    }

    public static void printProfilingStats() {
        System.out.println("=== Statistiques OrbitoDecider ===");
        System.out.println("Nombre d'exécutions : " + Profiler.nbExec);
        System.out.println("Temps total : " + Profiler.getGlobalTime() + " ns");
        System.out.println("Temps moyen : " + String.format("%.2f", averageExecutionTime()) + " ns");
    }


    public ActionList decideRandom() {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard orbitoBoard = gameStage.getBoard();

        int size = orbitoBoard.getNbCols();

        Pawn[][] board = new Pawn[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Pawn pawn = (Pawn) orbitoBoard.getElement(row, col);
                board[row][col] = pawn;
            }
        }

        List<String> possibleMoves = getValidMarbleMoves(board, model.getIdPlayer());
        System.out.println(possibleMoves);

        ActionList actionMove = null;
        ActionList actionPlace;

        if (!possibleMoves.isEmpty()) {
            String randomMove = possibleMoves.get((int) (Math.random() * possibleMoves.size()));

            if (Math.random() < 0.5) {
                moveMarble(board, randomMove, false);

                int colSrc = randomMove.charAt(0) - 'A';
                int rowSrc = randomMove.charAt(1) - '1';
                int colDest = randomMove.charAt(2) - 'A';
                int rowDest = randomMove.charAt(3) - '1';

                System.out.println("Computer moves marble from " + randomMove.substring(0, 2) + " to " + randomMove.substring(2));

                Pawn pawnMove = (Pawn) orbitoBoard.getElement(rowSrc, colSrc);
                actionMove = ActionFactory.generateMoveWithinContainer(model, pawnMove, rowDest, colDest);
            }
        }

        List<String> possibleCells = getValidCells(board);

        System.out.println(possibleCells);
        String randomCell = possibleCells.get((int) (Math.random() * possibleCells.size()));

        int col = randomCell.charAt(0) - 'A';
        int row = randomCell.charAt(1) - '1';

        ContainerElement pot = null;
        if (model.getIdPlayer() == 0) {
            pot = gameStage.getWhitePot();
        } else {
            pot = gameStage.getBlackPot();
        }

        int pawnIndex = 0;
        while (pot.isEmptyAt(pawnIndex, 0)) {
            pawnIndex++;
        }
        Pawn pawn = (Pawn) pot.getElement(pawnIndex, 0);

        System.out.println("Computer places marble in " + randomCell);
        actionPlace = ActionFactory.generatePutInContainer(model, pawn, "Orbitoboard", row, col);

        ActionList actions = new ActionList();

        if (actionMove != null) {
            actions.addAll(actionMove);
        }
        actions.addAll(actionPlace);
        actions.setDoEndOfTurn(true); // after playing this action list, it will be the end of turn for current player.
        return actions;
    }


    public ActionList decideBestMove() {
        return null;
    }

    private ArrayList<String> getValidMarbleMoves(Pawn[][] board, int playerID) {
        int size = board.length;
        ArrayList<String> validMoves = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                String cell = Character.toString(65 + col) + (row + 1);
                Pawn pawn = board[row][col];
                if (pawn != null && pawn.getColor() != playerID) {
                    //check if we can move the pawn up, down, left, or right
                    if (row > 0 && board[row - 1][col] == null) {
                        validMoves.add(cell + Character.toString(65 + col) + (row));
                    }

                    if (row < size - 1 && board[row + 1][col] == null) {
                        validMoves.add(cell + Character.toString(65 + col) + (row + 2));
                    }

                    if (col > 0 && board[row][col - 1] == null) {
                        validMoves.add(cell + Character.toString(65 + col - 1) + (row + 1));
                    }

                    if (col < size - 1 && board[row][col + 1] == null) {
                        validMoves.add(cell + Character.toString(65 + col + 1) + (row + 1));
                    }
                }
            }
        }

        return validMoves;
    }

    private ArrayList<String> getValidCells(Pawn[][] board) {
        int size = board.length;
        ArrayList<String> validCells = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                String cell = Character.toString(65 + col) + (row + 1);
                Pawn pawn = board[row][col];
                if (pawn == null) {
                    validCells.add(cell);
                }
            }
        }

        return validCells;
    }

    private void rotateBoard(Pawn[][] board) {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        boolean[] rotation = gameStage.getRotation();

        for (int i = 0; i < rotation.length; i++) {
            if (rotation[i]) {
                rotateRingClockwise(i, board);
            } else {
                rotateRingCounterClockwise(i, board);
            }
        }
    }

    private void rotateRingClockwise(int ring_index, Pawn[][] board) {
        int size = board.length - 1;

        List<Pawn> topList = new ArrayList<>();
        List<Pawn> leftList = new ArrayList<>();
        List<Pawn> rightList = new ArrayList<>();
        List<Pawn> bottomList = new ArrayList<>();

        for (int i = ring_index; i < size - ring_index; i++) {
            // Top row
            Pawn topPawn = board[ring_index][i + 1];
            topList.add(topPawn);

            // Left column
            Pawn leftPawn = board[i][ring_index];
            leftList.add(leftPawn);

            // Right column
            Pawn rightPawn = board[i + 1][size - ring_index];
            rightList.add(rightPawn);

            // Bottom row
            Pawn bottomPawn = board[size - ring_index][i];
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
            board[ring_index][i + 1] = topPawn;

            Pawn leftPawn = leftList.get(counter);
            board[i][ring_index] = leftPawn;

            Pawn rightPawn = rightList.get(counter);
            board[i + 1][size - ring_index] = rightPawn;

            Pawn bottomPawn = bottomList.get(counter);
            board[size - ring_index][i] = bottomPawn;
        }
    }

    private void rotateRingCounterClockwise(int ring_index, Pawn[][] board) {
        int size = board.length - 1; // Assuming square board, so rows == cols

        List<Pawn> topList = new ArrayList<>();
        List<Pawn> leftList = new ArrayList<>();
        List<Pawn> rightList = new ArrayList<>();
        List<Pawn> bottomList = new ArrayList<>();

        for (int i = ring_index; i < size - ring_index; i++) {
            // Top row
            Pawn topPawn = board[ring_index][i];
            topList.add(topPawn);

            // Left column
            Pawn leftPawn = board[i + 1][ring_index];
            leftList.add(leftPawn);

            // Right column
            Pawn rightPawn = board[i][size - ring_index];
            rightList.add(rightPawn);

            // Bottom row
            Pawn bottomPawn = board[size - ring_index][i + 1];
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
            board[ring_index][i] = topPawn;

            Pawn leftPawn = leftList.get(counter);
            board[i + 1][ring_index] = leftPawn;

            Pawn rightPawn = rightList.get(counter);
            board[i][size - ring_index] = rightPawn;

            Pawn bottomPawn = bottomList.get(counter);
            board[size - ring_index][i + 1] = bottomPawn;
        }
    }

    private void moveMarble(Pawn[][] board, String cell, boolean undo) {
        String cellSrc, cellDest;

        if (undo) {
            cellSrc = cell.substring(2);
            cellDest = cell.substring(0, 2);
        } else {
            cellSrc = cell.substring(0, 2);
            cellDest = cell.substring(2);
        }

        int colSrc = cellSrc.charAt(0) - 'A';
        int rowSrc = cellSrc.charAt(1) - '1';
        int colDest = cellDest.charAt(0) - 'A';
        int rowDest = cellDest.charAt(1) - '1';

        Pawn pawn = board[rowSrc][colSrc];
        board[rowSrc][colSrc] = null;
        board[rowDest][colDest] = pawn;
    }

    private void placeMarble(Pawn[][] board, String cell, int playerID, boolean undo) {
        Pawn pawn = new Pawn(1, playerID, model.getGameStage());

        int row = cell.charAt(0) - 'A';
        int col = cell.charAt(1) - '1';

        if (undo) {
            board[row][col] = null;
        } else {
            board[row][col] = pawn;
        }
    }
}
