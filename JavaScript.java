import java.io.*;
import java.util.*;

/**
 * The JavaScript class has two variants: "Java" and "Python." This determines whether the class translates
 * to Java or Python. Private data for which language the class will translate to. Methods in this class
 * are called to translate from Java to either Java or Python.
 *
 * @author Nikola Istvanic
 *
 */
public class JavaScript extends Language {

	public static final String I_STRUCTURE = "{";
	public static final String F_STRUCTURE = "}";
	public static final String FUNC = "function";
	public static final String THIS = "this";
	public static final String SEMI = ";";
	public static final String COMMENT = "//";
	public static final String JAVADOC1 = "/**";
	public static final String JAVADOC2 = "*";
	public static final String JAVADOC3 = "*/";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String WHILE = "while";
	public static final String BOOLEAN = "boolean";
	public static final String FOR = "for";
	public static final String INPUT = "prompt";

	/**
	 * JavaScript is the constructor method for a JavaScript object. The toLang field determines to which
	 * language the translator translates.
	 * @param answer either "Java" or "Python," it controls which language the translator writes to.
	 * @throws IOException for the use of the BufferedWriter.
	 */
	public JavaScript(String toLang) throws IOException {
		super(toLang);
	}

	/**
	 * translate takes in a string as a parameter and determines the correct translation of the entire line
	 * based on the very first word of that line. Through a rather lengthy series of conditionals, the method
	 * determines the translation and calls various methods with the same string parameter.
	 * @param str string from the read method which contains syntax from the file.
	 * @throws IOException in order to use the BufferedWriter to write to a separate file.
	 */
	@Override
	public void translate(String str) throws IOException {
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if (first.equals(JAVADOC1)) {
			if (toLang.equals("Python")) {
				bw.write("\"\"\"");
				bw.newLine();
			} else {
				bw.write(first);
				bw.newLine();
			}
		} else if (first.equals(JAVADOC2)) {
			this.longComment(str);
			bw.newLine();
		} else if (first.equals(JAVADOC3)) {
			if (toLang.equals("Python")) {
				bw.write("\"\"\"");
				bw.newLine();
			} else {
				bw.write(first);
				bw.newLine();
			}
		}
		else if (first.equals(COMMENT)) {
			this.lineComment(str);
			bw.newLine();
		} else if (first.equals(I_STRUCTURE)) {
			if (toLang.equals("Python")) {
				bw.newLine();
			} else {
				bw.newLine();
				bw.write(first);
				bw.newLine();
			}
		} else if (first.equals(F_STRUCTURE) || first.equals(F_STRUCTURE + SEMI)) {
			if (toLang.equals("Java")) {
				bw.write(F_STRUCTURE);
				bw.newLine();
			}
		} else if (first.equals(FUNC)) {
			this.constructor(str);
		} else if (first.indexOf(THIS) >= 0) {
			if (str.indexOf("(") > 0) {
				this.method(str);
			} else {
				this.body(str); //variable declaration
				bw.newLine();
			}
		} else if (first.equals(IF) || first.equals(WHILE) || first.equals(ELSE)) {// Same syntax for "if" and
																			   //"while" statements{
			this.ifWhileStatement(str);
		} else if (first.equals(INPUT)) {
			this.body(str);
		} else if(first.equals(FOR)) {
			this.forLoop(str);
		} else {
			this.body(str);
			bw.newLine();
		}
	}

	/**
	 * constructor writes an accurate constructor for the language the translator translates to. Although
	 * Java and Python's constructors are rather different, the method is still able to create a translation
	 * for a functioning constructor.
	 * @param str the syntax that will be translated.
	 * @throws IOException in order to use the BufferedWriter bw.
	 */
	@Override
	public void constructor(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String name = st.nextToken(); //"function"
		name = st.nextToken();
		String parameters = "";
		String parameter;
		do {
			parameter = st.nextToken().replace("(", "");
			if (parameter.equals(COMMENT)) {
				isComment = true;
				break;
			}
			if (toLang.equals("Java")) {
				parameters+= " int " + parameter; //parameters are assumed to be integers.
			} else {
				parameters+= " " + parameter;
			}
		} while(st.hasMoreTokens());
		parameters = parameters.replace(")", "");
		if (toLang.equals("Python")) {
			bw.write("class " + name + "(object):");
			bw.newLine();
			bw.newLine();
			if (parameter.equals("")) {
				bw.write("def __init__(self):");
			} else {
				bw.write("def __init__(self," + parameters + "):");
			}
		} else {
			StringTokenizer priv = new StringTokenizer(parameters);
			ArrayList<String> privates = new ArrayList<String>();
			String param;
			do {
				param = priv.nextToken().replace(",", "");
				privates.add(param);
			} while (priv.hasMoreTokens());
			bw.write("public class " + name);
			bw.newLine();
			bw.write(I_STRUCTURE);
			bw.newLine();
			for(int x = 0; x < privates.size(); x++) {
				String result = privates.get(x);
				if (!(result.equals("int"))) {
					bw.write("private int p" + privates.get(x) + ";"); //writing private variables
					bw.newLine();
				}
			}
			bw.write("public " + name + " (" + parameters + ")");
		}
		if (isComment) {
			String comment = "";
			while (st.hasMoreTokens()) {
				comment+= " " + st.nextToken();
			}
			this.lineComment(comment);
		}
	}

	/**
	 * The method method uses a the string from its parameter to create an equivalent method header for a
	 * different computer language. In the case of to Java, methods are assumed to be void.
	 * @param str the code containing the method header.
	 * @throws IOException in order to use the BufferedWriter bw for file writing.
	 */
	@Override
	public void method(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String name = st.nextToken().replace("this.", ""); //"this.calcArea"
		String parameters = "";
		String parameter;
		do {
			parameter = st.nextToken().replace("(", "");
			if (parameter.equals(COMMENT)) {
				isComment = true;
				break;
			} else if (!(parameter.equals("=") || parameter.equals("function"))) {
				parameters+= " " + parameter;
			}
		} while (st.hasMoreTokens());
		parameters = parameters.replace(")", "");
		if (toLang.equals("Python")) {
			bw.newLine();
			bw.write("def " + name);
			if (parameters.equals(" ")) {
				bw.write("(self):");
			} else {
				bw.write("(self, " + parameters.replace(" ", "") + "):");
			}
		} else {
			bw.write("public void " + name + " (" + parameters.replace(" ", "") + ")");
		}			//methods are void by default.
		if (isComment) {
			String comment = "";
			String nextField;
			do {
				nextField = st.nextToken();
				comment+= " " + nextField;
			} while (st.hasMoreTokens());
			this.lineComment(comment);
		}
	}

	/**
	 * body takes any syntax that was rejected from all of the other conditionals of the translate method. In
	 * this method, code such as printing, returning, and assigning variables is translated.
	 * @param str the syntax of the body of a method in Javascript.
	 * @throws IOException in order to write to a file using the BufferedWriter bw.
	 */
	@Override
	public void body(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if (first.equals("console.log") || first.equals("return") || first.equals(INPUT)) {
			String rest = "";
			String next;
			while (st.hasMoreTokens()) {
				next = st.nextToken();
				if (next.equals(COMMENT)) {
					isComment = true;
					break;
				} else if (next.indexOf(THIS) >=0) {
					if (toLang.equals("Java")) {
						next = next.replace(THIS + ".", "p"); //private variable
					} else {
						next = next.replace(THIS + ".", "");
					}
				}
				rest+= " " + next;
			} 
			if (first.equals("console.log")) { //print line 
				if (toLang.equals("Python")) {
					bw.write("print" + rest.replace(SEMI, ""));
				} else {
					bw.write("System.out.println" + rest);
				}
			} else if (first.equals("return")) { // return statement
				if (toLang.equals("Python")) {
					bw.write(first + rest.replace(SEMI, ""));
				} else {
					bw.write(first + rest);
				}
			} else { //input line(s)
				if (toLang.equals("Python")) {
					bw.write("input" + rest.replace(SEMI, ""));
				} else {
					bw.write("System.out.println" + rest);
					bw.newLine();
					bw.write("String answer = input.readLine();");
					bw.newLine();
				}
			}
		} else if (str.indexOf("=") > 0) { //variable declaration
			first = first.replace("this.", "");
			String equals = st.nextToken(); //"="
			String value = st.nextToken();
			if (toLang.equals("Python")) {
				bw.write("self." + first + " " + equals + " " + value.replace(";", ""));
			} else {
				bw.write("p" + first + " " + equals + " " + value);
			}
		} else {
			String rest = "";
			String next = st.nextToken();
			while (st.hasMoreTokens()) {
				if (next.equals(COMMENT)) {
					isComment = true;
					break;
				}
				rest+= next;
				next = st.nextToken();
			}
			if (toLang.equals("Python")) {
				bw.write(rest.replace(";", ""));
			} else {
				bw.write(rest);
			}
		}
		try {
			if (isComment || st.nextToken().equals(COMMENT)) {
				String comment = "";
				String next;
				do {
					next = st.nextToken();
					if (!(next.equals(COMMENT))) {
						comment+= " " + next;
					}
				} while (st.hasMoreTokens());
				this.lineComment(comment);
			}
		}
		catch (NoSuchElementException e)
		{
			//no comment
		}
	}

	/**
	 * forLoop translates for loops from Javascript to either Java or Python. Both regular and for - each loops
	 * work for this method, such that that method does not contain multiplication or division in the iterative
	 * step of the loop.
	 * @param str the syntax just containing the for loop declaration.
	 * @throws IOException to be able to write to the file translation.txt with the BufferedWriter.
	 */
	public void forLoop(String str) throws IOException
	{
		StringTokenizer st = new StringTokenizer(str);
		try
		{
			String tFor = st.nextToken(); //"for"
			String name = st.nextToken(); //"(var"
			name = st.nextToken(); //"x"
			String determine = st.nextToken(); //"in" or "="
			if(determine.equals("in")) //check for a for-each loop.
			{
				String object = st.nextToken();
				if(toLang.equals("Python"))
				{
					object = object.replace(")", "");
					bw.write(tFor + " " + name + " " + determine + " " + object + ":");
				}
				else
				{
					bw.write(tFor + " (int " + name + " : " + object);
				}
			}
			else //regular for loop.
			{
				String start = st.nextToken(); //starting integer
				name = st.nextToken(); //"x"
				String operator = st.nextToken(); //"<"
				String stop = st.nextToken(); //ending integer
				String step = st.nextToken();
				if(step.indexOf("++") > 0 || step.indexOf("--") > 0)
				{
					if(toLang.equals("Python"))
					{
						start = start.replace(";", "");
						stop = stop.replace(";", "");
						bw.write(tFor + " " + name + " in range(" + start + ", " + stop + "):");
					}
					else
					{
						bw.write(tFor + " (int " + name + " " + determine + " " + start + " "
								+ name + " " + operator + " " + stop + " " + step);
					}
				}
				else
				{
					if(toLang.equals("Python"))
					{
						String increment = st.nextToken();
						increment = st.nextToken();
						increment = st.nextToken();
						increment = st.nextToken();
						start = start.replace(";", "");
						stop = stop.replace(";", "");
						bw.write(tFor + " " + name + " in range(" + start + ", " + stop + ", "
								+ increment + ":");
					}
					else
					{
						String increment = st.nextToken();
						increment = st.nextToken();
						increment = st.nextToken();
						String arithmetic = increment;
						increment = st.nextToken();
						bw.write(tFor + " (int " + name + " " + determine + " " + start + " "
								+ name + " " + operator + " " + stop + " " + name + " " + determine
								+ " " + name + " " + arithmetic + " " + increment);
					}
				}
			}
		}
		catch(NoSuchElementException e)
		{
			bw.write("Error in 'for'-loop.");
		}
		try
		{
			if(st.nextToken().equals(COMMENT))
			{
				String comment = "";
				while (st.hasMoreTokens())
				{
					comment+= " " + st.nextToken();
				}
				this.lineComment(comment);
			}
		}
		catch(NoSuchElementException e)
		{
			//No comment.
		}
	}

	/**
	 * ifWileStatement allows for the translation of if statements and while loops together. This is because of
	 * their analogous code: if (x || y) and while (x || y) are similar in structure and allow for efficient
	 * translation.
	 * @param str the code containing either the if statement or the while loop.
	 * @throws IOException allowing for the use of the private BufferedWriter bw.
	 */
	@Override
	public void ifWhileStatement(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if (first.equals(ELSE)) {
			bw.write(first);
			bw.newLine();
		} else {
			bw.write(first); //"if" or "while"
			String syntax;
			do {
				syntax = st.nextToken();
				if (syntax.equals(COMMENT)) {
					isComment = true;
					break;
				} else if (toLang.equals("Python")) {
					if (syntax.equals("||")) {
						bw.write(" or ");
					} else if (syntax.equals("&&")) {
						bw.write(" and ");
					} else if (syntax.equals("!=")) {
						bw.write(" not ");
					} else {
						bw.write(" " + syntax.replace("(", "").replace(")", "")); //Write whatever is being
																				  //compared or inequalities.
					}
				} else {
					bw.write(" " + syntax + " "); //Same syntax as Java.
				}
			} while (st.hasMoreTokens());
			if (toLang.equals("Python")) {
				bw.write(":");
			}
		}
		if (isComment) {
			String comment = "";
			while (st.hasMoreTokens()) {
				comment+= " " + st.nextToken();
			}
			this.lineComment(comment);
		}
	}

	/**
	 * lineComment is called at the end of every method to see if there is a comment after any amount
	 * of syntax. Because of this, it must be in a "try-catch" block or with the aid of a boolean variable in
	 * every method as there might not be a comment.
	 * @param str the syntax which will be converted into a comment.
	 * @throws IOException in order to use the BufferedWriter.
	 */
	@Override
	public void lineComment(String str) throws IOException
	{
		if(toLang.equals("Python"))
		{
			bw.write(" #" + str.replace(COMMENT, ""));
		}
		else
		{
			bw.write(" //" + str.replace(COMMENT, ""));
		}
	}

	/**
	 * longComment checks for JavaDoc-like comments in the file and converts them into the appropriate form
	 * for either language it will translate into.
	 * @param str the syntax which will be translated into a longer comment.
	 */
	@Override
	public void longComment(String str) throws IOException {
		if(toLang.equals("Python"))
		{
			bw.write(str.replace("*", ""));
		}
		else
		{
			bw.write(str);
		}
	}
}