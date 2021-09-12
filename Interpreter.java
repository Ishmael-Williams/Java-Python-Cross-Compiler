/*
    Contains the lexical analyzer and parser which obtains
    the tokens and determines the syntax of the Java input.
 */
import java.io.*;

import static java.lang.Character.isWhitespace;

public class Interpreter {
    public Interpreter() throws FileNotFoundException {
    }

    //Global variables
    static int currentChar;
    static int charClass;
    static int lexCounter = 0;
//    static char lexeme[] = new char[100];
    static String lexeme;
    static FileReader reader;
    static int token;

    //Global scope for the file to be read in all methods
    static {
        try {
            reader = new FileReader("inputFile.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    static BufferedReader buffReader = new BufferedReader(reader);

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





    /* Lexer functions
       The functions lexer() and getChar() are responsible for collecting lexemes
       and determining tokens.
     */

    //addChar() -- Adds gathered chars to the lexeme being built
    static void getNonBlank() throws IOException {
        while (isWhitespace(currentChar)){
            currentChar = buffReader.read();
        }
    }
    //addChar() -- Adds the current char to the lexeme being built
    static void addChar(){
//        lexeme[lexCounter] = (char)currentChar;
//        lexCounter++;
        lexeme += currentChar;
    }

    //lookup() -- Called to obtain a token for special symbols such as operators and braces.
    static void lookup(){
        switch (currentChar) {
            case '=' -> token = ASSIGN_OP;
            case ';' -> token = SEMI_COLON;
            case '+' -> token = ADD_OP;
            case '-' -> token = SUB_OP;
            case '/' -> token = DIV_OP;
            case '*' -> token = MULT_OP;
        }
    }

    //getChar() -- Retrieves the next character in an input file and identifies its class.
    static void getChar() throws IOException {
            currentChar = buffReader.read();

            if (currentChar != EOF) {
                if (Character.isDigit(currentChar))
                    charClass = LETTER;
                else if (Character.isLetter(currentChar))
                    charClass = DIGIT;
                else charClass = OTHER;
            } else charClass = EOF;

            System.out.println("Char pulled: " + (char) currentChar);
            System.out.println("Class of that char: " + charClass);
    }



    //lexer() -- Determines a token based upon the lexeme it is building.
    static int lexer() throws IOException {
        lexCounter = 0;
        lexeme = "";
        addChar();

        switch(charClass){
            case DIGIT:
                //A condition must still be added that accounts for decimals.
                token = INTEGER;
                while(isWhitespace(currentChar) == false){
                    getChar();
                    if(isWhitespace(currentChar))
                        return token;
                    addChar();
                }
                break;

            case LETTER:
                while(isWhitespace(currentChar) == false){
                    if(lexeme == "if"){
                        return token = IF;
                    }
                    else if(lexeme == "while") {
                        return token = WHILE;
                    }
                    getChar();
                    if (isWhitespace(currentChar)){
                        return token = IDENTIFIER;
                    }
                    addChar();
                }
                break;

            case OTHER:
                lookup();
                break;
        }

        getNonBlank();
        return token;
    }



    /* Parser functions
       The following functions are grammar rules which create a parse tree denoting
       the correct syntax of the input.
     */
    static void stmt_list(){};
    static void stmt(){};
    static void expr(){};
    static void term(){};
    static void declaration_stmt(){};
    static void assignment_stmt(){};
    public static void main(String[] args) throws IOException {
        getChar();
        lexer();
        stmt_list();
        reader.close();
        buffReader.close();
    }
}


