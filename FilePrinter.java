import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class FilePrinter {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("Test Programs/Test 1.txt");
        Scanner scan = new Scanner(file);

        while(scan.hasNextLine()) {
            System.out.println(scan.nextLine());
        }
    }
}

