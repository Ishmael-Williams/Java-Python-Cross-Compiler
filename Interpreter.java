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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import static java.lang.Character.isWhitespace;

public class Interpreter {
    private static GUI gui;

    public Interpreter(GUI gui) {
        this.gui = gui;
    }

    static class tokenInfo {
        tokens token;
        String lexeme;
    }

    //Character classes
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int SPACE = 98;
    static final int OTHER = 99;
    static final int EOF = -1;

    //Token codes

    /*Special note on the "CALL" token:
     * - It is defined for any lexeme that is followed by a period or parentheses.
     * - For now, we ignore the complexity of a call, for example "System.out.print" is defined as a token for a single
     *   call, and the meaning and relationship of "system", "out", and "print" are ignored.
     *
     * - Later we will capture the meaning of a CALL by investigating only the last term, "print" in the example.
     */
    public enum tokens {
        ADD_OP, ASSIGN_OP,
        CALL, CLASS,
        DATA_TYPE, DECLARATION, DIV_OP, DRIVER, //"DRIVER" refers to main()
        FUNCTION_DECLARATION,
        IDENTIFIER, IF, INTEGER,
        L_BRCE, L_PAREN,
        MULT_OP,
        PARAMETER, PRIMITIVE_DATA_TYPE, PUBLIC, //"PARAMETER" is conditionally derived and must appear in function declaration
        R_BRCE, R_PAREN,
        SEMI_COLON, STATIC, STRING, SUB_OP,
        VARIABLE, VOID,
        WHILE
    }


    //Global variables
    static int currentChar;
    static int charClass;
    static String lexeme;
    static FileReader reader;
    static BufferedReader buffReader;
    static tokens token;
    static String tokenText;
    static ArrayList<tokenInfo> tokenList = new ArrayList<tokenInfo>();

    /*  lexer functions for assigning classes and tokens   */
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
        while (isWhitespace(currentChar)) {
            currentChar = buffReader.read();
        }

        if (currentChar != EOF) {
            if (Character.isLetter(currentChar))
                charClass = LETTER;
            else if (Character.isDigit(currentChar))
                charClass = DIGIT;
            else if (isWhitespace(currentChar))
                charClass = SPACE;
            else charClass = OTHER;
        } else charClass = EOF;

        if (charClass == SPACE)
            return;
        System.out.println("Char pulled: " + (char) currentChar);
        System.out.println("Class of that char: " + charClass + "\n");

//        gui.EP.appendText("Char pulled: " + (char) currentChar + "\n");
//        gui.EP.appendText("Class of that char: " + charClass + "\n");


    }

    static void lookup() {
        switch (currentChar) {
            case '=' -> token = tokens.ASSIGN_OP;
            case ';' -> token = tokens.SEMI_COLON;
            case '+' -> token = tokens.ADD_OP;
            case '-' -> token = tokens.SUB_OP;
            case '/' -> token = tokens.DIV_OP;
            case '*' -> token = tokens.MULT_OP;
        }
    }

    static void lexer() throws IOException {
        while (currentChar != -1){
            lexeme = "";
            addChar();

            switch (charClass) {
                case DIGIT:
                    //A condition must still be added that accounts for decimals.
                    token = tokens.INTEGER;
                    while (isWhitespace(currentChar) == false) {
                        getChar();
                        if (isWhitespace(currentChar)) {
                            addTokenObject(token, lexeme);
                            break;
                        }
                        addChar();
                    }
                    break;
                case LETTER:
                    while (isWhitespace(currentChar) == false) {
                        if (Objects.equals(lexeme, "if")) {
                            token = tokens.IF;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "while")) {
                            token = tokens.WHILE;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "int")) {
                            token = tokens.DECLARATION;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "main(")) {
                            token = tokens.DRIVER;
                            lexeme = "main()";
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "String")) {
                            token = tokens.DATA_TYPE;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "args")) {
                            token = tokens.PARAMETER;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "print")) {
                            token = tokens.CALL;
                            addTokenObject(token, lexeme);
                            break;
                        }
                        getChar();
                        if (isWhitespace(currentChar)) {
                            token = tokens.IDENTIFIER;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (currentChar == '.') {
                            lexeme = "";
                            getChar();
                        }
                        addChar();
                        System.out.println("Current lexeme: " + lexeme);
//                        gui.EP.appendText("Current lexeme: " + lexeme + "\n");
                    }
                    break;

                case OTHER:
                    if (currentChar == '\"') {
                        token = tokens.STRING;
                        getChar();
                        addChar();
                        while (currentChar != '\"') {
                            getChar();
                            addChar();
                        }
                        addTokenObject(token, lexeme);
                    } else {
                        lookup();
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + charClass);
            }
            getChar();
        }
    }




    static void cleanFile() throws IOException {
        Path filePath = Path.of("Test 1.txt");
        Path cleanFile = Path.of("cleanFile.txt");

        String fileContents = Files.readString(filePath, StandardCharsets.US_ASCII);
        fileContents = fileContents.replace("public", "");
        fileContents = fileContents.replace("static", "");
        fileContents = fileContents.replace("void", "");
        Files.writeString(cleanFile, fileContents);
        reader = new FileReader("cleanFile.txt");
        buffReader = new BufferedReader(reader);

    }
    static void addTokenObject(tokens token, String lexeme){
        tokenInfo tokenInfoObject = new tokenInfo();
        tokenInfoObject.token = token;
        tokenInfoObject.lexeme = lexeme;
        tokenList.add(tokenInfoObject);

    }
    static void stmt_list() {
//        while (token != EOF) {
//            stmt();
//        }
    }

    /*  Parser functions for determining syntax through parse trees */
    static void stmt() {
    }

    static void expr() {
    }

    static void term() {
    }

    static void factor() {

    }


    public static void runInterpreter() throws IOException{
        cleanFile();
        getChar();
        lexer();
        System.out.println("The current token is: " + token + "\n");
//        gui.EP.appendText("The current token is: " + token + "\n\n");
        reader.close();
        buffReader.close();

        for (int i = 0; i < tokenList.size(); i++){
            System.out.println("Token " + (i+1) + ": \n     token - " + tokenList.get(i).token + "\n     lexeme - " + tokenList.get(i).lexeme);
        }
    }
    public static void main(String[] args) throws IOException {
        runInterpreter();
    }
}



