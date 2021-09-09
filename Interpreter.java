package sample;

/*
    Contains the lexical analyzer and parser which obtains
    the tokens and determines the syntax of the Java input.
 */


public class Interpreter {
    //Character classes
    final int LETTER = 0;
    final int DIGIT = 1;
    final int OTHER = 99;
    final int EOF = -1;

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
    void getChar(){};

    //lexer() -- Pulls a sequence of lexemes from the input file a determines their tokens.
    int lexer(){
        return 0;
    };



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
}


