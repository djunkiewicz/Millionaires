
import java.io.IOException;
import java.util.Scanner;

public class App {
    Scanner scanner;
    String userChoice;
    boolean shouldContinue = true;
    String msgArg;
    public App() {
        scanner = new Scanner(System.in);
        msgArg = "ok";
    }
    public void run(){
        while(shouldContinue){
            cls();
            showMessages(msgArg);
            showMenu();
            userChoice = scanner.nextLine();
            switch (userChoice){
                case "1" -> {cls();
                    Game game = new Game();
                    game.run();}
                case "2" -> {cls();
                    showRules();}
                case "3" -> shouldContinue = false;
                default -> msgArg = "bad input";
            }
        }
    }
    public void showMenu() {
        System.out.println("Millionaires!");
        System.out.println("1. New game");
        System.out.println("2. Rules");
        System.out.println("3. Exit");
    }

    public void showRules() {
        cls();
        System.out.println("You have to answer 10 question without fail to win!");
        System.out.println("Each question brings you closer to 1 000 000 dollars.");
        System.out.println("You have 3 lifebuoys:");
        System.out.println("50/50 -> computer will delete 2 wrong answers");
        System.out.println("  sw. -> switch the question");
        System.out.println("    ? -> question to the audience");
        System.out.println("Click Enter to back to the main menu.");
        scanner.nextLine();
    }

    public void showMessages(String msgArg) {
        if (msgArg.equals("ok")){
            System.out.println("No messages and alerts");
            System.out.println("\n");
        } else if (msgArg.equals("bad input")) {
            System.out.println("Invalid input parameter used.");
            System.out.println("\n");
        }
        this.msgArg = "ok";
    }


    public static void cls(){
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ignored) {}
    }
}
