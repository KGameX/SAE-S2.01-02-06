package model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;

/**
 * Hole pot for pawns represent the element where pawns are stored at the beginning of the party.
 * Thus, a simple ContainerElement with 4 rows and 1 column is needed.
 */
public class OrbitoMarblePot extends ContainerElement {
    public OrbitoMarblePot(int x, int y,int nbr_row,int nbr_col, GameStageModel gameStageModel) {
        // call the super-constructor to create a 4x1 grid, named "pawnpot", and in x,y in space
        super("marblepot", x, y,  nbr_row, nbr_col,gameStageModel);
    }
}
