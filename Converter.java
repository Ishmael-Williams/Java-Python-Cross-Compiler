import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class Converter {
    static String pythonText = "";
    static ArrayList<Interpreter.tokenInfo> tokenList;
    private static GUI gui;

    public Converter(GUI gui) {
        Converter.gui = gui;
    }

    static public void runConverter(ArrayList<Interpreter.tokenInfo> oldTokenList) throws IOException {
        tokenList = oldTokenList;
        pythonText = "";
        //for each token in tokenList, determine the Python equivalent
        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i).ignore == true)
                continue;

            if (tokenList.get(i).special) {
                switch (tokenList.get(i).token) {
                    case ARRAY_DECLARATION:
                        pythonText += "[]";
                        break;
                    case ASSIGN_OP:
                        //To check for cases where the assignment operator is followed by
                        //an array initialization, which uses [] in Python instead of the {} in Java
                        i++;
                        while (tokenList.get(i).token == Interpreter.tokens.SPACE) {
                            i++;
                        }
                        if (tokenList.get(i).token == Interpreter.tokens.L_BRCE) {
                            pythonText += " = [";
                            i++; //skip the "{"
                            while (tokenList.get(i).token != Interpreter.tokens.R_BRCE) {
                                pythonText += tokenList.get(i).lexeme;
                                i++;
                            }
                            pythonText += " ]";
                            break;
                        } else {
                            pythonText += " = ";
                            break;
                        }
                    case DATA_TYPE:
                        break;
                    case DRIVER:
                        pythonText += "def main():\n\t";
                        continue;
                    case FOR:
                        //Presently only accounts for a range of 0 to n. If the code specifies a starting range other than 0, that is not accounted for.
                        //Skip lexemes between "for" and the declaration of "i"
                        pythonText += tokenList.get(i).lexeme + " ";
                        while (tokenList.get(i).token != Interpreter.tokens.IDENTIFIER) {
                            i++;
                        }
                        pythonText += tokenList.get(i).lexeme;

                        //Skip lexemes until the end range is found
                        int counter = 0;
                        while (counter < 2) {
                            if (tokenList.get(i).token == Interpreter.tokens.INTEGER ||
                                    tokenList.get(i).token == Interpreter.tokens.LENGTH_FUNC) {
                                counter++;
                                if (counter == 2) {
                                    break;
                                }
                            }
                            i++;
                        }
                        if (tokenList.get(i).token == Interpreter.tokens.INTEGER) {
                            pythonText += " in range(" + tokenList.get(i).lexeme + "):\n\t\t";
                        } else if (tokenList.get(i).token == Interpreter.tokens.LENGTH_FUNC) {
                            int size = findSize(tokenList.get(i - 1).lexeme);
                            if (size == 0) {
                                pythonText += " in range(len(" + tokenList.get(i - 1).lexeme + ")):\n\t\t";
                            } else {
                                pythonText += " in range(" + findSize(tokenList.get(i - 1).lexeme) + "):\n\t\t";
                            }
                        }
                        while (tokenList.get(i).token != Interpreter.tokens.R_PAREN) {
                            i++;
                        }
                        break;
                    case LENGTH_FUNC:
                        int size = findSize(tokenList.get(i - 1).lexeme);
                        if (size == 0) {
                            pythonText += " len(" + tokenList.get(i - 1).lexeme + ")";
                        } else {
                            pythonText += findSize(tokenList.get(i - 1).lexeme);
                        }
                        break;
                    case NEXT_INT:
                        //This case currently doesn't support adding the print text to prompt the user to input something, into the
                        //the parantheses of the function itself. That is fairly complex and possibly not worth implementing.
                        pythonText += " input()";
                        i += 2;
                        break;
                    case NEW_LINE:
                        //This case is intended to resolve excess new_line characters, but doesn't work and ruins output
                        //so for now it just adds the new_line characters to the final output
                        pythonText += tokenList.get(i).lexeme;
//                        while (tokenList.get(i).token == Interpreter.tokens.NEW_LINE && i < tokenList.size()-2){
//                            i++;
//                        }
                        break;
                    case IMPORT:
                        //currently, there are no supported libraries to import so this
                        //will exist exclusively to skip all contents that follow import
                        while (tokenList.get(i + 1).token != Interpreter.tokens.DRIVER) {
                            i++;
                        }
                        break;
                    case INTEGER:
                        pythonText += tokenList.get(i).lexeme;
                        break;
                    case PRIMITIVE_DATA_TYPE:
                        pythonText += tokenList.get(i).lexeme + "\t";
                        break;
                    case SCANNER:
                        //All contents following "Scanner" in Java input are currently ignored
                        while (tokenList.get(i + 1).token != Interpreter.tokens.SEMI_COLON) {
                            i++;
                        }
                        break;
                    case SEMI_COLON:
                        break;
                }
            } else {
                switch (tokenList.get(i).token) {
                    case DECLARATION:
                        String replacedString = "";
                        if (tokenList.get(i).lexeme.equals("int")) {
                            replacedString = tokenList.get(i).lexeme.replace("int", "\t");
                            pythonText += replacedString;
                        }
                        break;
                    case PRINT: //Handling variable wrapping in strings.
                        i = printHandler(i);
//                        tokenList.get(i).lexeme = "\t" + tokenList.get(i).lexeme;
//                        try {
//                            if (tokenList.get(i + 1).token.equals(Interpreter.tokens.L_PAREN)
//                                    && tokenList.get(i + 2).token.equals(Interpreter.tokens.STRING)) {
//                                int startIndex = i +2; //index of beginning of string in the tokenList
//                                tokenList = new ArrayList<Interpreter.tokenInfo> (variableWrap(tokenList, startIndex));
//                                pythonText += tokenList.get(i).lexeme;
//                            }
//                        }catch(ArrayIndexOutOfBoundsException OOB){
//                            //End of array was reached.
//                            System.out.println(OOB.getMessage());
//                            break;
//                        }

                        break;
                    case STRING:
//                        System.out.println("STRING!!! -> " + tokenList.get(i).lexeme);
//                        tokenList.get(i).lexeme = cleanString(tokenList.get(i).lexeme);
//                        tokenList.get(i).lexeme = tokenList.get(i).lexeme.replace('"', ' ');

                        pythonText += tokenList.get(i).lexeme;
                        break;

                    default:
                        System.out.println(tokenList.get(i).token);
                        pythonText += tokenList.get(i).lexeme;
                        break;
                }
            }
        }
        pythonText += "\nif __name__ == \"__main__\": \n\tmain()";
        System.out.println(pythonText);
        gui.EP.setText(pythonText);
        checkAccuracy(pythonText);
    }

    // Find the size corresponding to an object or array.
    // Searches for the first occurrence of the name, where the object or array was first initialized
    // and the size was explicitly stored for that token
    public static int findSize(String lexeme) {
        int index = 0;
        int size = 0;
        while (!Objects.equals(lexeme, tokenList.get(index).lexeme)) {
            index++;
        }
        size = tokenList.get(index).size;
        return size;
    }

    /**
     * @param tokenList List of tokens to read (includes lexemes)
     * @param start     Identifies the beginning index for the print string.
     */
    public static ArrayList<Interpreter.tokenInfo> variableWrap(ArrayList<Interpreter.tokenInfo> tokenList, int start) {
        boolean varFound = false;
        ArrayList<Interpreter.tokenInfo> updatedList = new ArrayList<Interpreter.tokenInfo>();
        updatedList.addAll(tokenList);
        int end = getRParamIndex(updatedList, start); //index of R_Paren.
        updatedList.get(end - 1).lexeme = "";
        String containedString = "";


        for (int i = start; i < updatedList.size(); i++) {
            if (updatedList.get(i).token.equals(Interpreter.tokens.IDENTIFIER)) {
                updatedList.get(i).lexeme = "{" + updatedList.get(i).lexeme + "}";
                varFound = true;
            }
            if (updatedList.get(i).token.equals(Interpreter.tokens.ADD_OP)) {
                updatedList.get(i).lexeme = "";

            }
        }
        updatedList = new ArrayList<Interpreter.tokenInfo>(cleanString(updatedList, start, end));

        return updatedList;
    }

    /**
     * Used to find the end of a set of parenthesis. Helpful for string conversion to Python.
     *
     * @param start
     * @return The index of the right parenthesis
     */
    public static int getRParamIndex(ArrayList<Interpreter.tokenInfo> tokenList, int start) {
        int index = 0;
        for (int i = start; i < tokenList.size(); i++) {
            if (tokenList.get(i).token.equals(Interpreter.tokens.R_PAREN)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Cleans the string input to remove all quotation marks except the beginning and end.
     *
     * @return Cleaned string which only includes the first and last quotation marks.
     */
    public static ArrayList<Interpreter.tokenInfo> cleanString(ArrayList<Interpreter.tokenInfo> tokenList, int start, int end) {
        for (int i = start + 1; i < end - 1; i++) {
            if (tokenList.get(i).lexeme.equals('"')) {
                tokenList.get(i).lexeme = tokenList.get(i).lexeme.replace('"', '"');
            }
        }
        tokenList.get(start).lexeme = "f\"" + tokenList.get(start).lexeme;
        tokenList.get(end - 1).lexeme += "\"";
        return tokenList;
    }

    /**
     * Handles and converts a for loop starting at the provided index.
     *
     * @param index current index of the tokenList.
     * @return new index in the tokenList
     */
    public static int forLoopHandler(int index) {
        boolean assignmentIncluded = false;
        boolean valueIncluded = false;
        boolean comparatorIncluded = false;
        boolean rangeIncluded = false;
        boolean validForLoop = false;
        int semicolonIndex = 0;
        int val = 0;
        String identifier = "";

        pythonText += "for ";
        String forLoopText = "for "; //for debugging purposes. Want to see the python loop statement
        //index += 2;

        while (tokenList.get(index).token != Interpreter.tokens.R_PAREN) {
            if (tokenList.get(index).token == Interpreter.tokens.IDENTIFIER) {
                identifier = tokenList.get(index).lexeme;
                while (tokenList.get(index).token != Interpreter.tokens.SEMI_COLON || tokenList.get(index).token
                        != Interpreter.tokens.R_PAREN) {
                    if (tokenList.get(index).token == Interpreter.tokens.ASSIGN_OP)
                        /*If we are in the declaration part of the loop statement, the range should be in the
                        next part, disregard range until next semicolon delimiter.*/
                        assignmentIncluded = true;
                    rangeIncluded = false;
                    if (tokenList.get(index).token == Interpreter.tokens.INTEGER) {
                        valueIncluded = true;
                        val = Integer.parseInt(tokenList.get(index).lexeme);
                    }
                    if (tokenList.get(index + 1).token == Interpreter.tokens.SEMI_COLON) {
                        semicolonIndex = index + 1;
                    }
                    if (tokenList.get(index).token == Interpreter.tokens.LESS_THAN) {
                        /*If we are in the comparator part of the loop statement, the range should be in the
                        this part, disregard assignment check.*/
                        comparatorIncluded = true;
                        assignmentIncluded = false;
                        rangeIncluded = true;

                    }
                    index++;
                }
                if (valueIncluded && comparatorIncluded && rangeIncluded)
                    validForLoop = true;
            }
            index++;
        }

        if (validForLoop) {
            pythonText += identifier + "in range(" + val + "):\n\t";
            forLoopText += identifier + "in range(" + val + "):\n\t";
            System.out.println("FOR LOOP STATEMENT: " + forLoopText);
        }
        return index;
    }

    public static String cleanString(String s) {
        System.out.println("String before cleaning " + s);
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\"') {
                sb.setCharAt(i, '\0');
            }
        }
        System.out.println(sb);
        return sb.toString();
    }

    public static int printHandler(int index) {
        pythonText += "print(f\"";
        index++;
        while (tokenList.get(index).token != Interpreter.tokens.R_PAREN) {
            if (tokenList.get(index).token == Interpreter.tokens.IDENTIFIER) {
                tokenList.get(index).lexeme = "{" + tokenList.get(index).lexeme + "}";
                pythonText += tokenList.get(index).lexeme;
            } else if (tokenList.get(index).token == Interpreter.tokens.STRING) {
                tokenList.get(index).lexeme = cleanString(tokenList.get(index).lexeme);
                pythonText += tokenList.get(index).lexeme;
            } else {
                System.out.println("The token " + tokenList.get(index).token.toString() + " is not yet supported in print calls");
            }
            index++;
        }
        pythonText += "\"" + tokenList.get(index).lexeme;
        return index;
    }

    public static boolean checkAccuracy(String pythonText) throws IOException {
        boolean isAccurate = true;
        String caseNumber = Interpreter.file.getName().substring(5, 6);
        Path pythonPath = Path.of("Test Programs/Python Results/python" + caseNumber + ".txt");

        File pythonFile = new File(String.valueOf(pythonPath));
        String pythonCorrect = Files.readString(pythonPath, StandardCharsets.US_ASCII);

        int errors = 0;
        int i = 0;
        int j = 0;
        while (i < pythonCorrect.length() && j < pythonText.length()) {
            while (pythonCorrect.charAt(i) == ' ' || pythonCorrect.charAt(i) == '\n' ||
                    pythonCorrect.charAt(i) == '\r' || pythonCorrect.charAt(i) == '\t' || pythonCorrect.charAt(i) == '\0') {
                i++;
            }
            while (pythonText.charAt(j) == ' ' || pythonText.charAt(j) == '\n' ||
                    pythonText.charAt(j) == '\r' || pythonText.charAt(j) == '\t' || pythonText.charAt(j) == '\0') {
                j++;
            }

            if (pythonCorrect.charAt(i) != pythonText.charAt(j)) {
                errors++;
                isAccurate = false;
            }
            i++;
            j++;
        }
        gui.accuracy.setText("Accuracy results: ");
        gui.accuracy.setText(gui.accuracy.getText() + errors + " mismatches.");
        return isAccurate;
    }
}

