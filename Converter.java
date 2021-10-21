import java.util.ArrayList;

public class Converter {
    private static GUI gui;
    public Converter(GUI gui) {
        this.gui = gui;
    }

    static public void runConverter(ArrayList<Interpreter.tokenInfo> tokenList){
        String pythonText = "";
        //for each token in tokenList, determine the Python equivalent
        for (int i = 0; i < tokenList.size(); i++){
            if (tokenList.get(i).special) {
                switch (tokenList.get(i).token) {
                    case DRIVER:
                        pythonText += "def " + tokenList.get(i).lexeme + "():";
                        i += 8;
                        break;
                }
            } else {
                pythonText += tokenList.get(i).lexeme;
            }
        }
        pythonText += "\nif __name__ == \"__main__\": \n\tmain()";
        System.out.println(pythonText);
        gui.EP.setText(pythonText);
    }
}

