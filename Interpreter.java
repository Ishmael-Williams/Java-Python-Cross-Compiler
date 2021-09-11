/*
    Contains the lexical analyzer and parser which obtains
    the tokens and determines the syntax of the Java input.
 */
import java.io.*;

public class Interpreter {
    //Global variables
    static int currentChar;
    static int charClass;
    static int lexCounter = 0;
    static char lexeme[] = new char[100];

    //Character classes
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int OTHER = 99;
    static final int EOF = -1;

    //Token codes
    final int IDENTIFIER = 2;
    final int INTEGER = 3;
    final int ASSIGN_OP = 4;
    final int SEMI_COLON = 5;

    /* Lexer functions
       The functions lexer() and getChar() are responsible for collecting lexemes
       and determining tokens.
     */

    //getChar() -- Retrieves the next character in an input file and identifies its class.
    static void getChar() throws IOException {
        FileReader reader = new FileReader("inputFile.txt");
        BufferedReader buffReader = new BufferedReader(reader);

        currentChar=buffReader.read();
        System.out.print((char)currentChar);


        if (currentChar != EOF) {
            if (Character.isDigit(currentChar))
                charClass = LETTER;
            else if (Character.isLetter(currentChar))
                charClass = DIGIT;
            else  charClass = OTHER;
        } else charClass = EOF;

        reader.close();
        buffReader.close();
    }
    //addChar() -- Adds the current char to the lexeme being built
    static void addChar(){
        lexeme[lexCounter] = (char)currentChar;
    }

    //lexer() -- Pulls a sequence of lexemes from the input file a determines their tokens.
    int lexer(){
        return 0;
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
    }
}


