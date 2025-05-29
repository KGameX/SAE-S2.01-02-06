package view;

import boardifier.model.GameElement;
import boardifier.view.ElementLook;
import model.Pawn;
import boardifier.view.ConsoleColor;

public class MarbleLook extends ElementLook {
    public MarbleLook(GameElement element) {
        super(element,1,1);
    }
    public void render(){
        Pawn pawn = (Pawn)element;
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            shape[0][0] = ConsoleColor.WHITE + ConsoleColor.BLACK_BACKGROUND + pawn.getNumber() + ConsoleColor.RESET;
        } else {
            shape[0][0] = ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND + pawn.getNumber() + ConsoleColor.RESET;
        }
    }
}