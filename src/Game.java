
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Game {
    Scanner scanner;
    boolean isAmountOfQuestionsOk;
    String playerName;
    String userChoice;
    boolean shouldContinue;
    Question currentQuestion;
    List<Question> questionList;
    HashMap<Integer, Integer> rateValues;
    int numberOfQuestions;
    List<String> lifebuoys = new ArrayList<>();
    boolean isAvailableLifebuoy1;
    boolean isAvailableLifebuoy2;
    boolean isAvailableLifebuoy3;
    int currentWin;
    String msgArg;

    public Game() {
        cls();
        questionList = new ArrayList<>();
        isAvailableLifebuoy1 = true;
        isAvailableLifebuoy2 = true;
        isAvailableLifebuoy3 = true;
        shouldContinue = true;
        currentWin = 0;
        numberOfQuestions = 1;
        msgArg = "ok";
        currentQuestion = new Question();
        scanner = new Scanner(System.in);
        rateValues = new HashMap<>();
        rateValues.put(1, 500);
        rateValues.put(2, 1000);
        rateValues.put(3, 2000);
        rateValues.put(4, 5000);
        rateValues.put(5, 10000);
        rateValues.put(6, 40000);
        rateValues.put(7, 125000);
        rateValues.put(8, 250000);
        rateValues.put(9, 500000);
        rateValues.put(10, 1000000);
        lifebuoys.add("50/50");
        lifebuoys.add("sw.");
        lifebuoys.add("?");
    }

    ///////////////////////////////////////////////////////////// START

    public void run() {
        initialScreen();
        downloadQuestions();
        shuffleAnswers();
        if (isAmountOfQuestionsOk == true) {
            while (shouldContinue) {
                cls();
                currentQuestion = questionList.get(numberOfQuestions - 1);
                showMessages(msgArg);
                showGameScreen();
                userChoice = scanner.nextLine();
                switch (userChoice) {
                    case "a" -> answeredQuestion("a");
                    case "b" -> answeredQuestion("b");
                    case "c" -> answeredQuestion("c");
                    case "d" -> answeredQuestion("d");
                    case "1" -> {
                        if (isAvailableLifebuoy1) {
                            fiftyFifty();
                        } else {
                            msgArg = "no lifebuoy";
                        }
                    }
                    case "2" -> {
                        if (isAvailableLifebuoy2) {
                            switchTheQuestion();
                        } else {
                            msgArg = "no lifebuoy";
                        }
                    }
                    case "3" -> {
                        if (isAvailableLifebuoy3) {
                            askTheAudience();
                        } else {
                            msgArg = "no lifebuoy";
                        }
                    }
                    case "0" -> leaveTheGame();
                    default -> msgArg = "bad input";
                }
            }
        } else {
            cls();
            System.out.println("At least 12 questions were not found in the database.");
            System.out.println("Game cannot start.");
            scanner.nextLine();
        }
    }

    //////////////////////////////////////////////////////////// END

    public void downloadQuestions() {
        String dbURL = "jdbc:mysql://localhost:3306/millionaires";
        String user = "root";
        String password = "jnsp1";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Question downloadingQuestion;
        int amountOfQuestions = 0;
        try {
            connection = Database.getConnection(dbURL,user,password);
            statement = connection.createStatement();
            String sqlCountQuestions = "SELECT COUNT(question) AS amount FROM millionaires.questions;";

            resultSet = statement.executeQuery(sqlCountQuestions);
            resultSet.next();
            amountOfQuestions = resultSet.getInt("amount");
            System.out.println(amountOfQuestions);

            String sqlSelectOneQuestion = "SELECT * FROM millionaires.questions WHERE id=?;";
            preparedStatement = connection.prepareStatement(sqlSelectOneQuestion);

            if (amountOfQuestions > 11) {
                List<Integer> drawnIndices = new ArrayList<>();
                for (int i = 0; i < amountOfQuestions; i++) {
                    drawnIndices.add(i+1);
                }
                Collections.shuffle(drawnIndices);
                for (int i = 0; i < 11; i++) {

                        preparedStatement.setInt(1,drawnIndices.get(i));
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        
                        downloadingQuestion = new Question();
                        downloadingQuestion.question = resultSet.getString("question");
                        downloadingQuestion.answerA = resultSet.getString("answer_a");
                        downloadingQuestion.answerB = resultSet.getString("answer_b");
                        downloadingQuestion.answerC = resultSet.getString("answer_c");
                        downloadingQuestion.answerD = resultSet.getString("answer_d");
                        downloadingQuestion.correctAnswer = resultSet.getString("correct_answer");

                        questionList.add(downloadingQuestion);
                }
                isAmountOfQuestionsOk = true;
            } else {
                isAmountOfQuestionsOk = false;
            }
            //////////////////////////END

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            Database.closeResultSet(resultSet);
            Database.closeStatement(statement);
            Database.closeStatement(preparedStatement);
            Database.closeConnection(connection);
        }
    }

    public void showGameScreen() {
        System.out.println("Question for " + rateValues.get(numberOfQuestions) + "$");
        System.out.println(numberOfQuestions + ". " + currentQuestion.question);
        System.out.println("a) " + currentQuestion.answerA);
        System.out.println("b) " + currentQuestion.answerB);
        System.out.println("c) " + currentQuestion.answerC);
        System.out.println("d) " + currentQuestion.answerD);
        System.out.println("\nLifebuoys available:");
        int size = lifebuoys.size();
        for (int i = 0; i < size; i++) {
            System.out.print(lifebuoys.get(i));
            System.out.print("  ");
        }
        System.out.println("\n\n1. Use 50/50 lifebuoy.");
        System.out.println("2. Switch the question.");
        System.out.println("3. Ask the audience.");
        System.out.println("\n0. Leave the game.");
        System.out.println("\nWhat's your decision?");
    }

    public void leaveTheGame() {
        cls();
        shouldContinue = false;
        System.out.println("Congratulations " + playerName + "!");
        System.out.println("You won " + currentWin + "$");
        System.out.println("Press Enter to comeback to main menu");
        scanner.nextLine();
    }

    public void fiftyFifty() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            cls();
            System.out.println("Are you sure to use 50/50?");
            System.out.println("1. YES");
            System.out.println("2. NO");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    String apartFrom = currentQuestion.correctAnswer;
                    List<String> abcd = new ArrayList<>();
                    abcd.add("a");
                    abcd.add("b");
                    abcd.add("c");
                    abcd.add("d");
                    switch (apartFrom) {
                        case "a" -> abcd.remove("a");
                        case "b" -> abcd.remove("b");
                        case "c" -> abcd.remove("c");
                        case "d" -> abcd.remove("d");
                    }
                    Collections.shuffle(abcd);
                    for (int i = 0; i < 2; i++) {
                        if (abcd.get(i).equals("a")) {
                            currentQuestion.answerA = "";
                        } else if (abcd.get(i).equals("b")) {
                            currentQuestion.answerB = "";
                        } else if (abcd.get(i).equals("c")) {
                            currentQuestion.answerC = "";
                        } else if (abcd.get(i).equals("d")) {
                            currentQuestion.answerD = "";
                        }
                    }
                    isAvailableLifebuoy1 = false;
                    lifebuoys.remove("50/50");
                    shouldContinue = false;
                }
                case "2" -> {
                    shouldContinue = false;
                }
            }
        }
    }

    public void switchTheQuestion() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            cls();
            System.out.println("Are you sure to use switch the question lifebuoy?");
            System.out.println("1. YES");
            System.out.println("2. NO");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    questionList.get(numberOfQuestions - 1).question = questionList.get(10).question;
                    questionList.get(numberOfQuestions - 1).answerA = questionList.get(10).answerA;
                    questionList.get(numberOfQuestions - 1).answerB = questionList.get(10).answerB;
                    questionList.get(numberOfQuestions - 1).answerC = questionList.get(10).answerC;
                    questionList.get(numberOfQuestions - 1).answerD = questionList.get(10).answerD;
                    questionList.get(numberOfQuestions - 1).correctAnswer = questionList.get(10).correctAnswer;

                    isAvailableLifebuoy2 = false;
                    lifebuoys.remove("sw.");
                    shouldContinue = false;
                }
                case "2" -> {
                    shouldContinue = false;
                }
            }
        }
    }

    public void askTheAudience() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            cls();
            System.out.println("Are you sure you want to ask the audience for help?");
            System.out.println("1. YES");
            System.out.println("2. NO");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    Random random = new Random();
                    int percentageAmount = 100;
                    int aPercentage = 0;
                    int bPercentage = 0;
                    int cPercentage = 0;
                    int dPercentage = 0;

                    int correctPercentage = random.nextInt(76) + 20;
                    percentageAmount -= correctPercentage;

                    List<Integer> incorrectPercentage = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
                        Integer temp = random.nextInt(percentageAmount + 1);
                        percentageAmount -= temp;
                        incorrectPercentage.add(temp);
                    }
                    incorrectPercentage.add(percentageAmount);
                    Collections.shuffle(incorrectPercentage);

                    switch (currentQuestion.correctAnswer) {
                        case "a" -> {
                            aPercentage = correctPercentage;
                            bPercentage = incorrectPercentage.get(0);
                            cPercentage = incorrectPercentage.get(1);
                            dPercentage = incorrectPercentage.get(2);
                        }
                        case "b" -> {
                            aPercentage = incorrectPercentage.get(0);
                            bPercentage = correctPercentage;
                            cPercentage = incorrectPercentage.get(1);
                            dPercentage = incorrectPercentage.get(2);
                        }
                        case "c" -> {
                            aPercentage = incorrectPercentage.get(0);
                            bPercentage = incorrectPercentage.get(1);
                            cPercentage = correctPercentage;
                            dPercentage = incorrectPercentage.get(2);
                        }
                        case "d" -> {
                            aPercentage = incorrectPercentage.get(0);
                            bPercentage = incorrectPercentage.get(1);
                            cPercentage = incorrectPercentage.get(2);
                            dPercentage = correctPercentage;
                        }
                    }

                    //wyświetlenie histogramu
                    cls();
                    System.out.println("Question for " + rateValues.get(numberOfQuestions) + "$");
                    System.out.println(numberOfQuestions + ". " + currentQuestion.question);
                    System.out.println("a) " + currentQuestion.answerA);
                    System.out.println("b) " + currentQuestion.answerB);
                    System.out.println("c) " + currentQuestion.answerC);
                    System.out.println("d) " + currentQuestion.answerD);
                    System.out.println("\nAudience vote:");

                    System.out.print("a -> ");
                    for (int i=0; i<aPercentage; i++) {
                        System.out.print("|");
                    }
                    System.out.print("  "+aPercentage+"%\n");

                    System.out.print("b -> ");
                    for (int i=0; i<bPercentage; i++) {
                        System.out.print("|");
                    }
                    System.out.print("  "+bPercentage+"%\n");

                    System.out.print("c -> ");
                    for (int i=0; i<cPercentage; i++) {
                        System.out.print("|");
                    }
                    System.out.print("  "+cPercentage+"%\n");

                    System.out.print("d -> ");
                    for (int i=0; i<dPercentage; i++) {
                        System.out.print("|");
                    }
                    System.out.print("  "+dPercentage+"%");

                    isAvailableLifebuoy3 = false;
                    lifebuoys.remove("?");
                    shouldContinue = false;

                    scanner.nextLine();
                }
                case "2" -> {
                    shouldContinue = false;
                }
            }
        }
    }

    public void initialScreen() {
        boolean nameIsOk = false;
        while (!nameIsOk) {
            System.out.println("Hello!");
            System.out.println("What's your name?");
            playerName = scanner.nextLine();
            if (!(playerName.isBlank() || playerName.isEmpty())) {
                nameIsOk = true;
            } else {
                cls();
                System.out.println("Name cannot be empty!\n");
            }
        }
        cls();
        System.out.println("Welcome " + playerName);
        System.out.println("Good luck! Press Enter to see the first question!");
        playerName = scanner.nextLine();
    }

    public void showMessages(String msgArg) {
        if (msgArg.equals("ok")) {
            System.out.println("No messages and alerts");
            System.out.println("\n");
        } else if (msgArg.equals("bad input")) {
            System.out.println("Invalid input parameter used.");
            System.out.println("\n");
        } else if (msgArg.equals("empty question")) {
            System.out.println("The selected answer has been rejected.");
            System.out.println("\n");
        } else if (msgArg.equals("no lifebuoy")) {
            System.out.println("This lifebuoy has been already used.");
            System.out.println("\n");
        }
        this.msgArg = "ok";
    }

    public void answeredQuestion(String answer) {
        if (isAnswerEmpty(answer)) {
            msgArg = "empty question";
        } else {
            if (answer.equals(currentQuestion.correctAnswer)) {
                cls();
                //checking win condition
                if (numberOfQuestions == 10) {
                    System.out.println("Congratulations! you answered properly for all 10 question!");
                    System.out.println("You are a Millionaire!!!");
                    shouldContinue = false;
                    System.out.println("\nPress Enter to comeback to main menu");
                    scanner.nextLine();
                } else {
                    currentWin = rateValues.get(numberOfQuestions);
                    numberOfQuestions += 1;
                    System.out.println("Good job! That's a good answer!");
                    System.out.println("Your current win is: " + currentWin+"$");
                    scanner.nextLine();
                }
            } else {
                cls();
                if (currentWin >= 40000) {
                    currentWin = 40000;
                } else if (currentWin >= 1000) {
                    currentWin = 1000;
                } else {
                    currentWin = 0;
                }
                System.out.println("Unfortunately this is not good answer for question:");
                System.out.println(currentQuestion.question);
                System.out.print("Correct answer is "+currentQuestion.correctAnswer+") ");
                switch (currentQuestion.correctAnswer) {
                    case "a" -> System.out.print(currentQuestion.answerA);
                    case "b" -> System.out.print(currentQuestion.answerB);
                    case "c" -> System.out.print(currentQuestion.answerC);
                    case "d" -> System.out.print(currentQuestion.answerD);
                }
                System.out.println("\n");
                System.out.println("Game over, you won: " + currentWin + "$");
                shouldContinue = false;
                System.out.println("Press Enter to comeback to main menu");
                scanner.nextLine();
            }
        }
    }

    public boolean isAnswerEmpty(String answer) {
        boolean temp = false;
        switch (answer) {
            case "a" -> {
                if (currentQuestion.answerA.equals("")) {
                    temp = true;
                }
            }
            case "b" -> {
                if (currentQuestion.answerB.equals("")) {
                    temp = true;
                }
            }
            case "c" -> {
                if (currentQuestion.answerC.equals("")) {
                    temp = true;
                }
            }
            case "d" -> {
                if (currentQuestion.answerD.equals("")) {
                    temp = true;
                }
            }
        }
        return temp;
    }

    public void shuffleAnswers() {
        String correct = "";
        List<String> bufor = new ArrayList<>();

        for (Question question: questionList) {
            switch (question.correctAnswer) {
                case "a" -> correct = question.answerA;
                case "b" -> correct = question.answerB;
                case "c" -> correct = question.answerC;
                case "d" -> correct = question.answerD;
            }
            bufor.add(question.answerA);
            bufor.add(question.answerB);
            bufor.add(question.answerC);
            bufor.add(question.answerD);

            Collections.shuffle(bufor);

            if (correct.equals(bufor.get(0))) {
                question.correctAnswer = "a";
            } else if (correct.equals(bufor.get(1))) {
                question.correctAnswer = "b";
            } else if (correct.equals(bufor.get(2))) {
                question.correctAnswer = "c";
            } else if (correct.equals(bufor.get(3))) {
                question.correctAnswer = "d";
            }

            question.answerA = bufor.get(0);
            question.answerB = bufor.get(1);
            question.answerC = bufor.get(2);
            question.answerD = bufor.get(3);

            bufor.clear();
        }
    }

    public static void cls() {
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {
        }
    }
}
