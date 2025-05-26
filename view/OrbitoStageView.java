package view;

import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.OrbitoStageModel;

import java.util.Scanner;

public class OrbitoStageView extends GameStageView {
    private int taille;
    public OrbitoStageView(String name, GameStageModel gameStageModel) {
        super(name,gameStageModel);
    }
    @Override
    public void createLooks() {
        OrbitoStageModel model = (OrbitoStageModel)gameStageModel;

        addLook(new TextLook(model.getPlayerName()));
        addLook(new ClassicBoardLook(this.taille,this.taille, model.getBoard(),1, 1, true));
        addLook(new BlackMarblePotLook(model.getBlackPot()));
        addLook(new WhiteMarblePotLook(model.getWhitePot()));
        for(int i = 0; i < 8; i++) {
            addLook(new MarbleLook(model.getBlackMarbles()[i]));
            addLook(new MarbleLook(model.getWhiteMarbles()[i]));
        }
    }
}
