package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;

public class TestOrbitoBoard {
    @Test
    public void test() {
        List<Point> lst = new ArrayList<>();

        int cols=2;
        int rows=2;
        for(int i=0;i<cols;i++) {
            for (int j = 0; j < rows; j++) {
                lst.add(new Point(j,i));
            }
        }

        OrbitoBoard board =Mockito.mock(OrbitoBoard.class);
        //pour appeler la vraie méthode et eviter de return null
        Mockito.when(board.computeValidCells(anyInt(), anyInt())).thenCallRealMethod();

        //quand y'a le plateau vide , le joueur joue où il veut
        Mockito.when(board.isEmpty()).thenReturn(true);
        Mockito.when(board.GetNbrRow()).thenReturn(rows);
        Mockito.when(board.GetNbrCol()).thenReturn(cols);
        Assertions.assertEquals(lst,board.computeValidCells(0,0));

        // x=0 & y!=0
        Mockito.when(board.isEmpty()).thenReturn(false);
        Mockito.when(board.isEmptyAt(anyInt(),anyInt())).thenReturn(true);
        lst = new ArrayList<>();
        lst.add(new Point(0,0));
        lst.add(new Point(1,1));
        Assertions.assertEquals(lst,board.computeValidCells(0,1));

        // x!=0 & y=0
        lst = new ArrayList<>();
        lst.add(new Point(0,0));
        lst.add(new Point(1,1));
        Assertions.assertEquals(lst,board.computeValidCells(1,0));

        // Test setValidCells
        Mockito.when(board.canReachCell(anyInt(), anyInt())).thenCallRealMethod();
        List<Point> nlst = new ArrayList<>();
        nlst.add(new Point(0,0));
        nlst.add(new Point(1,1));
        Mockito.doReturn(nlst).when(board).computeValidCells(anyInt(), anyInt());
        board.setValidCells(0,1);
        //Assertions.assertEquals(lst,board.canReachCell(0,1));


    }
}
