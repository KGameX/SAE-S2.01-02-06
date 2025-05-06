package model;

import boardifier.model.*;

/**
 * HoleStageModel defines the model for the single stage in "The Hole". Indeed,
 * there are no levels in this game: a party starts and when it's done, the game is also done.
 *
 * HoleStageModel must define all that is needed to manage a party : state variables and game elements.
 * In the present case, there are only 2 state variables that represent the number of pawns to play by each player.
 * It is used to detect the end of the party.
 * For game elements, it depends on what is chosen as a final UI design. For that demo, there are 12 elements used
 * to represent the state : the main board, 2 pots, 8 pawns, and a text for current player.
 *
 * WARNING ! HoleStageModel DOES NOT create itself the game elements because it would prevent the possibility to mock
 * game element classes for unit testing purposes. This is why HoleStageModel just defines the game elements and the methods
 * to set this elements.
 * The instanciation of the elements is done by the HoleStageFactory, which uses the provided setters.
 *
 * HoleStageModel must also contain methods to check/modify the game state when given events occur. This is the role of
 * setupCallbacks() method that defines a callback function that must be called when a pawn is put in a container.
 * This is done by calling onPutInContainer() method, with the callback function as a parameter. After that call, boardifier
 * will be able to call the callback function automatically when a pawn is put in a container.
 * NB1: callback functions MUST BE defined with a lambda expression (i.e. an arrow function).
 * NB2:  there are other methods to defines callbacks for other events (see onXXX methods in GameStageModel)
 * In "The Hole", everytime a pawn is put in the main board, we have to check if the party is ended and in this case, who is the winner.
 * This is the role of computePartyResult(), which is called by the callback function if there is no more pawn to play.
 *
 */
public class OrbitoStageModel extends GameStageModel {

    // define stage state variables
    private int blackPawnsToPlay;
    private int redPawnsToPlay;

    // define stage game elements
    private OrbitoBoard board;
    private OrbitoMarblePot whitePot;
    private OrbitoMarblePot blackPot;
    private Pawn[] whiteMarbles;
    private Pawn[] blackMarbles;
    private TextElement playerName;
    protected int nbr_column;
    protected int nbr_row;
    protected int nbr_align;
    protected boolean[] rotation;

    /**
     *
     * @param rotation définit la rotation des anneaux , si true sens horaire , si false antihoraire
     *                 en partant de l'anneau extérieur
     */
    public void setRotation(boolean[] rotation) {
        this.rotation = rotation;
    }
    public boolean[] getRotation() {
        return rotation;
    }
    public int getNbr_align() {
        return this.nbr_align;
    }
    public void setNbr_align(int nbr_align) {
        this.nbr_align = nbr_align;
    }
    public void setNbr_column(int nbr_column) {
        this.nbr_column = nbr_column;
    }
    public void setNbr_row(int nbr_row) {
        this.nbr_row = nbr_row;
    }
    public int getNbr_column() {
        return this.nbr_column;
    }
    public int getNbr_row() {
        return this.nbr_row;
    }
    // Uncomment next line if the example with a main container is used. see end of HoleStageFactory and HoleStageView
    //private ContainerElement mainContainer;

    public OrbitoStageModel(String name, Model model) {
        super(name, model);
        setupCallbacks();
    }

    //Uncomment this 2 methods if example with a main container is used
    /*
    public ContainerElement getMainContainer() {
        return mainContainer;
    }

    public void setMainContainer(ContainerElement mainContainer) {
        this.mainContainer = mainContainer;
        addContainer(mainContainer);
    }
     */

    public OrbitoBoard getBoard() {
        return board;
    }
    public void setBoard(OrbitoBoard board) {
        this.board = board;
        addContainer(board);
    }

    public OrbitoMarblePot getWhitePot() {
        return blackPot;
    }
    public void setWhitePot(OrbitoMarblePot whitePot) {
        this.whitePot = whitePot;
        addContainer(whitePot);
    }

    public OrbitoMarblePot getBlackPot() {
        return whitePot;
    }
    public void setBlackPot(OrbitoMarblePot blackPot) {
        this.blackPot = blackPot;
        addContainer(blackPot);
    }

    public Pawn[] getWhiteMarbles() {
        return whiteMarbles;
    }
    public void setWhiteMarbles(Pawn[] blackPawns) {
        this.whiteMarbles = blackPawns;
        for(int i=0;i<blackPawns.length;i++) {
            addElement(blackPawns[i]);
        }
    }

    public Pawn[] getBlackMarbles() {
        return blackMarbles;
    }
    public void setBlackMarbles(Pawn[] redPawns) {
        this.blackMarbles = redPawns;
        for(int i=0;i<redPawns.length;i++) {
            addElement(redPawns[i]);
        }
    }

    public TextElement getPlayerName() {
        return playerName;
    }
    public void setPlayerName(TextElement playerName) {
        this.playerName = playerName;
        addElement(playerName);
    }


    private void setupCallbacks() {
        onPutInContainer( (element, gridDest, rowDest, colDest) -> {
            // just check when pawns are put in 3x3 board
            if (gridDest != board) return;
            Pawn p = (Pawn) element;
            if (p.getColor() == 0) {
                blackPawnsToPlay--;
            }
            else {
                redPawnsToPlay--;
            }
            computePartyResult();

        });
    }
    //todo: gérer le nombre de billes d'alignement gagnant
    private void computePartyResult() {
        int idWinner = -1;
        // get the empty cell, which should be in 2D at [0,0], [0,2], [1,1], [2,0] or [2,2]
        // i.e. or in 1D at index 0, 2, 4, 6 or 8
//        for (int x = 0; x <this.nbr_column; x++){
//            for (int y = 0; y <this.nbr_row; y++){
//                Pawn cur_ele=(Pawn)getBoard().getElement(x, y);
//                if (x ==0 && y ==0){
//                    //vérification horizontale
//                    for (int d=0;d<this.nbr_column;d++){
//                        if (((Pawn)getBoard().getElement(d, y)).getColor()!=cur_ele.getColor()){
//                            break;
//                        }
//                        if (d==nbr_column){
//                            idWinner=cur_ele.getColor();
//                        }
//                    }
//                    //vérification verticale
//                    for (int d=0;d<this.nbr_column;d++){
//                        if (((Pawn)getBoard().getElement(x,d)).getColor()!=cur_ele.getColor()){
//                            break;
//                        }
//                        if (d==nbr_column){
//                            idWinner=cur_ele.getColor();
//                        }
//                    }
//                    //vérification diagonale
//                    for (int d=0;d<this.nbr_column;d++){
//                        if (((Pawn)getBoard().getElement(d,d)).getColor()!=cur_ele.getColor()){
//                            break;
//                        }
//                        if (d==nbr_column){
//                            idWinner=cur_ele.getColor();
//                        }
//                    }
//                    //------------------------------Bottom Right corner-----------------------------------------
//                    if (x ==0 && y ==nbr_row-1) {
//                        //vérification horizontale
//                        for (int d = 0; d < this.nbr_column; d++) {
//                            if (((Pawn) getBoard().getElement(x, d)).getColor() != cur_ele.getColor()) {
//                                break;
//                            }
//                            if (d == 0) {
//                                idWinner = cur_ele.getColor();
//                            }
//                        }
//
//                        //vérification verticale
//                        for (int d = nbr_column; d !=0; d--) {
//                            if (((Pawn) getBoard().getElement(x, d)).getColor() != cur_ele.getColor()) {
//                                break;
//                            }
//                            if (d == nbr_column) {
//                                idWinner = cur_ele.getColor();
//                            }
//                        }
//                        //vérification diagonale
//                        for (int d = 0; d < this.nbr_column; d++) {
//                            if (((Pawn) getBoard().getElement(d, d)).getColor() != cur_ele.getColor()) {
//                                break;
//                            }
//                            if (d == nbr_column) {
//                                idWinner = cur_ele.getColor();
//                            }
//                        }
//                    }
//                }
//            }
//        }
        //vérifie s'il y a des alignements horizontaux
        for (int y = 0; y < nbr_row; y++) {
            int x=0;
            while (x!=nbr_column-1){
                Pawn cur_ele=(Pawn)getBoard().getElement(x, y);
                int count=1;
                x+=1;
                while (cur_ele.getColor()==((Pawn)getBoard().getElement(x, y)).getColor()){
                    count++;
                    x+=1;
                }
                //alignement gagnant
                if (count>=4){
                    idWinner=cur_ele.getColor();
                    break;
                }
            }
        }

        for (int y = 0; y < nbr_row; y++) {
            int x=0;
            while (x!=nbr_column-1){
                Pawn cur_ele=(Pawn)getBoard().getElement(y,x);
                int count=1;
                x+=1;
                while (cur_ele.getColor()==((Pawn)getBoard().getElement(y,x)).getColor()){
                    count++;
                    x+=1;
                }
                //alignement gagnant
                if (count>=4){
                    idWinner=cur_ele.getColor();
                    break;
                }
            }
        }
//        liste=[[1,1,1,0,1],
//       [0,0,0,0,0],
//       [0,0,0,0,0],
//       [0,0,0,0,0],
//       [0,0,0,0,0]]
//        for i in range(len(liste)):
//        count=1
//        for l in range(len(liste[i])-1):
//        current=liste[i][l]
//        if (liste[i][l+1]==current):
//        count+=1
//        else:
//        count=0
//
//        print(count)

        for (int i=0; i<nbr_row; i++) {
            int count=1;
            for (int l=0; l<nbr_column-1; l++) {
                Pawn cur_ele=(Pawn)getBoard().getElement(i,l);
                if (cur_ele.getColor()==((Pawn)getBoard().getElement(i,l)).getColor()){
                    count++;
                }
                else {
                    count=0;
                }
                if (count>=this.nbr_align){
                    idWinner=cur_ele.getColor();
                }
            }
        }
        // censé s'occuper de décider qui gagne
        // set the winner
        model.setIdWinner(idWinner);
        // stop de the game
        model.stopStage();
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new OrbitoStageFactory(this,this.nbr_column,this.nbr_row);
    }
}
