package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import model.OrbitoBoard;
import model.OrbitoStageModel;
import model.Pawn;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.Assertions;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

public class TestOrbitoController {

    @Test
    public void testRotateBoard() {
        OrbitoBoard board = Mockito.mock(OrbitoBoard.class);
        OrbitoController controller = Mockito.mock(OrbitoController.class);
        OrbitoStageModel stageModel = Mockito.mock(OrbitoStageModel.class);
        Pawn pawn1 = Mockito.mock(Pawn.class);
        Pawn pawn2 = Mockito.mock(Pawn.class);
        Pawn pawn3 = Mockito.mock(Pawn.class);
        Pawn pawn4 = Mockito.mock(Pawn.class);
        Pawn pawn5 = Mockito.mock(Pawn.class);
        Pawn pawn6 = Mockito.mock(Pawn.class);
        Pawn pawn7 = Mockito.mock(Pawn.class);

        Mockito.when(board.GetNbrRow()).thenReturn(4);
        Mockito.when(board.GetNbrCol()).thenReturn(4);
        Mockito.when(stageModel.getRotation()).thenReturn(new boolean[]{false});
        Mockito.when(board.getElement(0,0)).thenReturn(pawn1);
        Mockito.when(board.getElement(1,1)).thenReturn(pawn2);
        Mockito.when(board.getElement(0,2)).thenReturn(pawn3);
        Mockito.when(board.getElement(2,3)).thenReturn(pawn4);
        Mockito.when(board.getElement(0,3)).thenReturn(pawn5);
        Mockito.when(board.getElement(1,0)).thenReturn(pawn6);
        Mockito.when(board.getElement(3,1)).thenReturn(pawn7);

        controller.rotateBoard();

        Assertions.assertEquals(pawn1, board.getElement(1,0));
        Assertions.assertEquals(pawn2, board.getElement(2,1));
        Assertions.assertEquals(pawn3, board.getElement(0,1));
        Assertions.assertEquals(pawn4, board.getElement(2,2));
        Assertions.assertEquals(pawn5, board.getElement(0,2));
        Assertions.assertEquals(pawn6, board.getElement(2,0));
        Assertions.assertEquals(pawn7, board.getElement(3,2));
    }

    @Test
    public void testMoveMarbleValid() {
        OrbitoBoard board = Mockito.mock(OrbitoBoard.class);
        OrbitoController controller = Mockito.mock(OrbitoController.class);
        Model model = Mockito.mock(Model.class);
        Pawn pawn = Mockito.mock(Pawn.class);

        Mockito.when(board.GetNbrCol()).thenReturn(4);
        Mockito.when(board.GetNbrRow()).thenReturn(4);
        Mockito.when(board.getElement(0,0)).thenReturn(pawn);
        Mockito.when(board.isElementAt(1,1)).thenReturn(false);
        Mockito.when(pawn.getColor()).thenReturn(1);
        Mockito.when(model.getIdPlayer()).thenReturn(0);

        ActionList actions = Mockito.mock(ActionList.class);

        try (var mockActionFactory = Mockito.mockStatic(ActionFactory.class)) {
            mockActionFactory.when(() -> ActionFactory.generateMoveWithinContainer(
                    eq(model), eq(pawn), eq(0), eq(1)
            )).thenReturn(actions);

            boolean result = controller.moveMarble("A1A2");

            Assertions.assertTrue(result);

            Mockito.verify(board).getElement(0,0);
            Mockito.verify(board).getElement(1,1);

            Mockito.verify(pawn).getColor();
            Mockito.verify(model).getIdPlayer();

            Mockito.verify(actions).setDoEndOfTurn(false);
            mockActionFactory.verify(() -> ActionFactory.generateMoveWithinContainer(
                    model, pawn, 0, 1
            ));

            Mockito.verify(actions).setDoEndOfTurn(true);

            Mockito.verify(board, Mockito.never()).addElement(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        }
    }

    @Test
    public void testMoveMarbleInvalidCoordinates() {
        OrbitoBoard board = Mockito.mock(OrbitoBoard.class);
        OrbitoController controller = Mockito.mock(OrbitoController.class);

        Mockito.when(board.GetNbrCol()).thenReturn(4);
        Mockito.when(board.GetNbrRow()).thenReturn(4);

        boolean move = controller.moveMarble("E6A2");

        Assertions.assertFalse(move);
        Mockito.verify(board).getElement(0,0);
        Mockito.verify(board, Mockito.never()).isElementAt(anyInt(), anyInt());
    }
}
