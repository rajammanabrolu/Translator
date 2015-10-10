import java.io.*;
import java.util.*;

/**
 * The Python class accepts code from the language Python and translates into either Java or Javascript. The
 * class contains the same methods as classes Java and Javascript but has dissimilar conditionals and syntax
 * to write to the two classes. Private data in this class, in addition to the language which the translator
 * translates to, includes the name of the class in the translation.
 * 
 *
 */
public class Python extends Language {
	private String className = ""; // Python constructors do not include a name; the name of the class is the name of the constructor.

	public static final String I_STRUCTURE = ":";
	public static final String F_STRUCTURE = ">"; // used to denote the end of a method in Python (StringTokenizer cannot read a new line).
	public static final String FUNC = "def";
	public static final String JAVADOC1 = "\"\"\"";
	public static final String JAVADOC2 = "*"; // used to denote the beginning of a long comment (such a character does not exist in the Python language).
	public static final String JAVADOC3 = "\"\"\"\""; // used to denote the end of a long comment (same as the beginning of a new long comment (used to differentiate)).
	public static final String COMMENT = "#";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String WHILE = "while";
	public static final String FOR = "for";
	public static final String INPUT = "input";

	/**
	 * The constructor method for a Python object with a parameter representing the language the code in
	 * Python will be changed to.
	 * @param answer the language which the syntax will be translated into.
	 * @throws IOException in order to write to the aforementioned private file with the private BufferedWriter.
	 */
	public Python(String toLang) throws IOException {
		super(toLang);
	}

	/**
	 * translate takes in a string as a parameter and determines the translation of the entire line based
	 * on the first word of that line. A lengthy series of conditionals determines the translation and
	 * calls different methods with the string parameter.
	 * @param str string from the read method which contains syntax from the file.
	 * @throws IOException to be allowed access to the BufferedWriter bw.
	 */
	public void translate(String str) throws IOException
	{
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if(first.equals(JAVADOC1))
		{
			bw.write("/**");
			bw.newLine();
		}
		else if(first.equals(JAVADOC2))
		{
			this.longComment(str);
			bw.newLine();
		}
		else if(first.equals(JAVADOC3))
		{
			bw.write("*/");
			bw.newLine();
		}
		else if(first.equals(COMMENT))
		{
			this.lineComment(str);
			bw.newLine();
		}
		else if(first.equals(F_STRUCTURE))
		{
			bw.write("}");
			bw.newLine();
		}
		else if(first.equals("class"))
		{
			className = st.nextToken();
			if(toLang.equals("Java"))
			{
				bw.write("public class " + className);
				bw.newLine();
				bw.write("{");
				bw.newLine();
			}
		}
		else if(first.equals(FUNC))
		{
			if(st.nextToken().indexOf("__init__") >= 0)
			{
				this.constructor(str);
			}
			else
			{
				this.method(str);
			}
			bw.newLine();
			bw.write("{");
			bw.newLine();
		}
		else if(first.equals(IF) || first.equals(WHILE) || first.equals(ELSE + ":")) // Same syntax for "if" and
																			  //"while" statements.
		{
			this.ifWhileStatement(str);
			bw.newLine();
		}
		else if(first.equals(FOR))
		{
			this.forLoop(str);
			bw.newLine();
		}
		else
		{
			this.body(str);
			bw.newLine();
		}
	}

	/**
	 * constructor takes in a string representing a constructor method in Python and translates it into either a
	 * Java or Javascript constructor.
	 * @param str the syntax that will be translated.
	 * @throws IOException to permit the use of the private BufferedWriter bw.
	 */
	public void constructor(String str) throws IOException
	{
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String parameters = "";
		String parameter = st.nextToken(); //"def"
		parameter = st.nextToken(); //"__init__"
		do
		{
			parameter = st.nextToken();
			if(parameter.equals(COMMENT))
			{
				isComment = true;
				break;
			}
			else if(!(parameter.indexOf("self") >= 0))
			{
				if(toLang.equals("Java"))
				{
					parameters+= " int " + parameter; //parameters are assumed to be integers.
				}
				else
				{
					parameters+= " " + parameter;
				}
			}
		}
		while(st.hasMoreTokens());
		parameters = parameters.replace(")", "").replace(":", "");
		if(toLang.equals("Java"))
		{
			StringTokenizer priv = new StringTokenizer(parameters);
			ArrayList<String> privates = new ArrayList<String>();
			String param;
			do
			{
				param = priv.nextToken().replace(",", "");
				privates.add(param);
			}
			while(priv.hasMoreTokens());
			for(int x = 0; x < privates.size(); x++)
			{
				String result = privates.get(x);
				if(!(result.equals("int")))
				{
					bw.write("private int p" + privates.get(x) + ";"); //writing private variables
					bw.newLine();
				}
			}
			bw.write("public " + className + " (" + parameters + ")");
		}
		else
		{
			bw.write("function " + className + " (" + parameters + ")");
		}
		if(isComment)
		{
			String comment = "";
			while(st.hasMoreTokens())
			{
				comment+= " " + st.nextToken();
			}
			this.lineComment(comment);
		}
	}

	/**
	 * method takes in a regular Python method header and converts it to the appropriate language: Java or
	 * Javascript. For Java methods, they shall be considered void regardless of present return statements in
	 * the respective method's body.
	 * @param str the string encompassing the Python method header.
	 * @throws IOException permits the use of the BufferedWriter.
	 */
	public void method(String str) throws IOException
	{
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String name = st.nextToken(); //"def"
		name = st.nextToken();
		String parameters = "";
		String parameter;
		do
		{
			parameter = st.nextToken();
			if(parameter.equals(COMMENT))
			{
				isComment = true;
				break;
			}
			else if(!(parameter.indexOf("self") >= 0))
			{
				if(toLang.equals("Java"))
				{
					parameters+= " int " + parameter; //all parameters are assumed to be integers.
				}
				else
				{
					parameters+= " " + parameter;
				}
			}
		}
		while(st.hasMoreTokens());
		parameters = parameters.replace(":", "");
		if(toLang.equals("Java"))
		{
			bw.write("public void " + name + "(" + parameters);
		}			//methods are void by default.
		else
		{
			bw.write("this." + name + " = function (" + parameters);
		}
		if(isComment)
		{
			String comment = "";
			String nextField;
			do
			{
				nextField = st.nextToken();
				comment+= " " + nextField;
			}
			while(st.hasMoreTokens());
			this.lineComment(comment);
		}
	}

	/**
	 * body converts Python syntax into Java or Javascript syntax for code such as print statements and return
	 * statements. If it is undetermined what the syntax is, it will be written in its current form.
	 * @param str the string containing the Python code.
	 * @throws IOException in order be granted use of the private BufferedWriter bw.
	 */
	public void body(String str) throws IOException
	{
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if(first.equals("print") || first.equals("return"))
		{
			String rest = "";
			String next;
			while(st.hasMoreTokens())
			{
				next = st.nextToken();
				if(next.equals(COMMENT))
				{
					isComment = true;
					break;
				}
				rest+= " " + next;
			}
			if(first.equals("print")) //print line
			{
				if(toLang.equals("Java"))
				{
					bw.write("System.out.println" + rest + ";");
				}
				else
				{
					bw.write("console.log" + rest + ";");
				}
			}
			else //return statement
			{
					bw.write(first + rest + ";");
			}
		}
		else if(str.indexOf("self.") >= 0 || str.indexOf("=") >= 0) //variable declaration
		{
			first = first.replace("self.", "");
			String equals = st.nextToken(); //"="
			String value = st.nextToken();
			if(value.equals(INPUT)) //input statement
			{
				String rest = "";
				String next;
				while(st.hasMoreTokens())
				{
					next = st.nextToken();
					if(next.equals(COMMENT))
					{
						isComment = true;
						break;
					}
					rest+= " " + next;
				}
				rest = rest.replace("(", "").replace(")", "");
				if(toLang.equals("Java"))
				{
					bw.write("String " + first + " = " + rest + ";");
					bw.newLine();
					bw.write("String answer = input.readLine()");
				}
				else
				{
					bw.write(first + " = prompt(" + rest + ");");
				}
			}
			else
			{
				if(toLang.equals("Java"))
				{
					bw.write("p" + first + " " + equals + " " + value + ";");
				}
				else
				{
					bw.write("this." + first + " " + equals + " " + value + ";");
				}
			}
		}
		else
		{
			String rest = "";
			String next = st.nextToken();
			while(st.hasMoreTokens())
			{
				if(next.equals(COMMENT))
				{
					isComment = true;
					break;
				}
				rest+= next;
				next = st.nextToken();
			}
			if(toLang.equals("Java"))
			{
				bw.write(rest);
			}
		}
		try
		{
			if(isComment || st.nextToken().equals(COMMENT))
			{
				String comment = "";
				String next;
				do
				{
					next = st.nextToken();
					if(!(next.equals(COMMENT)))
					{
						comment+= " " + next;
					}
				}
				while(st.hasMoreTokens());
				this.lineComment(comment);
			}
		}
		catch(NoSuchElementException e)
		{
			//no comment
		}
	}

	/**
	 * the forLoop method changes conventional and for-each loops in Python to corresponding Java or Javascript
	 * for loops. Three kinds of Python for loops can be translated: for-in loops for iterative data, for-in
	 * loops where a range is involved, and for-in loops where a range and step not 1 is involved.
	 * @param str the string containing the entire for loop declaration.
	 * @throws IOException to yield a functioning BufferedWriter.
	 */
	public void forLoop(String str) throws IOException
	{
		StringTokenizer st = new StringTokenizer(str);
		try
		{
			String tFor = st.nextToken(); //"for"
			String name = st.nextToken(); //"number"
			String determine = st.nextToken(); //"in"
			determine = st.nextToken(); //"range" or variable
			if(!(determine.equals("range"))) //check for a for-each loop.
			{ //for letter in word:
				String object = st.nextToken();
				object = object.replace(":", "");
				if(toLang.equals("Java"))
				{
					bw.write(tFor + " (int " + name + " : " + object + ")");
				}
				else
				{
					bw.write(tFor + " (var " + name + " in " + object + ")");
				}
			}
			else //regular for loop.
			{ //for number in range (0, 5):
				String start = st.nextToken().replace("(", "").replace(",", ""); //starting integer
				String stop = st.nextToken(); //ending integer
				if(stop.indexOf(",") >= 0) //two or three numbers in the range method
				{ //three numbers
					String step = st.nextToken().replace(")", "").replace(":", "");
					stop = stop.replace(",", "");
					if(toLang.equals("Java"))
					{
						bw.write(tFor + " (int " + name + " = " + start + "; ");
					}
					else
					{
						bw.write(tFor + " (var " + name + " = " + start + "; ");
					}
					if(Integer.parseInt(start) > Integer.parseInt(stop))
					{
						bw.write(name + " > " + stop + "; " + name + " = " + name + " - " + step + ")");
					}
					else
					{
						bw.write(name + " < " + stop + "; " + name + " = " + name + " + " + step + ")");
					}
				}
				else
				{ //two numbers
					if(Integer.parseInt(start) > Integer.parseInt(stop))
					{
						bw.write(name + " > " + stop + "; " + name + "--)");
					}
					else
					{
						bw.write(name + " < " + stop + "; " + name + "++)");
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
				while(st.hasMoreTokens())
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
	 * ifWhileStatement allows for the translation of either an if statement or while loop from Python to
	 * Java or Javascript. The similar nature of the code of both of these blocks of code allow for the
	 * combination of the translation of both of them.
	 * @param str the string encompassing the if statement or while loop.
	 * @throws IOException in order to allow use of the private BufferedWriter.
	 */
	public void ifWhileStatement(String str) throws IOException
	{
		boolean isComment = false;
		StringTokenizer st = new StringTokenizer(str);
		String first = st.nextToken();
		if(first.equals(ELSE + ":"))
		{
			bw.write("}");
			bw.newLine();
			bw.write(ELSE);
		}
		else
		{
			bw.write(first + "("); //"if" or "while"
			String syntax;
			do
			{
				syntax = st.nextToken().replace("(", "");
				if(syntax.equals(COMMENT))
				{
					isComment = true;
					break;
				}
				else if(syntax.equals("or"))
				{
					bw.write(" || ");
				}
				else if(syntax.equals("and"))
				{
					bw.write(" && ");
				}
				else if(syntax.equals("not"))
				{
					bw.write(" !");
				}
				else
				{
					bw.write(" " + syntax.replace(":", "")); //Write whatever is being compared or inequalities.
				}
			}
			while(st.hasMoreTokens());
			bw.write(")");
		}
		if(isComment)
		{
			String comment = "";
			while(st.hasMoreTokens())
			{
				comment+= " " + st.nextToken();
			}
			this.lineComment(comment);
		}
		bw.newLine();
		bw.write("{");
	}

	/**
	 * lineComment is called at the end of every method to see if there is a comment after any amount
	 * of syntax. Because of this, it must be in a "try-catch" block or the use of a boolean variable present
	 * in every method as there might not be a comment.
	 * @param str the syntax which will be converted into a comment.
	 * @throws IOException in order to use the BufferedWriter.
	 */
	public void lineComment(String str) throws IOException
	{
		bw.write(" //" + str.replace(COMMENT, ""));
	}

	/**
	 * longComment checks for JavaDoc comments in the file and converts them into the appropriate
	 * form for either language it will translate into.
	 * @param str the syntax which will be translated into a longer comment.
	 * @throws IOException to be allowed access to the private BufferedWriter bw.
	 */
	public void longComment(String str) throws IOException
	{
		bw.write(str);
	}
}
