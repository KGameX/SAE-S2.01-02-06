package control;

import boardifier.model.GameElement;
import model.OrbitoStageFactory;

public class MoveMarble {
    protected GameElement element;

    protected int colSrc; int rowSrc;

    protected int colDest; int rowDest;

    MoveMarble(GameElement element, int rowSrc, int colSrc) {
        this.element = element;
        this.rowSrc = rowSrc;
        this.colSrc = colSrc;
        this.colDest = colSrc;
        this.rowDest = rowSrc;
    }

    void computeDest(boolean[] rotations) {
        int centerRow = rowSrc / 2;
        int centerCol = colSrc / 2;

        int distanceRow = Math.abs(rowSrc - centerRow);
        int distanceCol = Math.abs(colSrc - centerCol);
        int ring = Math.max(distanceRow, distanceCol);

        if (ring >= 0 && ring < rotations.length) {
            int relRow = rowSrc - centerRow;
            int relCol = colSrc - centerCol;

            if (rotations[ring]) {
                rowDest = relCol + centerRow;
                colDest = -relRow + centerCol;
            } else {
                rowDest = -relCol + centerRow;
                colDest = relRow + centerCol;
            }

            if (rowDest < 0 || rowDest >= 7 || colDest < 0 || colDest >= 7) {
                rowDest = rowSrc;
                colDest = colSrc;
            }
        }
    }

    public GameElement getElement() {
        return element;
    }

    public int getColDest() {
        return colDest;
    }

    public int getRowDest() {
        return rowDest;
    }
}
