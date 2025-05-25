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
import java.util.List;

public class OrbitoDecider extends Decider {
    private int computerMode;

    public OrbitoDecider(Model model, Controller control, int computerMode) {
        super(model, control);
        this.computerMode = computerMode;
    }

    @Override
    public ActionList decide() {
        if (computerMode == 0) {
            return decideGreedy();
        } else if (computerMode == 1) {
            return decideCenterControl();
        }
        return decideCenterControl();
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
}
