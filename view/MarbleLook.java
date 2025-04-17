package view;

import boardifier.model.GameElement;
import boardifier.view.ElementLook;

public class MarbleLook extends ElementLook {
    public MarbleLook(GameElement element) {
        super(element,1,1);
    }
    public void render(){
        shape[0][0]="O";
    };
}
