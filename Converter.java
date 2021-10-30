import java.util.ArrayList;

public class Converter {
    private static GUI gui;
    public Converter(GUI gui) {
        this.gui = gui;
    }
    static String pythonText = "";
    static ArrayList<Interpreter.tokenInfo> tokenList;

    static public void runConverter(ArrayList<Interpreter.tokenInfo> oldTokenList){
        tokenList = oldTokenList;
        //for each token in tokenList, determine the Python equivalent
        for (int i = 0; i < tokenList.size(); i++){
            if (tokenList.get(i).special) {
                switch (tokenList.get(i).token) {
                    case DRIVER:
                        pythonText += "def " + tokenList.get(i).lexeme + "():\n\t";
                        i += 8;
                        continue;
                    case INTEGER:
                        pythonText += tokenList.get(i).lexeme;
                        break;

                    case DATA_TYPE:
                        break;
                    case PRIMITIVE_DATA_TYPE:
                        pythonText += tokenList.get(i).lexeme + "\t";
                        break;
                }
            }
            if (!tokenList.get(i).special) {
                switch(tokenList.get(i).token){
                    case DECLARATION:
                        String replacedString = "";
                        if(tokenList.get(i).lexeme.equals("int")){
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
                        System.out.println("STRING!!! -> " + tokenList.get(i).lexeme);
                        tokenList.get(i).lexeme = cleanString(tokenList.get(i).lexeme);
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
    }

    /**
     *
     * @param tokenList List of tokens to read (includes lexemes)
     * @param start Identifies the beginning index for the print string.
     */
    public static ArrayList<Interpreter.tokenInfo> variableWrap (ArrayList<Interpreter.tokenInfo> tokenList, int start){
        boolean varFound = false;
        ArrayList<Interpreter.tokenInfo> updatedList = new ArrayList<Interpreter.tokenInfo>();
        updatedList.addAll(tokenList);
        int end = getRParamIndex(updatedList, start); //index of R_Paren.
        updatedList.get(end-1).lexeme = "";
        String containedString = "";



        for (int i = start; i < updatedList.size(); i++){
            if (updatedList.get(i).token.equals(Interpreter.tokens.IDENTIFIER)){
                updatedList.get(i).lexeme = "{" + updatedList.get(i).lexeme + "}";
                varFound = true;
            }
            if(updatedList.get(i).token.equals(Interpreter.tokens.ADD_OP)){
                updatedList.get(i).lexeme = "";

            }
        }
        updatedList = new ArrayList<Interpreter.tokenInfo>(cleanString(updatedList, start, end));

        return updatedList;
    }

    /**
     * Used to find the end of a set of parenthesis. Helpful for string conversion to Python.
     * @param start
     * @return The index of the right parenthesis
     */
    public static int getRParamIndex(ArrayList<Interpreter.tokenInfo> tokenList, int start){
        int index = 0;
        for(int i = start; i < tokenList.size(); i++){
            if(tokenList.get(i).token.equals(Interpreter.tokens.R_PAREN)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Cleans the string input to remove all quotation marks except the beginning and end.
     * @return Cleaned string which only includes the first and last quotation marks.
     */
    public static ArrayList<Interpreter.tokenInfo> cleanString(ArrayList<Interpreter.tokenInfo> tokenList, int start, int end){
        for (int i = start+1; i < end -1; i++){
            if(tokenList.get(i).lexeme.equals('"')){
                tokenList.get(i).lexeme = tokenList.get(i).lexeme.replace('"', '"');
            }
        }
        tokenList.get(start).lexeme = "f\"" + tokenList.get(start).lexeme;
        tokenList.get(end-1).lexeme += "\"";
        return tokenList;
    }

    public static String cleanString(String s){
        System.out.println("String before cleaning " + s);
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '\"') {
                sb.setCharAt(i, '\0');
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static int printHandler(int index){
        pythonText += "print(f\"";
        index += 2;
        while(tokenList.get(index).token != Interpreter.tokens.R_PAREN) {
            if (tokenList.get(index).token == Interpreter.tokens.VARIABLE){
                tokenList.get(index).lexeme = "{" + tokenList.get(index).lexeme + "}";
                pythonText += tokenList.get(index).lexeme;
            } else if(tokenList.get(index).token == Interpreter.tokens.STRING){
                tokenList.get(index).lexeme = cleanString(tokenList.get(index).lexeme);
                pythonText += tokenList.get(index).lexeme;
            } else{
                System.out.println("This token :" + tokenList.get(index).token.toString() + ", is not yet supported in print calls");
            }
            index++;
        }
        pythonText +=  "\"" +tokenList.get(index).lexeme;
        return index;
    }
}

