package view;

import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TableLook;
import boardifier.view.TextLook;

import java.util.Scanner;

public class OrbitoStageView extends GameStageView {
    private int taille;
    public OrbitoStageView(String name, GameStageModel gameStageModel,int taille) {
        super(name,gameStageModel);
        this.taille = taille;
    }
    @Override
    public void createLooks() {
//        Scanner s=new Scanner(System.in);
//        System.out.println("Entrez la taille de la grille (4 ou 6)");
        addLook(new TextLook(model.getPlayerName()));
        addLook(new ClassicBoardLook(this.taille,this.taille));
    }
}
