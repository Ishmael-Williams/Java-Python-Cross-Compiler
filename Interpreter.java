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
    //Character classes
    static final int LETTER = 0;
    static final int DIGIT = 1;
    static final int SPACE = 99;
    static final int OTHER = 99;
    static final int EOF = -1;
    static File file = new File("");
    //Global variables
    static boolean usesSpace = false;
    static int currentChar;
    static int charClass;
    //Token codes
    static String lexeme;
    static tokens token;
    static FileReader reader;
    static BufferedReader buffReader;
    static String tokenText;
    static ArrayList<tokenInfo> tokenList = new ArrayList<tokenInfo>();
    private static GUI gui;
    public Interpreter(GUI gui) {
        Interpreter.gui = gui;
    }

    /*Special note on the "CALL" token:
     * - It is defined for any lexeme that is followed by a period or parentheses.
     * - For now, we ignore the complexity of a call, for example "System.out.print" is defined as a token for a single
     *   call, and the meaning and relationship of "system", "out", and "print" are ignored.
     *
     * - Later we will capture the meaning of a CALL by investigating only the last term, "print" in the example.
     */
    public enum tokens {
        ADD_OP, ARRAY_DECLARATION, ARRAY_GENERIC, ARRAY_REFERENCE, ASSIGN_OP,
        PRINT, CLASS, CR, COMMA,
        DATA_TYPE, DECLARATION, DIV_OP, DRIVER, //"DRIVER" refers to main()
        FOR, FUNCTION_DECLARATION,
        GREATER_THAN, GREATER_THAN_OR_EQUAL,
        HASH,
        IDENTIFIER, IF, IMPORT, INTEGER,
        L_BRCE, L_BRCT, LENGTH_FUNC, L_PAREN, LESS_THAN, lESS_THAN_OR_EQUAL,
        MULT_OP,
        NEW, NEW_LINE, NEW_TAB, NEXT_INT,
        PARAMETER, PRIMITIVE_DATA_TYPE, PUBLIC, //"PARAMETER" is conditionally derived and must appear in function declaration
        R_BRCE, R_BRCT, R_PAREN,
        SCANNER, SEMI_COLON, SPACE, STATIC, STRING, SUB_OP,
        UNKNOWN,
        VARIABLE, VOID,
        WHILE
    }

    static class tokenInfo {
        tokens token;
        String lexeme;
        boolean special = false;
        boolean ignore = false;
        int size = 0;
    }

    static void lexer() throws IOException {
        while (currentChar != -1) {
            lexeme = "";
            addChar();

            switch (charClass) {
                case DIGIT:
                    //A condition must still be added that accounts for decimals.
                    token = tokens.INTEGER;
                    while (isWhitespace(currentChar) == false) {
                        getChar();
                        if (isWhitespace(currentChar) || currentChar == ';') {
                            addTokenObject(token, lexeme);
                            break;
                        }
                        addChar();
                    }
                    break;
                case LETTER:
                    while (isWhitespace(currentChar) == false) {
                        getChar();
                        //Compare the current lexeme against the set of supported lexemes
                        if (Objects.equals(lexeme, "if")) {
                            token = tokens.IF;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "while")) {
                            token = tokens.WHILE;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "int") && currentChar == ' ') {
                            token = tokens.DECLARATION;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "main")) {
                            token = tokens.DRIVER;
                            addChar();
                            while(currentChar != ')'){
                                getChar();
                                addChar();
                            }
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size() - 1).special = true;
                            break;
                        } else if (Objects.equals(lexeme, "String") && currentChar == ' ') {
                            token = tokens.DATA_TYPE;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "args")) {
                            token = tokens.PARAMETER;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "print")) {
                            token = tokens.PRINT;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (Objects.equals(lexeme, "import")) {
                            token = tokens.IMPORT;
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size() - 1).special = true;
                            break;
                        } else if (Objects.equals(lexeme, "Scanner")) {
                            token = tokens.SCANNER;
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size() - 1).special = true;
                            break;
                        } else if (Objects.equals(lexeme, "length")){
                            token = tokens.LENGTH_FUNC;
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size() -1).special = true;
                            break;
                        } else if (Objects.equals(lexeme, "new")) {
                            token = tokens.NEW;
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size() - 1).ignore = true;
                            break;
                        }
                        /* Check the char following the current lexeme for special cases:
                         * - Flow control statements
                         * - functions
                         * - arrays
                         * */
                        if (currentChar == ' ' || currentChar == ';') {
                            token = tokens.IDENTIFIER;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (currentChar == ',') {
                            token = tokens.IDENTIFIER;
                            addTokenObject(token, lexeme);
                            break;
                        } else if (currentChar == ')') {
                            token = tokens.IDENTIFIER;
                            addTokenObject(token, lexeme);
                            lookup();
                            lexeme = Character.toString(currentChar);
                            addTokenObject(token, lexeme);
                            break;
                        } else if (currentChar == '(') {
                            if (Objects.equals(lexeme, "for")) {
                                token = tokens.FOR;
                                addTokenObject(token, lexeme);
                                tokenList.get(tokenList.size() - 1).special = true;
                            } else if (Objects.equals(lexeme, "nextInt")) {
                                token = tokens.NEXT_INT;
                                addTokenObject(token, lexeme);
                                tokenList.get(tokenList.size() - 1).special = true;
                            } else {
                                token = tokens.UNKNOWN;
                                addTokenObject(token, lexeme);
                            }

                            //After processing the name and token for the function call up to this point, process the "(" the system is in possession of.
                            lookup();
                            lexeme = Character.toString(currentChar);
                            addTokenObject(token, lexeme);
                            break;
                        } else if (currentChar == '[' && tokenList.get(tokenList.size() - 1).token == tokens.NEW) {
                            /*3 possible cases for encountering a "[" character:
                             * 1 - ARRAY_GENERIC = "int[]"
                             *          ignore this token
                             * 2 - ARRAY_REFERENCE = "varName[any_integer]"
                             *          write this token as is
                             * 3 - ARRAY_DECLARATION = "varName = new int[any_integer]"
                             *          In the case of "new" followed by anyDatatype[], capture its size for varName that precedes it
                             * */

                            addChar();
                            //Current case = 3; occurrence of "[" was preceded by "new"
                            //Find the entire integer that will determine the size of the array being declared
                            //Continue to get and add chars until the lexeme looks like "data_type[any_integer]"
                            String arraySize = "";
                            getChar();
                            while(charClass == DIGIT){
                                arraySize+=Character.getNumericValue(currentChar);
                                addChar();
                                getChar();
                                addChar();
                            }

                            //Trace back through the tokens to find the identifier token corresponding
                            //to the new array being declared, assign it the integer size that was found
                            int i = tokenList.size()-1;
                            while (tokenList.get(i).token != tokens.IDENTIFIER) {
                                i--;
                            }
                            tokenList.get(i).size = Integer.parseInt(arraySize);
                            token = tokens.ARRAY_DECLARATION;
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size()-1).special = true;
                            break;
                        }  else if(Objects.equals(lexeme, "int") || Objects.equals(lexeme, "boolean") ||
                                Objects.equals(lexeme, "String") || Objects.equals(lexeme, "double")){
                            //Current case = 3; ARRAY_GENERIC
                            //continue to get and add chars to the lexeme until it looks like "data_type[]"
                            addChar();
                            getChar();
                            addChar();
                            token = tokens.ARRAY_GENERIC;
                            addTokenObject(token, lexeme);
                            tokenList.get(tokenList.size()-1).ignore = true;
                            break;
                        }  else if(currentChar == '['){
                            //Current case = 2; ARRAY_REFERENCE
                            //Continue to get and add chars until the lexeme looks like "varName[any_integer]"
                            addChar();
                            while(currentChar != ']'){
                                getChar();
                                addChar();
                            }
                            token = tokens.ARRAY_REFERENCE;
                            addTokenObject(token, lexeme);
                            break;
                        }

                        //Check for classes and objects between periods, such as
                        //"array.length" where we want to collect and tag "array"
                        if (currentChar == '.'){
                          token = tokens.IDENTIFIER;
                          addTokenObject(token, lexeme);

                          /* Tokens preceding a period are ignored for now but later can
                           * be retrieved for special uses, such as when a lexeme's name "arrayName" precedes
                           * ".length" and must be collected to write the natural python conversion "len(arrayName)"
                           */
                            tokenList.get(tokenList.size()-1).ignore = true;
                            break;
                        }

                        addChar();
                        System.out.println("Current lexeme: " + lexeme);
                    }
                    break;

                case OTHER:
                    if (currentChar == '\"') {
                        token = tokens.STRING;
                        usesSpace = true;
                        getChar();
                        addChar();
                        while (currentChar != '\"') {
                            getChar();
                            addChar();
                        }
                        usesSpace = false;
                        addTokenObject(token, lexeme);
                    } else {
                        lookup();
                        addTokenObject(token, lexeme);
                        if (token == tokens.SEMI_COLON) {
                            tokenList.get(tokenList.size() - 1).special = true;
                        } else if (token == tokens.NEW_LINE) {
                            tokenList.get(tokenList.size() - 1).special = true;
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + charClass);
            }
            getChar();
        }
    }

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
//        while (isWhitespace(currentChar) && usesSpace == false) {
//            currentChar = buffReader.read();
//        }

        if (currentChar != EOF) {
            if (Character.isLetter(currentChar))
                charClass = LETTER;
            else if (Character.isDigit(currentChar))
                charClass = DIGIT;
            else if (isWhitespace(currentChar))
                charClass = SPACE;
            else charClass = OTHER;
        } else charClass = EOF;

        System.out.println("Char pulled: " + (char) currentChar);
        System.out.println("Class of that char: " + charClass + "\n");
    }

    static void lookup() {
        switch (currentChar) {
            case '=' -> token = tokens.ASSIGN_OP;
            case ';' -> token = tokens.SEMI_COLON;
            case ',' -> token = tokens.COMMA;
            case '+' -> token = tokens.ADD_OP;
            case '-' -> token = tokens.SUB_OP;
            case '/' -> token = tokens.DIV_OP;
            case '*' -> token = tokens.MULT_OP;
            case '(' -> token = tokens.L_PAREN;
            case ')' -> token = tokens.R_PAREN;
            case '{' -> token = tokens.L_BRCE;
            case '}' -> token = tokens.R_BRCE;
            case '[' -> token = tokens.L_BRCT;
            case ']' -> token = tokens.R_BRCT;
            case ' ' -> token = tokens.SPACE;
            case '#' -> token = tokens.HASH;
            case '\r' -> token = tokens.CR;
            case '\n' -> token = tokens.NEW_LINE;
            case '\t' -> token = tokens.NEW_TAB;
        }

    }

    static void cleanInput() throws IOException {
        String contents = "";
        if (file.toString() == "") {
            contents = gui.EJ.getText();
        } else {
            Path filePath = Path.of(file.getPath());
            contents = Files.readString(filePath, StandardCharsets.US_ASCII);
        }
        contents = contents.replace("public ", "");
        contents = contents.replace("static ", "");
        contents = contents.replace("void ", "");
        contents = contents.replace("{", "");
        contents = contents.replace("}", "");
//        contents = contents.replace(";", "");

        Path cleanFile = Path.of("Test Programs/cleanFile.txt");
        Files.writeString(cleanFile, contents);
        reader = new FileReader("Test Programs/cleanFile.txt");

        buffReader = new BufferedReader(reader);
    }

    static void addTokenObject(tokens token, String lexeme) {
        tokenInfo tokenInfoObject = new tokenInfo();
        tokenInfoObject.token = token;
        tokenInfoObject.lexeme = lexeme;
        tokenList.add(tokenInfoObject);

    }

    public static void runInterpreter() throws IOException {
        tokenList.clear();
        cleanInput();
        getChar();
        lexer();
        System.out.println("The current token is: " + token + "\n");
        reader.close();
        buffReader.close();

        for (int i = 0; i < tokenList.size(); i++) {
            System.out.println("Token " + (i + 1) + ": \n     token - " + tokenList.get(i).token + "\n     lexeme - " + tokenList.get(i).lexeme);
            //gui.EP.appendText("Token " + (i+1) + ": \n     token - " + tokenList.get(i).token + "\n     lexeme - " + tokenList.get(i).lexeme + "\n\n");
        }
        Converter.runConverter(tokenList);
    }

    public static void main(String[] args) throws IOException {
        runInterpreter();
    }
}