package CommandsUtils;

import java.util.Random;

public class RandomJokes {

     private static final String[] joke = {"Lo sapete che per il decreto coronavirus sono vietate le canzoni di Andrea Bocelli.... In particolare Partirò.",
             "Cosa dice uno spaventapasseri bugiardo? Dice le balle di fieno!",
             "Lo sai perché nei ristoranti genovesi non possono vedere il conto? Perché è buio pesto!",
             "Se il cane ringhia, la ringhiera abbaia?",
             "Wonder Woman sei spacciata! - Lo so, sono un'eroina...",
             "Che cosa disse una goccia di sangue cadendo a terra? Oggi non sono in vena...",
             "I've gotten a ton of work done today. a skele-ton",
             "Qual è il contrario di melodia ? Se-lo-tenga",
             "Perché ai carabinieri non fanno mai usare la Panda 4x4 ? Se no cercano di salire in 16",
             "Cosa fa un'ostrica stupida? - Una pirla!",
             "Durante uno spettacolo comico: Bene, ho finito le battute che avevo in serbo. Adesso comincio con quelle in croato.",
             "Che cos'e' un orso polare? E' un orso cartesiano che ha cambiato coordinate",
             "Cosa sono il cono e la piramide per uno studente ignorante? I solidi ignoti.",
             "L'adesivo sulla macchina dei pasticceri: babà a bordo"};

    public static String jokes(){
        Random rand = new Random();
        final int indmaxm = joke.length;
        int indr = rand.nextInt(indmaxm);
        return joke[indr];
    }
}
