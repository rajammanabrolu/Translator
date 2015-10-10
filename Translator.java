import java.io.*;

/**
 * The Translator class is used as the main class the user can use to choose which language he or she would
 * like to translate to. Note: Syntax in the original language must not contain empty lines for spacing.
 * The resulting translation does not contain indentation.
 * 
 * @author Nikola Istvanic
 *
 */
public class Translator {

    /**
     * answer asks the user to enter which language he or she has and would like to translate to. The answer to
     * this question determines which type of language object will be created.
     * @throws IOException to accept user input for the names of the appropriate languages.
     */
    public static void answer() throws IOException {
        InputStreamReader reader = new InputStreamReader (System.in);
        BufferedReader input = new BufferedReader (reader);
        System.out.println("Enter what language you want translated to which language (ex: 'Python to Java'): ");
        String answer = input.readLine().toLowerCase();
        while (!(answer.equals("python to java") || answer.equals("python to javascript") || 
                answer.equals("java to python") || answer.equals("java to javascript") || 
                answer.equals("javascript to java") || answer.equals("javascript to python"))) {
            System.out.println("Invalid command. Re-enter: ");
            answer = input.readLine().toLowerCase();
        }
        if (answer.equals("python to java")) {
            Language p = new Python("Java");
            p.read();
        } else if (answer.equals("python to javascript")) {
            Language p = new Python("Javascript");
            p.read();
        } else if (answer.equals("java to python")) {
            Language j = new Java("Python");
            j.read();
        } else if (answer.equals("java to javascript")) {
            Language j = new Java("Javascript");
            j.read();
        } else if (answer.equals("javascript to java")) {
            Language js = new JavaScript("Java");
            js.read();
        } else {
            Language js = new JavaScript("Python");
            js.read();
        }
    }

    /**
     * Main method of the Translator class. Here, a Translator object is created and operated on using the
     * answer method wherein different language objects are created.
     * @throws IOException to accept user input to answer the questions associated with answer().
     */
    public static void main(String[] args) throws IOException {
    	answer();
    }
}