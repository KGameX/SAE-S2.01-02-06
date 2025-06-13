package model;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
public class OrbitoBoard extends ContainerElement {
    private int nbr_row,nbr_col;
    public OrbitoBoard(int x, int y,int nbr_row,int nbr_col,GameStageModel gameStageModel) {
        // call the super-constructor to create a 3x3 grid, named "Orbitoboard", and in x,y in space
        super("Orbitoboard", x, y, nbr_row , nbr_col, gameStageModel);
    }
    public void set_nbr_row(int nbr_row) {
        this.nbr_row = nbr_row;
    }
    public void set_nbr_col(int nbr_col) {
        this.nbr_col = nbr_col;
    }
    public int getNbRows(){
        return nbr_row;
    }
    public int getNbCols(){
        return nbr_col;
    }
    public void setValidCells(int x,int y) {
        Logger.debug("called",this);
        resetReachableCells(false);
        List<Point> valid = computeValidCells(x,y);
        if (valid != null) {
            for(Point p : valid) {
                reachableCells[p.y][p.x] = true;
            }
        }
    }
    public void setNbrRow(int nbr_row) {
        this.nbr_row = nbr_row;
    }
    public void setNbrCol(int nbr_col) {
        this.nbr_col = nbr_col;
    }
    public int GetNbrRow() {
        return this.nbr_row;
    }
    public int GetNbrCol() {
        return this.nbr_col;
    }
    public List<Point> computeValidCells(int x, int y) {
        List<Point> lst = new ArrayList<>();
        Pawn p = null;
        // if the grid is empty, is it the first turn and thus, all cells are valid
        if (isEmpty()) {
            // i are rows
            for (int i = 0; i < this.GetNbrCol(); i++) {
                // j are cols
                for (int j = 0; j < this.GetNbrRow(); j++) {
                    // cols is in x direction and rows are in y direction, so create a point in (j,i)
                    lst.add(new Point(j, i));
                }
            }
            return lst;
        }
        //bas droit
        if (x==this.GetNbrCol()-1 &&  y==this.GetNbrRow()-1) {
            if (isEmptyAt(x-1,y)){
                lst.add(new Point(x-1,y));
            }
            if (isEmptyAt(x-1,y-1)){
                lst.add(new Point(x-1,y-1));
            }
            if (isEmptyAt(x,y-1)){
                lst.add(new Point(x,y-1));
            }
        }
        //haut droit
        else if (x==this.GetNbrCol()-1 &&  y==0){
            if (isEmptyAt(x-1,y)){
                lst.add(new Point(x-1,y));
            }
            if (isEmptyAt(x,y+1)){
                lst.add(new Point(x,y+1));
            }
            if (isEmptyAt(x-1,y+1)){
                lst.add(new Point(x-1,y+1));
            }
        }
        // bas gauche
        else if (x==0 && y==this.GetNbrRow()-1){
            if (isEmptyAt(x,y-1)){
                lst.add(new Point(x,y-1));
            }
            if (isEmptyAt(x+1,y-1)){
                lst.add(new Point(x+1,y-1));
            }
            if (isEmptyAt(x+1,y)){
                lst.add(new Point(x+1,y));
            }
        }
        else if (x==0 && y==0){
            if (isEmptyAt(x+1,y)){
                lst.add(new Point(x+1,y));
            }
            if (isEmptyAt(x+1,y+1)){
                lst.add(new Point(x+1,y-1));
            }
            if (isEmptyAt(x,y+1)){
                lst.add(new Point(x+1,y));
            }
        }
        else if (x==GetNbrRow()-1){
            if (isEmptyAt(x-1,y)){
                lst.add(new Point(x-1,y));
            }
            if (isEmptyAt(x-1,y-1)){
                lst.add(new Point(x-1,y-1));
            }
            if (isEmptyAt(x-1,y+1)){
                lst.add(new Point(x-1,y+1));
            }
            if (isEmptyAt(x,y+1)){
                lst.add(new Point(x,y+1));
            }
            if (isEmptyAt(x,y-1)){
                lst.add(new Point(x,y-1));
            }
        }
        else if  (y==GetNbrCol()-1){
            if (isEmptyAt(x+1,y)){
                lst.add(new Point(x+1,y));
            }
            if (isEmptyAt(x-1,y)){
                lst.add(new Point(x-1,y));
            }
            if (isEmptyAt(x,y-1)){
                lst.add(new Point(x,y-1));
            }
            if (isEmptyAt(x+1,y-1)){
                lst.add(new Point(x+1,y-1));
            }
            if (isEmptyAt(x-1,y-1)){
                lst.add(new Point(x-1,y-1));
            }
        }
        else if (y==0) {
            if (isEmptyAt(x+1,y)){
                lst.add(new Point(x+1,y));
            }
            if (isEmptyAt(x-1,y)){
                lst.add(new Point(x-1,y));
            }
            if (isEmptyAt(x,y+1)){
                lst.add(new Point(x,y+1));
            }
            if (isEmptyAt(x+1,y+1)){
                lst.add(new Point(x+1,y+1));
            }
            if (isEmptyAt(x-1,y+1)){
                lst.add(new Point(x-1,y+1));
            }
        }
        else if (x==0){
            if (isEmptyAt(x+1,y)){
                lst.add(new Point(x+1,y));
            }
            if (isEmptyAt(x+1,y-1)){
                lst.add(new Point(x+1,y-1));
            }
            if (isEmptyAt(x+1,y+1)){
                lst.add(new Point(x+1,y+1));
            }
            if (isEmptyAt(x,y+1)){
                lst.add(new Point(x,y+1));
            }
            if (isEmptyAt(x,y-1)){
                lst.add(new Point(x,y-1));
            }
        }
        else {
            if (isEmptyAt(x-1,y-1)){
                lst.add(new Point(x-1,y-1));
            }
            if (isEmptyAt(x+1,y-1)){
                lst.add(new Point(x+1,y-1));
            }
            if (isEmptyAt(x+1,y+1)){
                lst.add(new Point(x+1,y+1));
            }
            if (isEmptyAt(x-1,y+1)){
                lst.add(new Point(x-1,y+1));
            }
            if (isEmptyAt(x+1,y)){
                lst.add(new Point(x+1,y));
            }
            if  (isEmptyAt(x-1,y)){
                lst.add(new Point(x-1,y));
            }
            if (isEmptyAt(x,y+1)){
                lst.add(new Point(x,y+1));
            }
            if (isEmptyAt(x,y-1)){
                lst.add(new Point(x,y-1));
            }
        }

        return lst;
    }
}
