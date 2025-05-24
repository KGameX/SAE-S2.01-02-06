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
import java.util.Calendar;
import java.util.Random;

public class OrbitoDecider extends Decider {
    private static final Random loto = new Random(Calendar.getInstance().getTimeInMillis());
    
    public OrbitoDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        ActionList actions = null;
        OrbitoStageModel stage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = stage.getBoard();
        OrbitoMarblePot pot = null;
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;
        
        if (model.getIdPlayer() == Pawn.PAWN_BLACK) {
            pot = stage.getBlackPot();
        } else {
            pot = stage.getWhitePot();
        }

        for (int i=0; i<8; i++) {
            Pawn p = (Pawn)pot.getElement(i,0);
            if (p != null) {
                List<Point> valid = board.computeValidCells(p.getNumber(), 0);
                if (valid.size() != 0) {
                    int id = loto.nextInt(valid.size());
                    pawn = p;
                    rowDest = valid.get(id).y;
                    colDest = valid.get(id).x;
                    break;
                }
            }
        }

        assert pawn != null;
        actions = ActionFactory.generatePutInContainer(model, pawn, "orbitoboard", rowDest, colDest);
        actions.setDoEndOfTurn(true);
        return actions;
    }
}
