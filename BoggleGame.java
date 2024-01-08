package ass4;

import java.util.*;
import java.io.*;

public class Boggle {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final double[] FREQUENCIES = {
            0.08167, 0.01492, 0.02782, 0.04253, 0.12703, 0.02228,
            0.02015, 0.06094, 0.06966, 0.00153, 0.00772, 0.04025,
            0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
            0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
            0.01974, 0.00074
    };
    public String[][] board;
    private String dictionary;
    private int[][] marked;

    public Boggle(int N) {
        this.board = new String[N][N];
        this.marked = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.board[i][j] = randomLetter().toLowerCase();
                this.marked[i][j] = 0;
            }
        }
    }

    public Boggle(String[][] board) {
        this.board = new String[board.length][board.length];
        this.marked = new int[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                this.board[i][j] = board[i][j].toLowerCase();
                this.marked[i][j] = 0;
            }
        }
    }

    public Boggle(String[] dice) {
        int counter = 0;
        int length = (int) Math.sqrt(dice.length);
        this.board = new String[length][length];
        this.marked = new int[length][length];
        for (int i = 0; i < dice.length; i++) {
            int randIndex = (int) (Math.random() * dice.length);
            String temp = dice[i];
            dice[i] = dice[randIndex];
            dice[randIndex] = temp;
        }
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                this.board[i][j] = randomDice(dice[counter]).toLowerCase();
                this.marked[i][j] = 0;
                counter++;
            }
        }
    }

    public Boggle(String[] dice, long seed) {
        double d = Math.sqrt(dice.length);
        int N = (int) d;
        this.board = new String[N][N];
        this.marked = new int[N][N];
        Random rand = new Random(seed);
        // shake the dice
        for (int k = 0; k < dice.length - 1; k++) {
            int r = rand.nextInt(k, dice.length);
            // swap dice[k] and dice [r]
            String temp = dice[k];
            dice[k] = dice[r];
            dice[r] = temp;
        }
        int k = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                String die = dice[k++];
                int r = rand.nextInt(die.length());
                this.board[i][j] = String.valueOf(die.charAt(r)).toLowerCase();
                this.marked[i][j] = 0;
            }
        }
    }

    public Boggle(String fileName) {
        int indexOne = 0;
        int indexTwo;
        Scanner scan = null;
        String line;
        int length = 0;
        try {
            scan = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("\n" + "File not found");
            return;
        }
        if (scan.hasNextLine()) {
            line = scan.nextLine();
            length = Integer.parseInt(line.substring(0, 1));
            this.board = new String[length][length];
            this.marked = new int[length][length];
        }
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            Scanner lineScanner = new Scanner(line).useDelimiter(" ");
            indexTwo = 0;
            for (int i = 0; i < length; i++) {
                String debugVar = lineScanner.next().trim().toLowerCase(); // debugging delete later
                this.board[indexOne][indexTwo] = debugVar; // debug
                this.marked[indexOne][indexTwo] = 0;
                indexTwo++;
            }
            indexOne++;
        }
    }

    public String randomLetter() {
        double rand = Math.random();
        double cumSum = FREQUENCIES[0];
        if (rand > 0 && rand < FREQUENCIES[0])
            return "A";
        else {
            for (int i = 0; i < FREQUENCIES.length - 2; i++) {
                cumSum += FREQUENCIES[i + 1];
                if (rand > FREQUENCIES[i] && rand < cumSum)
                    return ALPHABET.substring(i + 1, i + 2);
            }
        }
        return "Z";
    }

    public String randomDice(String singleDice) {
        int rand = (int) (Math.random() * 6);
        return singleDice.substring(rand, rand + 1);
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board.length; j++) {
                output.append(this.board[i][j]);
                output.append(" ");
            }
            output.append("\n");
        }
        return output.toString();
    }

    public boolean matchWord(String word) {
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board.length; j++) {
                this.board[i][j] = this.board[i][j].toLowerCase();
            }
        }
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board.length; j++) {
                if (matchWordOneSpot(word, this.board[i][j], i, j, 1)) {
                    this.marked[i][j] = 0;
                    this.board[i][j] = this.board[i][j].toUpperCase();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean matchWordOneSpot(String word, String currLetter, int row, int col, int letterCounter) {
        if (currLetter.toLowerCase().equals("qu"))
            currLetter = "q";
        if (this.marked[row][col] == 1 || letterCounter > word.length()
                || !currLetter.substring(0, 1).toLowerCase()
                        .equals(word.substring(letterCounter - 1, letterCounter).toLowerCase())
                || word.length() < 3)
            return false;
        if (letterCounter == word.length()
                && currLetter.toLowerCase().equals(word.substring(word.length() - 1, word.length()).toLowerCase())) {
            this.board[row][col] = this.board[row][col].toUpperCase();
            return true;
        }
        this.marked[row][col] = 1;
        boolean success = false;

        // checking up
        if (row - 1 >= 0) {
            success = matchWordOneSpot(word, this.board[row - 1][col], row - 1, col, letterCounter + 1);
            if (success) {
                this.marked[row - 1][col] = 0;
                this.board[row - 1][col] = this.board[row - 1][col].toUpperCase();
                return true;
            }
        }

        // checking up and to the right
        if (row - 1 >= 0 && col + 1 <= this.board.length - 1) {
            success = matchWordOneSpot(word, this.board[row - 1][col + 1], row - 1, col + 1,
                    letterCounter + 1);
            if (success) {
                this.marked[row - 1][col + 1] = 0;
                this.board[row - 1][col + 1] = this.board[row - 1][col + 1].toUpperCase();
                return true;
            }
        }

        // checking right
        if (col + 1 <= this.board.length - 1) {
            success = matchWordOneSpot(word, this.board[row][col + 1], row, col + 1, letterCounter + 1);
            if (success) {
                this.marked[row][col + 1] = 0;
                this.board[row][col + 1] = this.board[row][col + 1].toUpperCase();
                return true;
            }
        }

        // checking down and to the right
        if (row + 1 <= this.board.length - 1 && col + 1 <= this.board.length - 1) {
            success = matchWordOneSpot(word, this.board[row + 1][col + 1], row + 1, col + 1,
                    letterCounter + 1);
            if (success) {
                this.marked[row + 1][col + 1] = 0;
                this.board[row + 1][col + 1] = this.board[row + 1][col + 1].toUpperCase();
                return true;
            }
        }

        // checking down
        if (row + 1 <= this.board.length - 1) {
            success = matchWordOneSpot(word, this.board[row + 1][col], row + 1, col, letterCounter + 1);
            if (success) {
                this.marked[row + 1][col] = 0;
                this.board[row + 1][col] = this.board[row + 1][col].toUpperCase();
                return true;
            }
        }

        // checking down and to the left
        if (row + 1 <= this.board.length - 1 && col - 1 >= 0) {
            success = matchWordOneSpot(word, this.board[row + 1][col - 1], row + 1, col - 1,
                    letterCounter + 1);
            if (success) {
                this.marked[row + 1][col - 1] = 0;
                this.board[row + 1][col - 1] = this.board[row + 1][col - 1].toUpperCase();
                return true;
            }
        }

        // checking left
        if (col - 1 >= 0) {
            success = matchWordOneSpot(word, this.board[row][col - 1], row, col - 1, letterCounter + 1);
            if (success) {
                this.marked[row][col - 1] = 0;
                this.board[row][col - 1] = this.board[row][col - 1].toUpperCase();
                return true;
            }
        }

        // checking up and to the left
        if (row - 1 >= 0 && col - 1 >= 0) {
            success = matchWordOneSpot(word, this.board[row - 1][col - 1], row - 1, col - 1,
                    letterCounter + 1);
            if (success) {
                this.marked[row - 1][col - 1] = 0;
                this.board[row - 1][col - 1] = this.board[row - 1][col - 1].toUpperCase();
                return true;
            }
        }

        this.marked[row][col] = 0;
        return false;
    }

    public static List<String> getAllValidWords(String dictionaryName, String boardName) {
        List<String> list = new LinkedList<String>();
        Boggle game = new Boggle(boardName);
        Scanner scan = null;
        try {
            scan = new Scanner(new File(dictionaryName));
        } catch (FileNotFoundException e) {
            System.err.println("\n" + "File not found");
            return null;
        }
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            StringBuilder sb = new StringBuilder();
            for (int m = 0; m < line.length(); m++) {
                if (line.charAt(m) != 'Q') {
                    sb.append(line.charAt(m));
                } else {
                    sb.append('Q');
                    m++; // skip 'u'
                }
            }
            line = sb.toString();
            if (game.matchWord(line))
                list.add(line.toLowerCase());
        }
        return list;
    }

    public List<String> getAllValidWords() {
        List<String> list = new LinkedList<String>();
        if (this.dictionary == null || this.board == null)
            return null;
        Scanner scan = null;
        try {
            scan = new Scanner(new File(this.dictionary));
        } catch (FileNotFoundException e) {
            System.err.println("\n" + "File not found");
            return null;
        }
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            StringBuilder sb = new StringBuilder();
            for (int m = 0; m < line.length(); m++) {
                if (line.charAt(m) != 'Q') {
                    sb.append(line.charAt(m));
                } else {
                    sb.append('Q');
                    m++; // skip 'u'
                }
            }
            line = sb.toString();
            if (this.matchWord(line))
                list.add(line.toLowerCase());
        }
        return list;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public static void main(String[] args) {
        Boggle game = null;
        boolean gameNotOver = true;
        int score = 0;
        Scanner input = new Scanner(System.in);
        System.out.println("\n" + "Welcome to Boggle!");
        System.out.println("Please type in the name of the dictionary file you would like to use.");
        String userInput = input.next();
        Scanner scan = null;
        HashSet<String> set = new HashSet<String>();
        try {
            scan = new Scanner(new File(userInput));
        } catch (FileNotFoundException e) {
            System.err.println("\n" + "File not found");
            return;
        }
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            set.add(line);
        }
        Scanner inputTwo = new Scanner(System.in);
        System.out.println("\n" + "Please type in the name of the board file you would like to use.");
        String userInputTwo = inputTwo.next();
        System.out.println(
                "\n" + "Enter a word that you think is in the boggle board. If you are correct, you will be rewarded with a point for each letter in the word.");
        game = new Boggle(userInputTwo);
        game.setDictionary(userInput);
        while (gameNotOver) {
            Scanner wordInput = new Scanner(System.in);
            System.out.println("Type '!' if you wish to terminate the game.");
            System.out.print("Word: ");
            String word = wordInput.next();
            int length = word.length();
            if (word.equals("!"))
                gameNotOver = false;
            else {
                if (!set.contains(word.toUpperCase())) {
                    System.out.println("\n" + "illegal word" + "\n");
                    continue;
                }
                // replace the qu
                StringBuilder sb = new StringBuilder();
                for (int m = 0; m < word.length(); m++) {
                    if (word.charAt(m) != 'Q') {
                        sb.append(word.charAt(m));
                    } else {
                        sb.append('Q');
                        m++; // skip 'u'
                    }
                }
                word = sb.toString();
                if (game.matchWord(word)) {
                    score += length;
                    System.out.println(
                            "\n" + "Correct! " + length + " points added. Total score is " + score + "." + "\n");
                    System.out.println(game.toString());
                } else {
                    System.out.println("\n" + "illegal word" + "\n");
                }
            }
        }
        Scanner in = new Scanner(System.in);
        System.out.println("\n"
                + "Would you like to know the maximum number of words that could be generated with this board and dictionary?");
        System.out.println("Type 'y' if you would. Otherwise, type 'n'.");
        String theUserInput = in.next();
        if (theUserInput.equals("y"))
            System.out.println(game.getAllValidWords().size());
    }
}