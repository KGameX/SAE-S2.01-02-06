package control;

import boardifier.control.Decider;
import boardifier.model.action.ActionList;
import model.OrbitoBoard;
import model.OrbitoMarblePot;
import model.OrbitoStageModel;

public class OrbitoDecider extends Decider {
    @Override
    public ActionList decide(){
        ActionList actions = null;
        OrbitoStageModel stage = (OrbitoStageModel) model.getGameStage();
        OrbitoBoard board = stage.getBoard();
        OrbitoMarblePot pot = null;
        Orbito
    }
}
