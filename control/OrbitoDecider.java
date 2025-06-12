package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
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

        int size = board.getNbCols();

        long startTime = Profiler.timestamp();

        Pawn[][] boardArray = new Pawn[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Pawn pawn = (Pawn) board.getElement(row, col);
                boardArray[row][col] = pawn;
            }
        }

        if (computerMode == 0) {
            return decideGreedy();
        } else if (computerMode == 1) {
            return decideCenterControl();
        }

        long executionTime = Profiler.timestamp() - startTime;
        Profiler.globalTime += executionTime;
        Profiler.nbExec++;
        System.out.println("[Profiler] OrbitoDecider.decide() : " + executionTime + " ns");

        return decideCenterControl();
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

    public ActionList decideCenterControl() {
        ActionList actions = null;
        OrbitoStageModel stage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = stage.getBoard();
        OrbitoMarblePot pot = (model.getIdPlayer() == Pawn.PAWN_BLACK) ? stage.getBlackPot() : stage.getWhitePot();
        GameElement bestPawn = null;
        int bestRow = 0, bestCol = 0;
        double minDist = Double.MAX_VALUE;

        int centerRow = board.GetNbrRow() / 2;
        int centerCol = board.GetNbrCol() / 2;

        for (int i = 0; i < 8; i++) {
            Pawn p = (Pawn) pot.getElement(i, 0);
            if (p != null) {
                List<Point> valid = board.computeValidCells(p.getNumber(), 0);
                for (Point pt : valid) {
                    double dist = Math.hypot(pt.x - centerCol, pt.y - centerRow);
                    if (dist < minDist) {
                        minDist = dist;
                        bestPawn = p;
                        bestRow = pt.y;
                        bestCol = pt.x;
                    }
                }
            }
        }
        if (bestPawn == null) throw new IllegalStateException("Aucun coup possible");
        actions = ActionFactory.generatePutInContainer(model, bestPawn, "orbitoboard", bestRow, bestCol);
        actions.setDoEndOfTurn(true);
        return actions;
    }

    public ActionList decideGreedy() {
        ActionList actions = null;
        OrbitoStageModel stage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = stage.getBoard();
        OrbitoMarblePot pot = (model.getIdPlayer() == Pawn.PAWN_BLACK) ? stage.getBlackPot() : stage.getWhitePot();
        GameElement bestPawn = null;
        int bestRow = 0, bestCol = 0, maxChoices = -1;

        for (int i = 0; i < 8; i++) {
            Pawn p = (Pawn) pot.getElement(i, 0);
            if (p != null) {
                List<Point> valid = board.computeValidCells(p.getNumber(), 0);
                if (valid.size() > maxChoices) {
                    maxChoices = valid.size();
                    if (!valid.isEmpty()) {
                        bestPawn = p;
                        bestRow = valid.get(0).y;
                        bestCol = valid.get(0).x;
                    }
                }
            }
        }
        if (bestPawn == null) throw new IllegalStateException("Aucun coup possible");
        actions = ActionFactory.generatePutInContainer(model, bestPawn, "orbitoboard", bestRow, bestCol);
        actions.setDoEndOfTurn(true);
        return actions;
    }

    public void decideBestMove(Pawn[][] board, int depth) {

        
    }

    private ArrayList<String> getValidMarbleMoves(Pawn[][] board, int playerID) {
        int size = board.length;
        ArrayList<String> validMoves = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                String cell = Character.toString(65 + row) + (col + 1);
                Pawn pawn = board[row][col];
                if (pawn != null && pawn.getColor() != playerID) {
                    //check if we can move the pawn up, down, left, or right
                    if (row > 0 && board[row - 1][col] == null) {
                        validMoves.add(cell + Character.toString(65 + row - 1) + (col + 1));
                    }

                    if (row < size - 1 && board[row + 1][col] == null) {
                        validMoves.add(cell + Character.toString(65 + row + 1) + (col + 1));
                    }

                    if (col > 0 && board[row][col - 1] == null) {
                        validMoves.add(cell + Character.toString(65 + row) + col);
                    }

                    if (col < size - 1 && board[row][col + 1] == null) {
                        validMoves.add(cell + Character.toString(65 + row) + (col + 2));
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
                String cell = Character.toString(65 + row) + (col + 1);
                Pawn pawn = board[row][col];
                if (pawn == null) {
                    validCells.add(cell);
                }
            }
        }

        return validCells;
    }

    private Pawn[][] rotateBoard(Pawn[][] board) {
        OrbitoStageModel gameStage = (OrbitoStageModel) model.getGameStage();
        boolean[] rotation = gameStage.getRotation();

        for (int i = 0; i < rotation.length; i++) {
            if (rotation[i]) {
                board = rotateRingClockwise(i, board);
            } else {
                board = rotateRingCounterClockwise(i, board);
            }
        }

        return board;
    }

    private Pawn[][] rotateRingClockwise(int ring_index, Pawn[][] board) {
        int size = board.length - 1; // Assuming square board, so rows == cols

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

        return board;
    }

    private Pawn[][] rotateRingCounterClockwise(int ring_index, Pawn[][] board) {
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

        return board;
    }
}
