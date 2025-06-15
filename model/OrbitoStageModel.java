package model;

import boardifier.model.*;

import java.util.ArrayList;

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
    private int whitePawnsToPlay;

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

    public OrbitoStageModel(String name, Model model) {
        super(name, model);
        int taille = OrbitoStageFactory.getDefaultSize();
        this.nbr_row = taille;
        this.nbr_column = taille;
        this.nbr_align = 4;
        int nbrBilles = (nbr_column * nbr_row) / 2;
        blackPawnsToPlay = nbrBilles;
        whitePawnsToPlay = nbrBilles;
        this.whiteMarbles = new Pawn[whitePawnsToPlay];
        this.blackMarbles = new Pawn[blackPawnsToPlay];
        setupCallbacks();
    }

    public OrbitoBoard getBoard() {
        return board;
    }
    public void setBoard(OrbitoBoard board) {
        this.board = board;
        addContainer(board);
    }

    public OrbitoMarblePot getWhitePot() {
        return whitePot;
    }
    public void setWhitePot(OrbitoMarblePot whitePot) {
        this.whitePot = whitePot;
        addContainer(whitePot);
    }

    public OrbitoMarblePot getBlackPot() {
        return blackPot;
    }
    public void setBlackPot(OrbitoMarblePot blackPot) {
        this.blackPot = blackPot;
        addContainer(blackPot);
    }

    public Pawn[] getWhiteMarbles() {
        return whiteMarbles;
    }
    public void setWhiteMarbles(Pawn[] whitePawns) {
        this.whiteMarbles = whitePawns;
        for(int i=0;i<whitePawns.length;i++) {
            addElement(whitePawns[i]);
        }
    }

    public Pawn[] getBlackMarbles() {
        return this.blackMarbles;
    }
    public void setBlackMarbles(Pawn[] blackPawns) {
        this.blackMarbles = blackPawns;
        for(int i=0;i<blackPawns.length;i++) {
            addElement(blackPawns[i]);
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
            } else {
                whitePawnsToPlay--;
            }
        });
    }
    /*
    étend une matrice de 1 en y et x , filler détermine ce qui est utilisé pour remplir l'espace créé
     */
    private ArrayList<ArrayList<Character>> extend_matrix(){
        char filler='X';
        int taille=getBoard().getNbCols();
        ArrayList<ArrayList<Character>> etendue=new ArrayList<>();
        ArrayList<Character> debut=new ArrayList<>();
        ArrayList<Character> fin=new ArrayList<>();

        for (int s=0;s<taille+2;s++){
            debut.add(filler);
        }
        etendue.add(debut);

        for (int i=0;i<taille;i++){
            ArrayList<Character> ajout=new ArrayList<>();
            ajout.add(filler);
            for (int x=0;x<taille;x++){
               getBoard().getElement(i,x);
                // remplacer par original.get(i).get(x)
            }
            ajout.add(filler);
            etendue.add(ajout);
        }

        for (int s=0;s<taille+2;s++){
            fin.add('X');
        }
        etendue.add(fin);

        return etendue;
    }

    public void computePartyResult() {
        int idWinner = -1;

        boolean blackWin = check_align(Pawn.PAWN_BLACK);
        boolean whiteWin = check_align(Pawn.PAWN_WHITE);

        if (blackWin && whiteWin) {
            model.setIdWinner(2);
            model.stopStage();
        } else {
            if (blackWin) {
                idWinner = Pawn.PAWN_BLACK;
                model.setIdWinner(idWinner);
                model.stopStage();
            } else if (whiteWin) {
                idWinner = Pawn.PAWN_WHITE;
                model.setIdWinner(idWinner);
                model.stopStage();
            }
        }

        if (blackPawnsToPlay == 0 && whitePawnsToPlay == 0) {
            model.stopStage();
        }
    }

    private boolean check_align(int id_player) {
        int size = getBoard().getNbCols();
        for (int row = 0; row < size - nbr_align + 1; row++) {
            for (int col = 0; col < size - nbr_align + 1; col++) {
                Pawn[] line = new Pawn[nbr_align];
                for (int i = 0; i < nbr_align; i++) {
                    line[i] = (Pawn) getBoard().getElement(row + i, col + i);
                }

                if (check_line(line, id_player)) {
                    return true;
                }
            }
        }

        for (int row = 0; row < size - nbr_align + 1; row++) {
            for (int col = 0; col < size - nbr_align + 1; col++) {
                Pawn[] line = new Pawn[nbr_align];
                for (int i = 0; i < nbr_align; i++) {
                    line[i] = (Pawn) getBoard().getElement(row + i, col + (nbr_align - 1 - i));
                }

                if (check_line(line, id_player)) {
                    return true;
                }
            }
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size - nbr_align + 1; col++) {
                Pawn[] line = new Pawn[nbr_align];
                for (int i = 0; i < nbr_align; i++) {
                    line[i] = (Pawn) getBoard().getElement(row, col + i);
                }

                if (check_line(line, id_player)) {
                    return true;
                }
            }
        }

        for (int row = 0; row < size - nbr_align + 1; row++) {
            for (int col = 0; col < size; col++) {
                Pawn[] line = new Pawn[nbr_align];
                for (int i = 0; i < nbr_align; i++) {
                    line[i] = (Pawn) getBoard().getElement(row + i, col);
                }

                if (check_line(line, id_player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check_line(Pawn[] line, int id_player) {
        for (Pawn pawn : line) {
            if (pawn == null || pawn.getColor() != id_player) {
                return false;
            }
        }
        return true;
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        OrbitoStageFactory ostf=new OrbitoStageFactory(this);
        ostf.set_nbr_column(this.nbr_column);
        ostf.set_nbr_row(this.nbr_row);
        ostf.setNbr_align(this.nbr_align);
        return ostf;
    }
}
