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

public class OrbitoDecider extends Decider {
    public OrbitoDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide(){
        ActionList actions = null;
        OrbitoStageModel stage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = stage.getBoard();
        OrbitoMarblePot pot = null;
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;

        actions = ActionFactory.generatePutInContainer(model, pawn, "orbitoboard", rowDest, colDest);
        actions.setDoEndOfTurn(true);
        return actions;
    }
}
