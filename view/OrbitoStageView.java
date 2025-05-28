package view;

import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.OrbitoStageModel;

import java.util.Scanner;

public class OrbitoStageView extends GameStageView {
    public OrbitoStageView(String name, GameStageModel gameStageModel) {
        super(name,gameStageModel);
    }
    @Override
    public void createLooks() {
        OrbitoStageModel model = (OrbitoStageModel)gameStageModel;

        int taille = model.getBoard().getNbCols();

        addLook(new TextLook(model.getPlayerName()));
        addLook(new ClassicBoardLook(taille, taille, model.getBoard(),1, 1, true));
        addLook(new BlackMarblePotLook(model.getBlackPot()));
        addLook(new WhiteMarblePotLook(model.getWhitePot()));

        int nbrBilles = (taille * taille) / 2;

        for(int i = 0; i < nbrBilles; i++) {
            addLook(new MarbleLook(model.getBlackMarbles()[i]));
            addLook(new MarbleLook(model.getWhiteMarbles()[i]));
        }
    }
}
