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
        blackPawnsToPlay = 8;
        whitePawnsToPlay = 8;
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
                whitePawnsToPlay--;
            }
            computePartyResult();

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
    //todo: gérer le nombre de billes d'alignement gagnant
    private void computePartyResult() {
        int idWinner = -1;
        ArrayList<ArrayList<Character>> arr=this.extend_matrix();
        int nbr_aligner=5;
        for (int y=1;y<arr.size()-1;y++){
            for (int x=1;x<arr.get(y).size()-1;x++){
                ArrayList<ArrayList> vecteur=new ArrayList<>();
                String current=arr.get(y).get(x).toString();
                ArrayList<Integer> temp;
                //bas droite
                if (current.equals(arr.get(y+1).get(x+1))) {
                    temp=new ArrayList<>();
                    temp.add(1,1);
                    vecteur.add(temp);
                }
                // bas gauche
                if (current.equals(arr.get(y+1).get(x-1))) {
                    temp=new ArrayList<>();
                    temp.add(1,-1);
                    vecteur.add(temp);
                }
                //haut droite
                if (current.equals(arr.get(y-1).get(x+1))) {
                    temp=new ArrayList<>();
                    temp.add(-1,1);
                    vecteur.add(temp);
                }
                //haut gauche
                if (current.equals(arr.get(y-1).get(x-1))) {
                    temp=new ArrayList<>();
                    temp.add(-1,-1);
                    vecteur.add(temp);
                }
                //haut
                if (current.equals(arr.get(y-1).get(x))) {
                    temp=new ArrayList<>();
                    temp.add(-1,0);
                    vecteur.add(temp);
                }
                //bas
                if (current.equals(arr.get(y+1).get(x))) {
                    temp=new ArrayList<>();
                    temp.add(1,0);
                    vecteur.add(temp);
                }
                //droite
                if (current.equals(arr.get(y).get(x+1))) {
                    temp=new ArrayList<>();
                    temp.add(0,1);
                    vecteur.add(temp);
                }
                //gauche
                if (current.equals(arr.get(y).get(x-1))) {
                    temp=new ArrayList<>();
                    temp.add(0,-1);
                    vecteur.add(temp);
                }
                for (int vi=0;vi<vecteur.size();vi++){
                    ArrayList<Integer> v=vecteur.get(vi);
                    int nbr=2;
                    int tx=x+v.get(1);
                    int ty=y+v.get(0);
                    char new_current=arr.get(tx).get(ty);
                    for (int r=0;r<nbr_aligner-2;r++){
                        if ((((ty+v.get(0)<6) && (tx+v.get(1)<6) && (tx+v.get(1)>=0) && (ty+v.get(0)>=0) && arr.get(ty+v.get(0)).get(tx+v.get(1)).equals(current)))){
                            nbr++;
                            ty+=v.get(0);
                            tx+=v.get(1);
                            new_current=arr.get(tx).get(ty);
                            if (nbr==nbr_aligner){
                                idWinner=Integer.valueOf(current);
                            }
                        }
                    }
                }
            }
        }
        //((Pawn)getBoard().getElement(x, y)).getColor()
        model.setIdWinner(idWinner);
        // stop the game
        model.stopStage();
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        OrbitoStageFactory ostf=new OrbitoStageFactory(this);
        ostf.set_nbr_column(this.nbr_column);
        ostf.set_nbr_row(this.nbr_row);
        return ostf;
    }
}
