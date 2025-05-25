package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyInt;

public class TestOrbitoStageModel {
    @Test
    public void testExtendMatrix() {
        //méthode apparement impossible à tester avec Mockito
        //puisque Mockito.mock(OrbitoStageModel.class) n'a pas de méthode extend_matrix
        // les autres méthodes retournent void donc impossible à tester aussi
        OrbitoStageModel ostm=Mockito.mock(OrbitoStageModel.class);
        ArrayList<Integer> tmp=new ArrayList<>();
        ArrayList<ArrayList> extended = new ArrayList<>();
        tmp.add(0);tmp.add(0);tmp.add(0);tmp.add(0);tmp.add(0);
        extended.add(tmp);
        tmp=new ArrayList<>();tmp.add(0);tmp.add(0);tmp.add(0);
        extended.add(tmp);
        tmp=new ArrayList<>();tmp.add(0);tmp.add(0);tmp.add(0);
        extended.add(tmp);
        tmp=new ArrayList<>();tmp.add(0);tmp.add(0);tmp.add(0);
        extended.add(tmp);
        ArrayList<ArrayList> non_extended = new ArrayList<>();
        tmp=new ArrayList<>();tmp.add(0);tmp.add(0);
        non_extended.add(tmp);
        tmp=new ArrayList<>();tmp.add(0);tmp.add(0);
        non_extended.add(tmp);


    }

}
