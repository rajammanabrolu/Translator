import java.io.*;

/**
 * The Language class is an abstract class which is extended by the three languages the translator offers:
 * Java, JavaScript, and Python. Each of the defined methods are implemented in these respective classes.
 * 
 * @author Nikola Istvanic
 *
 */
public abstract class Language {
	public String toLang;
	public File outFile = new File("C://Users//Nick//translation.txt");
	public BufferedWriter bw;
	
	/**
	 * Constructor which lays out the basic framework of a Language-implementing object.
	 * @param toLang a string representing to which language the object's language is being translated
	 * @throws IOException for use of the buffered writer
	 */
	public Language(String toLang) throws IOException {
		this.toLang = toLang;
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);
	}
	
	/**
	 * read allows for the user to enter the name of the file from which the original language will be read.
	 * The method calls translate with each line read from the file which creates the translation.
	 * @throws IOException to accept user input for the name of the file.
	 */
	public void read() throws IOException {
		InputStreamReader reader = new InputStreamReader (System.in);
		BufferedReader input = new BufferedReader (reader);
		System.out.println("Enter the name of the text file imported: ");
		FileReader readFile = new FileReader(input.readLine());
		BufferedReader inFile = new BufferedReader(readFile);
		System.out.println();
		System.out.println("Reading file...");
		String inputString = inFile.readLine();
		while (inputString != null) {
			System.out.println(inputString);
			this.translate(inputString);
			inputString = inFile.readLine();
		}
		System.out.println("Reading completed.");
		bw.close();
		inFile.close();
	}
	
	/**
	 * translate is meant to take in a string representing one line of code in a certain language which will then
	 * be passed through a series of conditionals to determine which of the below methods should be applied to
	 * that line of code.
	 * @param str the line of code of the original language which will be translated.
	 * @throws IOException to allow the use of a BufferedWriter.
	 */
	public abstract void translate(String str) throws IOException;
	
	/**
	 * constructor will take in a string representing the constructor method of a language and translate it into
	 * an equivalent constructor in the language the translator will translate to.
	 * @param str the string representing the constructor of the original language.
	 * @throws IOException for permission to use a private BufferedWriter.
	 */
	public abstract void constructor(String str) throws IOException;
	
	/**
	 * method will convert a string parameter representing the method header in the original method into the correct
	 * language being translated into.
	 * @param str the method header as one line and a string.
	 * @throws IOException for use of a BufferedWriter.
	 */
	public abstract void method(String str) throws IOException;
	
	/**
	 * body will translate any miscellaneous code from the original language to the correct one based on a series
	 * of conditionals.
	 * @param str one line of code from the body of a method, constructor, or loop.
	 * @throws IOException in order to be able to use a private BufferedWriter.
	 */
	public abstract void body(String str) throws IOException;
	
	/**
	 * forLoop will convert a for loop from one computer language to another in any form that for loop can be in.
	 * @param str the entire for loop declaration in one string.
	 * @throws IOException for access to a BufferedWriter.
	 */
	public abstract void forLoop(String str) throws IOException;
	
	/**
	 * ifWhileStatement will metamorphose either an if statement or while loop from one language to another
	 * because of their similar syntax; rather than have two methods for each of these blocks of code, it is
	 * more efficient to have one.
	 * @param str the string containing the if statement or while loop header.
	 * @throws IOException for allowed use of a BufferedWriter.
	 */
	public abstract void ifWhileStatement(String str) throws IOException;
	
	/**
	 * lineComment will take a single line comment from one language and convert it to a single line comment in
	 * another language through the use of one basic conditional.
	 * @param str the entire line comment.
	 * @throws IOException to request use of a private BufferedWriter to write to a separate file the translation.
	 */
	public abstract void lineComment(String str) throws IOException;
	
	/**
	 * longComment will take longer comments such as JavaDocs from one language and convert them to the respective
	 * form in another, again, through one simple conditional. 
	 * @param str one line of that line comment.
	 * @throws IOException to allow the use of a BufferedWriter for writing the translation to another file.
	 */
	public abstract void longComment(String str) throws IOException;
}