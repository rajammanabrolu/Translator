import java.io.*;
import java.util.*;

/**
 * The Java class has two variants: "JavaScript" and "Python." This determines whether the class
 * translates to JavaScript or Python. Private data for which language the class will translate to. Methods
 * in this class are called to translate from Java to either JavaScript or Python.
 * 
 *
 */
public class Java extends Language {

	public static final String I_STRUCTURE = "{";
	public static final String F_STRUCTURE = "}";
	public static final String PUBLIC = "public";
	public static final String VOID = "void";
	public static final String INT = "int";
	public static final String STRING = "String";
	public static final String DOUBLE = "double";
	public static final String CHAR = "char";
	public static final String BOOLEAN = "boolean";
	public static final String SEMI = ";";
	public static final String COMMENT = "//";
	public static final String JAVADOC1 = "/**";
	public static final String JAVADOC2 = "*";
	public static final String JAVADOC3 = "*/";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String WHILE = "while";
	public static final String FOR = "for";
	public static final String INPUT = "input.readLine()";

	/**
	 * Constructor method which creates a Java object with either "JavaScript" or "Python" as the String for
	 * the toLang for data.
	 * @param answer the language which the translator will translate to.
	 */
	public Java(String toLang) throws IOException {
		super(toLang);
	}

	/**
	 * translate takes in a string as a parameter and determines the translation of the entire line based
	 * on the first word of that string. A lengthy series of conditionals determines the translation and
	 * calls different methods with the string parameter.
	 * @param str string from the read method which contains syntax from the file.
	 * @throws IOException to use the BufferedWriter.
	 */
	@Override
	public void translate(String str) throws IOException {
		String[] syntax = str.split(" ");
		String first = syntax[0];
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
		} else if (first.equals(COMMENT)) {
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
		} else if (first.equals(F_STRUCTURE)) {
			if (toLang.equals("Javascript")) {
				bw.write(first);
				bw.newLine();
			}
		} else if (first.equals(PUBLIC)) {
			String next = syntax[1];
			if (next.equals("class")) {
				if (toLang.equals("Python")) {
					bw.write("class " + syntax[2] + "(object):");
					bw.newLine();
					bw.newLine();
				} else {
					// write nothing
				}
			} else if (!(next.equals(INT) || next.equals(VOID) || next.equals(DOUBLE) || next.equals(STRING)
					|| next.equals(BOOLEAN))) {
				this.constructor(str);
			} else {
				this.method(str);
			}
		} else if (first.equals(IF) || first.equals(WHILE) || first.equals(ELSE)) {
			this.ifWhileStatement(str); // same syntax for "if" and "while" statements
		} else if (first.equals(FOR)) {
			this.forLoop(str);
		} else {
			this.body(str);
		}
	}

	/**
	 * constructor takes in a string that represents a Java constructor. The method breaks apart the constructor
	 * and writes one in either JavaScript of Python.
	 * @param str The syntax that will be translated.
	 * @throws IOException in order to use the BufferedWriter bw.
	 */
	@Override
	public void constructor(String str) throws IOException {
		String[] syntax = str.split(" ");
		// public Java(String str, int y) {
		// public Java (String str, int y) { MUST WORK FOR ALL CASES
		// public Java ( String str , int y ) {
		String parameters = "";
		String parameter;
		for (int i = 0; i < syntax.length; i++) {
			parameter = syntax[i];
			if (!(parameter.equals(STRING) || parameter.equals(INT) || parameter.equals(DOUBLE)
					|| parameter.equals("(") || parameter.equals(")"))) {
				parameters+= " " + parameter.replace("(", "").replace(")", "");
			}
		}

		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String name = st.nextToken(); //"public"
		name = st.nextToken();
		String parameters = "";
		String parameter;
		do {
			parameter = st.nextToken().replace("(", "");
			if (parameter.equals(COMMENT)) {
				isComment = true;
				break;
			} else if (!(parameter.equals(STRING) || parameter.equals(INT) || parameter.equals(DOUBLE))) {
				parameters+= " " + parameter;
			}
		} while(st.hasMoreTokens());
		parameters = parameters.replace(")", "");
		if (toLang.equals("Python")) {
			if (parameters.equals(" ")) {
				bw.write("def __init__(self):");
			} else {
				bw.write("def __init__(self," + parameters + "):");
			}
		} else {
			bw.write("function " + name + " (" + parameters + ")");
		}
		if (isComment) {
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

	/**
	 * method accepts a string which is the method header of a Java method. From this, a method header for
	 * either JavaScript or Python is created with appropriate parameters.
	 * @param str the syntax which will be translated.
	 * @throws IOException to allow use of the private BufferedWriter bw.
	 */
	@Override
	public void method(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String name = st.nextToken(); //"public"
		name = st.nextToken(); //return type
		name = st.nextToken();
		String parameters = "";
		String parameter;
		do {
			parameter = st.nextToken().replace("(", "");
			if (parameter.equals(COMMENT)) {
				isComment = true;
				break;
			} else if (!(parameter.equals(CHAR) || parameter.equals(INT) || parameter.equals(BOOLEAN)
					|| parameter.equals(DOUBLE) || parameter.equals(STRING))) {
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
			bw.write("this." + name + " = function (" + parameters + ")");
		}
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
	 * body is the end of the conditional tree in the translate method. Any syntax that is not recognized there
	 * leads to the body method wherein rather specific code is translated.
	 * @param str the Java syntax.
	 * @throws IOException in order to use the BufferedWriter.
	 */
	@Override
	public void body(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if (first.equals("System.out.println") || first.equals("return") || str.indexOf("this.") >= 0) {
			String rest = "";
			String next;
			while (st.hasMoreTokens()) {
				next = st.nextToken();
				if (next.equals(COMMENT)) {
					isComment = true;
					break;
				}
				rest+= " " + next;
			}
			if (first.equals("System.out.println")) { // print line
				if (toLang.equals("Python")) {
					bw.write("print" + rest.replace(SEMI, ""));
				} else {
					bw.write("console.log" + rest);
				}
			} else if (first.equals("return")) { // return statement
				if (toLang.equals("Python")) {
					bw.write(first + rest.replace(SEMI, ""));
				} else {
					bw.write(first + rest);
				}
			} else { // method call
				if (toLang.equals("Python")) {
					bw.write(first.replace("this.", "self.") + rest.replace(SEMI, ""));
				} else {
					bw.write(first.replace("this.", "") + rest);
				}
			}
			bw.newLine();
		}
		else if (str.indexOf("=") > 0) { // variable declaration
			String equals = st.nextToken(); //"="
			String value;
			if (!(equals.equals("="))) { // presence of a different variable type
				first = equals;
				value = st.nextToken();
				value = st.nextToken();
			} else {
				value = st.nextToken();
				if (str.indexOf(INPUT) >= 0) { // input statement
					String rest = "";
					String next;
					while (st.hasMoreTokens()) {
						next = st.nextToken();
						if (next.equals(COMMENT)) {
							isComment = true;
							break;
						}
						rest+= " " + next;
					}
					if (toLang.equals("Python")) {
						bw.write("input(" + rest);
					} else {
						bw.write("prompt(" + rest + ");");
					}
					bw.newLine();
				}
			}
			if (toLang.equals("Python")) {
				bw.write("self." + first + " = " + value.replace(";", ""));
			} else {
				bw.write("this." + first + " = " + value);
			}
			bw.newLine();
		} else if (first.equals("private")) {
			// write nothing and do not add a new line
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
				bw.write(rest); //write whatever is contained.
			}
			bw.newLine();
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
		catch (NoSuchElementException e) {
			// no comment
		}
	}

	/**
	 * forLoop translate a Java for loop (both a for-each and regular for loop) into either JavScript or Python.
	 * Loops consisting of multiplication or division are not correctly translated; only ++, --, x = x + ..., or
	 * x = x -... are accepted.
	 * @param str syntax containing the for loop in the Java language.
	 * @throws IOException to use the BufferedWriter.
	 */
	@Override
	public void forLoop(String str) throws IOException {
		StringTokenizer st = new StringTokenizer(str);
		try {
			String tFor = st.nextToken(); // "for"
			String name = st.nextToken(); // "(int"
			name = st.nextToken(); //"x"
			String determine = st.nextToken(); // ":" or "="
			if (determine.equals(":")) { // check for a for-each loop.
				String object = st.nextToken();
				if (toLang.equals("Python")) {
					object = object.replace(")", "");
					bw.write(tFor + " " + name + " in " + object + ":");
				} else {
					bw.write(tFor + "(var " + name + " in " + object);
				}
			} else { // regular for loop.
				String start = st.nextToken(); // starting integer
				name = st.nextToken(); // "x"
				String operator = st.nextToken(); // "<"
				String stop = st.nextToken(); // ending integer
				String step = st.nextToken();
				if (step.indexOf("++") > 0 || step.indexOf("--") > 0) {
					if (toLang.equals("Python")) {
						start = start.replace(SEMI, "");
						stop = stop.replace(SEMI, "");
						bw.write(tFor + " " + name + " in range(" + start + ", " + stop + "):");
					} else {
						bw.write(tFor + " (var " + name + " " + determine + " " + start + " "
								+ name + " " + operator + " " + stop + " " + step);
					}
				} else {
					if (toLang.equals("Python")) {
						String increment = st.nextToken();
						increment = st.nextToken();
						increment = st.nextToken();
						increment = st.nextToken();
						start = start.replace(SEMI, "");
						stop = stop.replace(SEMI, "");
						bw.write(tFor + " " + name + " in range(" + start + ", " + stop + ", "
								+ increment + ":");
					} else {
						String increment = st.nextToken();
						increment = st.nextToken();
						increment = st.nextToken();
						String arithmetic = increment;
						increment = st.nextToken();
						bw.write(tFor + " (var " + name + " " + determine + " " + start + " "
								+ name + " " + operator + " " + stop + " " + name + " " + determine
								+ " " + name + " " + arithmetic + " " + increment);
					}
				}
			}
		}
		catch (NoSuchElementException e) {
			bw.write("Error in 'for'-loop.");
		}
		try {
			if (st.nextToken().equals(COMMENT)) {
				String comment = "";
				while(st.hasMoreTokens()) {
					comment+= " " + st.nextToken();
				}
				this.lineComment(comment);
			}
		}
		catch (NoSuchElementException e) {
			// No comment.
		}
	}

	/**
	 * ifWhileStatement translates if statements and while loops in tandem because both types of syntax have
	 * similar syntax. The method translates from Java to either Javascript of Python.
	 * @param str the code of the if statement or while loop.
	 * @throws IOException to allow the use of the BufferedWriter.
	 */
	@Override
	public void ifWhileStatement(String str) throws IOException {
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if (first.equals(ELSE)) {
			bw.write(ELSE);
		} else {
			bw.write(first + " "); // "if" or "while"
			String syntax;
			do {
				syntax = st.nextToken();
				if (syntax.equals(COMMENT)) {
					isComment = true;
					break;
				}
				if (toLang.equals("Python")) {
					if (syntax.equals("||")) {
						bw.write(" or ");
					} else if (syntax.equals("&&")) {
						bw.write(" and ");
					} else if (syntax.equals("!")) {
						bw.write(" not ");
					} else {
						bw.write(syntax.replace(")", "").replace("(", "")); // Write whatever is being
																			// compared or inequalities.
					}
				} else {
					bw.write(" " + syntax + " "); // Same syntax as Java.
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
	 * of syntax. Because of this, it must be in a "try-catch" block or with the use of a boolean variable as
	 * there may be a comment after any line of code.
	 * @param str the syntax which will be converted into a line comment.
	 * @throws IOException in order to use the BufferedWriter.
	 */
	@Override
	public void lineComment(String str) throws IOException {
		if (toLang.equals("Python")) {
			bw.write(" #" + str.replace(COMMENT, ""));
		} else {
			bw.write(" //" + str.replace(COMMENT, ""));
		}
	}

	/**
	 * longComment checks for JavaDoc comments in the file and converts them into the appropriate
	 * form for either language it will translate into.
	 * @param str the syntax which will be translated into a longer comment.
	 * @throws IOException for use of the private BufferedWriter.
	 */
	@Override
	public void longComment(String str) throws IOException {
		if (toLang.equals("Python")) {
			bw.write(str.replace("* ", ""));
		} else {
			bw.write(str);
		}
	}
}
