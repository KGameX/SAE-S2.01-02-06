import boardifier.control.*;
import boardifier.model.*;
import boardifier.view.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Entrez le nombre de billes à aligner");
        int n = s.nextInt();
        s.nextLine();
        Model m = new Model();
        View v = new View(m);
        Controller c = new Controller(m,v);
    }
}
