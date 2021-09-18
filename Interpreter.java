/*
    Contains the lexical analyzer and parser which obtains
    the tokens and determines the syntax of the Java input.
    The grammar rules are examples and will be changed to the
    rules that Java behaves by.

    functions:
        lexer and tokenizer functions
            getNonBlank() -- Pulls chars from the input file until a non-whitespace is encountered.
            addChar()     -- Adds the current char to the lexeme being built.
            lookup()      -- Obtains the token for special symbols such as operators and braces.
            getChar()     -- Retrieves the next character in an input file and identifies its class.
            lexer()       -- Builds a lexeme and determines its token.

        parser functions (grammar rules)
            stmt_list()   -- <stmt>{stmt}
            stmt()        -- <assignment> | <flow_ctrl_stmt> | <declaration>
            expr()        -- <term>{(+ | -) <term>}
            term()        -- <factor>{(* | /) <factor>}
 */

import java.io.*;
import java.util.Objects;

import static java.lang.Character.isWhitespace;

public class Interpreter {
    public Interpreter() throws FileNotFoundException {
    }

    //Character classes
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int OTHER = 99;
    static final int EOF = -1;
    //Token codes
    static final int IDENTIFIER = 2;
    static final int INTEGER = 3;
    static final int ASSIGN_OP = 4;
    static final int SEMI_COLON = 5;
    static final int IF = 6;
    static final int WHILE = 7;
    static final int ADD_OP = 8;
    static final int SUB_OP = 9;
    static final int DIV_OP = 10;
    static final int MULT_OP = 11;
    static final int DECLARATION = 12;
    //Global variables
    static int currentChar;
    static int charClass;
    static String lexeme;
    static FileReader reader;
    static int token;

    //Global scope for the file used in this class
    static {
        try {
            reader = new FileReader("Test 1.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static BufferedReader buffReader = new BufferedReader(reader);



    /*  lexer and tokenizer functions   */
    static void getNonBlank() throws IOException {
        while (isWhitespace(currentChar)) {
            currentChar = buffReader.read();
        }
    }

    static void addChar() {
        lexeme += (char) currentChar;
    }

    static void getChar() throws IOException {
        currentChar = buffReader.read();

        if (currentChar != EOF) {
            if (Character.isLetter(currentChar))
                charClass = LETTER;
            else if (Character.isDigit(currentChar))
                charClass = DIGIT;
            else charClass = OTHER;
        } else charClass = EOF;


        System.out.println("Char pulled: " + (char) currentChar);
        System.out.println("Class of that char: " + charClass);
    }

    static void lookup() {
        switch (currentChar) {
            case '=' -> token = ASSIGN_OP;
            case ';' -> token = SEMI_COLON;
            case '+' -> token = ADD_OP;
            case '-' -> token = SUB_OP;
            case '/' -> token = DIV_OP;
            case '*' -> token = MULT_OP;
        }
    }

    static int lexer() throws IOException {
        lexeme = "";
        addChar();

        switch (charClass) {
            case DIGIT:
                //A condition must still be added that accounts for decimals.
                token = INTEGER;
                while (isWhitespace(currentChar) == false) {
                    getChar();
                    if (isWhitespace(currentChar))
                        return token;
                    addChar();
                }
                break;

            case LETTER:
                while (isWhitespace(currentChar) == false) {
                    if (Objects.equals(lexeme, "if")) {
                        return token = IF;
                    } else if (Objects.equals(lexeme, "while")) {
                        return token = WHILE;
                    } else if (Objects.equals(lexeme, "int")) {
                        return token = DECLARATION;
                    }
                    getChar();
                    if (isWhitespace(currentChar)) {
                        return token = IDENTIFIER;
                    }
                    addChar();
                    System.out.println("Current lexeme: " + lexeme);
                }
                break;

            case OTHER:
                lookup();
                break;
        }

        getNonBlank();
        return token;
    }


    static void stmt_list() {
//        while (token != EOF) {
//            stmt();
//        }
    }

    /*  Parser functions  */
    static void stmt() {
    }

    static void expr() {
    }

    static void term() {
    }

    static void factor() {

    }


    public static void main(String[] args) throws IOException {

        while (currentChar != -1) {
            getChar();
            lexer();
            stmt_list();
            System.out.println("The current token is: " + token + "\n");

        }
        reader.close();
        buffReader.close();
    }
}


