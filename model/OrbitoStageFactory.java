package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;

/**
 * HoleStageFactory must create the game elements that are defined in HoleStageModel
 * WARNING: it just creates the game element and NOT their look, which is done in HoleStageView.
 *
 * If there must be a precise position in the display for the look of a game element, then this element must be created
 * with that position in the virtual space and MUST NOT be placed in a container element. Indeed, for such
 * elements, the position in their virtual space will match the position on the display. For example, in the following,
 * the black pot is placed in 18,0. When displayed on screen, the top-left character of the black pot will be effectively
 * placed at column 18 and row 0.
 *
 * Otherwise, game elements must be put in a container and it will be the look of the container that will manage
 * the position of element looks on the display. For example, pawns are put in a ContainerElement. Thus, their virtual space is
 * in fact the virtual space of the container and their location in that space in managed by boardifier, depending of the
 * look of the container.
 *
 */
public class OrbitoStageFactory extends StageElementsFactory {
    private OrbitoStageModel stageModel;
    protected int nbr_column;
    protected int nbr_row;
    private static int defaultSize = 4;
    public static int nbr_align;

    public OrbitoStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (OrbitoStageModel) gameStageModel;
        if (nbr_column == 0) {
            this.nbr_column = defaultSize;
            this.nbr_row = defaultSize;
        }
    }

    public static int getNbr_align() {
        return OrbitoStageFactory.nbr_align;
    }

    public static void setNbr_align(int nbr_align) {
        OrbitoStageFactory.nbr_align = nbr_align;
    }

    public static void setDefaultSize(int taille) {
        defaultSize = taille;
    }

    public static int getDefaultSize() {
        return defaultSize;
    }

    public void set_nbr_column(int nbr_column) {
        this.nbr_column = nbr_column;
    }
    public void set_nbr_row(int nbr_row) {
        this.nbr_row = nbr_row;
    }
    public int get_nbr_column() {
        return nbr_column;
    }
    public int get_nbr_row() {
        return nbr_row;
    }

    @Override
    public void setup() {

        if (nbr_column == 0) {
            this.nbr_column = defaultSize;
            this.nbr_row = defaultSize;
        }

        // create the text that displays the player name and put it in 0,0 in the virtual space
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(0,0);
        stageModel.setPlayerName(text);

        // create the board, in 0,1 in the virtual space
        OrbitoBoard board = new OrbitoBoard(0, 1 ,this.nbr_row,this.nbr_column,stageModel);
        board.set_nbr_row(this.nbr_row);
        board.set_nbr_col(this.nbr_column);
        // assign the board to the game stage model
        stageModel.setBoard(board);

        int whitePotX = this.nbr_column * 7;
        int blackPotX = this.nbr_column * 7 + 5;

        int nbr_billes = (this.nbr_column * this.nbr_row) / 2;

        OrbitoMarblePot marblePotWhite = new OrbitoMarblePot(whitePotX,2, nbr_billes,1,stageModel);
        OrbitoMarblePot marblePotBlack = new OrbitoMarblePot(blackPotX,2, nbr_billes,1, stageModel);

        stageModel.setWhitePot(marblePotWhite);
        stageModel.setBlackPot(marblePotBlack);

        stageModel.setNbr_align(OrbitoStageFactory.nbr_align);
        stageModel.setNbr_column(defaultSize);
        stageModel.setNbr_row(defaultSize);

        /* create the pawns
            NB: their coordinates are by default 0,0 but since they are put
            within the pots, their real coordinates will be computed by the view
         */
        Pawn[] WhiteMarbles = new Pawn[nbr_billes];
        for(int i=0;i<nbr_billes;i++) {
            WhiteMarbles[i] = new Pawn(i + 1, Pawn.PAWN_WHITE, stageModel);
        }
        // assign the black pawns to the game stage model
        stageModel.setWhiteMarbles(WhiteMarbles);

        Pawn[] BlackMarbles = new Pawn[nbr_billes];
        for(int i=0;i<nbr_billes;i++) {
            BlackMarbles[i] = new Pawn(i + 1, Pawn.PAWN_BLACK, stageModel);
        }
        // assign the black pawns to the game stage model
        stageModel.setBlackMarbles(BlackMarbles);

        // finally put the pawns to their pot
        for (int i=0;i<nbr_billes;i++) {
            marblePotWhite.addElement(WhiteMarbles[i], i,0);
            marblePotBlack.addElement(BlackMarbles[i], i,0);
        }

        /* Example with a main container that takes the ownership of the location
           of the element that are put within.
           If we put text, board, black/red pots within this container, their initial
           location in the virtual space is no more relevant.
           In such a case, we also need to create a look for the main container, see HoleStageView
           comment at the end of the class.

        // create the main container with 2 rows and 3 columns, in 0,0 in the virtual space
        ContainerElement mainContainer = new ContainerElement("rootcontainer",0,0,2,3, stageModel);
        // for cell 0,1, span over the row below => the column 1 goes from top to bottom of the container
        mainContainer.setCellSpan(0,1,2,1);
        // for cell 0,2, span over the row below => the column 2 goes from top to bottom of the container
        mainContainer.setCellSpan(0,2,2,1);
        // assign the
        stageModel.setMainContainer(mainContainer);
        // assign elements to main container cells
        mainContainer.addElement(text,0,0);
        mainContainer.addElement(board, 1,0);
        mainContainer.addElement(blackPot,0,1);
        mainContainer.addElement(redPot,0,2);
        */
    }
}
