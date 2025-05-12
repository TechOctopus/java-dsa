package Test;

import BDD.BDD;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Test {
    public static void start() throws IOException {
        System.out.print("""
                                           
                This program is designed to test the Binary Decision Diagram (BDD) algorithm.
                Made by: Heorhi Davydau, id: 116164
                                           
                """);
        menu();
    }

    private static void menu() throws IOException {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while (option != 6) {
            System.out.print("""
                                        
                    Choose option to test BDD:
                    1. Input test (BDD_create) - test with your b-function, order and input
                    2. Input test (BDD_create_beast_order) - test with your b-function and input, but with beast order
                    3. Truth table test - test with your b-function, order and input
                    4. Reduce test - test reduce percent for random b-functions of different length and orders
                    5. Time test - test time to create BDD for random b-functions of different length and orders
                    6. End
                                        
                    """);
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> inputTest("bdd_create");
                    case 2 -> inputTest("bdd_create_beast_order");
                    case 3 -> tableTest();
                    case 4 -> reduceTest();
                    case 5 -> timeTest();
                    case 6 -> System.out.println("End");
                    default -> System.out.println("Invalid input, please enter a number from 1 to 6");
                }
            } else {
                String invalidInput = scanner.next();
                System.out.println("Invalid input '" + invalidInput + "', please enter a number from 1 to 6");
            }
        }
    }

    private static void inputTest(String test) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("""
                                        
                Start input test
                Enter expression in DNF form, use latin letters in upper case and symbols: +, ! example: ABCE+!C+BCE
                                       
                """);

        String expression = scanner.nextLine();

        String order = null;
        if (test.equals("bdd_create")) {
            if (validateExpression(expression)) {
                System.out.print("""
                                                    
                        Invalid expression
                        End input test
                                                    
                        """);
                return;
            }
            System.out.println("""
                         
                    Enter order, use latin letters in upper case, example: ABCDE
                    """);
            order = scanner.nextLine();
            if (validateOrder(order, expression)) {
                System.out.print("""
                                                    
                        Invalid order
                        End input test
                                                    
                        """);
                return;
            }
        }
        BDD bdd = new BDD();
        if (test.equals("bdd_create")) {
            bdd.create(reformatExpression(expression), order);
        } else {
            bdd.createWithBeastOrder(reformatExpression(expression));
        }
        if (test.equals("bdd_create")) {
            System.out.println("\nEnter input for " + order + " order (example: 1010)\n");
        } else {
            System.out.println("\nEnter input for " + bdd.getOrder() + " order (example: 1010)\n");
        }
        String input = scanner.nextLine();
        if (test.equals("bdd_create")) {
            if (validateInput(input, order)) {
                System.out.print("""
                                                    
                        Invalid input
                        End input test
                                                    
                        """);
                return;
            }
        } else {
            if (validateInput(input, bdd.getOrder())) {
                System.out.print("""
                                                    
                        Invalid input
                        End input test
                                                    
                        """);
                return;
            }
        }

        if (test.equals("bdd_create")) {
            System.out.printf("""
                                                    
                            Result for input test for BDD_create:
                            Expression: %s, Order: %s, Input: %s
                                                    
                            BDD_use result: %s
                            Anticipated result: %s
                                                    
                            BDD before reduction: %s
                            BDD after reduction: %s
                            Average percent of reduction: %.2f%%
                                                        
                            End input test
                                                    
                            """,
                    expression,
                    order,
                    input,
                    bdd.use(input),
                    manualCalculation(expression, order, input),
                    bdd.getNumOfNodesBeforeReduce(),
                    bdd.getNumOfNodesAfterReduce(),
                    (bdd.getNumOfNodesBeforeReduce() - bdd.getNumOfNodesAfterReduce()) / bdd.getNumOfNodesBeforeReduce() * 100
            );
        } else {
            System.out.printf("""
                                                    
                            Result for input test for BDD_create_with_beast_order
                            Expression: %s, Order: %s, Input: %s
                                                    
                            BDD_use result: %s
                            Anticipated result: %s
                                                    
                            BDD before reduction: %s
                            BDD after reduction: %s
                            Average percent of reduction: %.2f%%
                                                    
                            End input test
                                                    
                            """,
                    expression,
                    bdd.getOrder(),
                    input,
                    bdd.use(input),
                    manualCalculation(expression, bdd.getOrder(), input),
                    bdd.getNumOfNodesBeforeReduce(),
                    bdd.getNumOfNodesAfterReduce(),
                    (bdd.getNumOfNodesBeforeReduce() - bdd.getNumOfNodesAfterReduce()) / bdd.getNumOfNodesBeforeReduce() * 100
            );
        }

    }

    private static void tableTest() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("""
                                        
                Start truth table test
                Enter expression in DNF form, use latin letters in upper case and symbols: +, ! example: ABCE+!C+BCE
                                       
                """);

        String expression = scanner.nextLine();

        if (validateExpression(expression)) {
            System.out.print("""
                                                
                    Invalid expression
                    End input test
                                                
                    """);
            return;
        }
        System.out.println("""
                     
                Enter order, use latin letters in upper case, example: ABCDE
                """);
        String order = scanner.nextLine();
        if (validateOrder(order, expression)) {
            System.out.print("""
                                                
                    Invalid order
                    End input test
                                                
                    """);
            return;
        }

        BDD bdd = new BDD();

        bdd.create(reformatExpression(expression), order);

        String[] inputs = generateInputs(order.length());

        boolean error = false;
        System.out.printf("""
                         
                        Result for truth table test:
                                                
                        Number of different variables: %s
                        BDD before reduction: %s
                        BDD after reduction: %s
                        Average percent of reduction: %.2f%%
                                                
                        Expression: %s, Order: %s
                         
                        %s\tOutput\tAnticipated output
                                                
                        """,
                order.length(),
                bdd.getNumOfNodesBeforeReduce(),
                bdd.getNumOfNodesAfterReduce(),
                (bdd.getNumOfNodesBeforeReduce() - bdd.getNumOfNodesAfterReduce()) / bdd.getNumOfNodesBeforeReduce() * 100,
                expression,
                order,
                order);
        for (String input : inputs) {
            System.out.println(input + "\t" + bdd.use(input) + "\t" + manualCalculation(expression, order, input));
            if (!bdd.use(input).equals(manualCalculation(expression, order, input))) {
                error = true;
            }
        }
        System.out.print("\nEnd truth table test with ");
        if (error) {
            System.out.println("error");
        } else {
            System.out.println("no error");
        }
    }

    private static void reduceTest() throws IOException {
        System.out.print("""
                                        
                Start reduce test for BDD_create and BDD_create_with_beast_order
                with range of letters in b-function from 2 to 20
                and 100 attempts for each number of letters
                                        
                """);

        FileWriter testFile = null;
        try {
            testFile = new FileWriter("./src/main/resources/tests/reduce_test.md");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (testFile != null) testFile.write("""
                | Number of different letters | Average percent of reduction for BDD_create | Average percent of reduction for BDD_create_with_beast_order |
                |---------------------------|----------------------------------------|----------------------------------------------------------------|
                """);
        for (int numLetters = 2; numLetters <= 21; numLetters++) {
            BDD bddRegular = new BDD();
            BDD bddBestOrder = new BDD();
            double averagePercentForBddCreat = 0, averagePercentForBddCreateWithBestOrder = 0;
            for (int numOfAttempt = 0; numOfAttempt < 100; numOfAttempt++) {
                String bFunction = generateBFunction(numLetters, numLetters * 2);
                String order = generateOrder(bFunction);
                bddRegular.create(bFunction, order);
                bddBestOrder.createWithBeastOrder(bFunction);

                double numOfDeletedNodesForBddCreat = bddRegular.getNumOfNodesBeforeReduce() - bddRegular.getNumOfNodesAfterReduce();
                double percentForBddCreat = numOfDeletedNodesForBddCreat * 100 / bddRegular.getNumOfNodesBeforeReduce();
                averagePercentForBddCreat += percentForBddCreat;

                double numOfDeletedNodesForBddCreateWithBestOrder = bddBestOrder.getNumOfNodesBeforeReduce() - bddBestOrder.getNumOfNodesAfterReduce();
                double percentForBddCreateWithBestOrder = numOfDeletedNodesForBddCreateWithBestOrder * 100 / bddBestOrder.getNumOfNodesBeforeReduce();
                averagePercentForBddCreateWithBestOrder += percentForBddCreateWithBestOrder;
            }
            averagePercentForBddCreat /= 100;
            averagePercentForBddCreateWithBestOrder /= 100;
            System.out.printf("Number of different letters: %d\nAverage percent for BDD create:\t\t\t\t\t%.2f%%\nAverage percent for BDD create with best order:\t%.2f%%\n", numLetters, averagePercentForBddCreat, averagePercentForBddCreateWithBestOrder);
            if (testFile != null)
                testFile.write(String.format("| %d | %.2f | %.2f |\n", numLetters, averagePercentForBddCreat, averagePercentForBddCreateWithBestOrder));
        }
        if (testFile != null) testFile.close();
        System.out.println("End reduce test");
    }

    private static void timeTest() throws IOException {
        System.out.print("""
                                        
                Start time test for BDD_create and BDD_create_with_beast_order
                with range of letters in b-function from 2 to 20
                and 100 attempts for each number of letters
                                        
                """);

        FileWriter testFile = null;
        try {
            testFile = new FileWriter("./src/main/resources/tests/time_test.md");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (testFile != null) testFile.write("""
                | Number of different letters | Average time for BDD_create in ms | Average time for BDD_create_with_beast_order in ms |
                |---------------------------|----------------------------------------|-------------------------------------|
                """);

        for (int numLetters = 2; numLetters <= 21; numLetters++) {
            BDD bddRegular = new BDD();
            BDD bddBestOrder = new BDD();
            double averageTimeForBddCreat = 0, averageTimeForBddCreateWithBestOrder = 0;
            for (int numOfAttempt = 0; numOfAttempt < 100; numOfAttempt++) {
                String bFunction = generateBFunction(numLetters, numLetters * 2);
                String order = generateOrder(bFunction);
                long startTime = System.nanoTime();
                bddRegular.create(bFunction, order);
                long endTime = System.nanoTime();
                long timeForBddCreat = endTime - startTime;
                averageTimeForBddCreat += timeForBddCreat;

                startTime = System.nanoTime();
                bddBestOrder.createWithBeastOrder(bFunction);
                endTime = System.nanoTime();
                long timeForBddCreateWithBestOrder = endTime - startTime;
                averageTimeForBddCreateWithBestOrder += timeForBddCreateWithBestOrder;
            }
            averageTimeForBddCreat /= 100;
            averageTimeForBddCreateWithBestOrder /= 100;
            System.out.printf("Number of different letters: %d\nAverage time for BDD create:\t\t\t\t\t%.2fms\nAverage time for BDD create with best order:\t%.2fms\n", numLetters, averageTimeForBddCreat / 1000000, averageTimeForBddCreateWithBestOrder / 1000000);
            if (testFile != null)
                testFile.write(String.format("| %d | %.2f | %.2f |\n", numLetters, averageTimeForBddCreat / 1000000, averageTimeForBddCreateWithBestOrder / 1000000));
        }
        if (testFile != null) testFile.close();
        System.out.println("End time test");
    }

    private static boolean validateExpression(String expression) {
        if (expression == null) return true;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (!Character.isUpperCase(ch) && ch != '!' && ch != '+') {
                return true;
            }
            if (ch == '!') {
                if (i == expression.length() - 1 || !Character.isLetter(expression.charAt(i + 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean validateOrder(String order, String expression) {
        if (order == null) return true;

        Set<Character> distinctLetters = new HashSet<>();

        for (char c : expression.toCharArray())
            if (Character.isLetter(c))
                distinctLetters.add(c);
        int maxUniqueLetters = distinctLetters.size();

        Set<Character> uniqueChars = new HashSet<>();
        for (int i = 0; i < order.length(); i++) {
            char c = order.charAt(i);
            if (!Character.isUpperCase(c) || uniqueChars.contains(c)) {
                return true;
            }
            uniqueChars.add(c);
            if (uniqueChars.size() > maxUniqueLetters) {
                return true;
            }
            distinctLetters.remove(c);
        }
        return !distinctLetters.isEmpty();
    }

    private static boolean validateInput(String input, String order) {
        if (input == null || input.length() != order.length()) return true;
        for (char c : input.toCharArray()) {
            if (c != '0' && c != '1') {
                return true;
            }
        }
        return false;
    }

    static String manualCalculation(String expression, String order, String input) {
        if (validateExpression(expression) || validateOrder(order, expression) || validateInput(input, order))
            return null;
        String bfunction = reformatExpression(expression);
        String[] letters = order.split("");
        String[] numbers = input.split("");
        String[] pieces = bfunction.split("");

        for (int i = 0; i < letters.length; i++) {
            String lowercase = letters[i].toLowerCase();
            for (int j = 0; j < pieces.length; j++) {
                if (pieces[j].equals(letters[i])) {
                    pieces[j] = numbers[i];
                } else if (pieces[j].equals(lowercase)) {
                    pieces[j] = numbers[i].equals("1") ? "0" : "1";
                }
            }
        }

        for (String piece : pieces) {
            if (!piece.equals("0") && !piece.equals("1") && !piece.equals("+")) {
                return null;
            }
        }

        expression = String.join("", pieces);
        pieces = expression.contains("+") ? expression.split("\\+") : new String[]{expression};

        for (String piece : pieces) {
            if (!piece.contains("0")) {
                return "1";
            }
        }

        return "0";
    }

    private static String reformatExpression(String expression) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);
            if (currentChar == '!')
                capitalizeNext = true;
            else if (Character.isLetter(currentChar)) {
                result.append(capitalizeNext ? Character.toLowerCase(currentChar) : currentChar);
                capitalizeNext = false;
            } else
                result.append(currentChar);
        }
        return result.toString();
    }

    public static String generateBFunction(int numberOfDifferentLetters, int maxLength) {
        String allLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder(numberOfDifferentLetters);
        Random random = new Random();
        while (sb.length() < numberOfDifferentLetters) {
            int index = random.nextInt(allLetters.length());
            char c = allLetters.charAt(index);
            if (sb.indexOf(Character.toString(c)) == -1) {
                sb.append(c);
            }
        }

        StringBuilder result = new StringBuilder(maxLength);
        boolean flag = false;
        for (int i = 0; i < maxLength; i++) {
            if (i != 0 && i != maxLength - 1 && random.nextInt(6) == 1 && flag) {
                result.append("+");
                flag = false;
            } else {
                int index = random.nextInt(numberOfDifferentLetters);
                char c = sb.charAt(index);
                result.append(c);
                flag = true;
            }
        }
        return result.toString();
    }

    @SuppressWarnings("Duplicates") // because we need to generate order for bfunction there and in BDD class
    private static String generateOrder(String bfunction) {
        char[] chars = bfunction.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
            }
        }

        TreeSet<Character> set = new TreeSet<>();
        for (int i = 0; i < sb.length(); i++) {
            set.add(sb.charAt(i));
        }

        StringBuilder result = new StringBuilder();
        for (Character c : set) {
            result.append(c);
        }

        return result.toString();
    }

    public static String[] generateInputs(int length) {
        int numStrings = (int) Math.pow(2, length);
        String[] strings = new String[numStrings];

        for (int i = 0; i < numStrings; i++) {
            StringBuilder binary = new StringBuilder(Integer.toBinaryString(i));
            while (binary.length() < length) {
                binary.insert(0, "0");
            }
            strings[i] = binary.toString();
        }

        return strings;
    }
}
