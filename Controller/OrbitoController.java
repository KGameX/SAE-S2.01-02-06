package Controller;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OrbitoController extends Controller {
    BufferedReader consoleIn;
    boolean firstPlayer;

    public OrbitoController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
    }
    public void stageLoop() {
        this.consoleIn = new BufferedReader(new InputStreamReader(System.in));
        update();
        while (!model.isEndStage()){
            playTurn();
            endOfTurn();
            update();
        }
        endGame();
    }
    public void playTurn() {
        // get the new player
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            OrbitoDecider decider = new OrbitoDecider(model,this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        }
        else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName()+ " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 3) {
                        ok = analyseAndPlay(line);
                    }
                    if (!ok) {
                        System.out.println("incorrect instruction. retry !");
                    }
                }
                catch(IOException e) {}
            }
        }

    }
}
