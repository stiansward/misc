package no.uio.ifi.asp.scanner;

import java.io.*;
import java.util.*;

import no.uio.ifi.asp.main.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class Scanner {
	private LineNumberReader sourceFile = null;
	private String curFileName;
	private ArrayList<Token> curLineTokens = new ArrayList<>();
	private int indents[] = new int[100];
	private int numIndents = 0;
	private final int tabDist = 4;


	public Scanner(String fileName) {
		curFileName = fileName;
		indents[0] = 0;  numIndents = 1;

		try {
			sourceFile = new LineNumberReader(
					new InputStreamReader(
					new FileInputStream(curFileName),
					"UTF-8"));
		} catch (IOException e) {
			scannerError("Cannot read " + curFileName + "!");
		}
	}


	private void scannerError(String message) {
		String m = "Asp scanner error";
		if (curLineNum() > 0)
			m += " on line " + curLineNum();
		m += ": " + message;

		Main.error(m);
	}


	public Token curToken() {
		while (curLineTokens.isEmpty()) {
			readNextLine();
		}
		return curLineTokens.get(0);
	}


	public void readNextToken() {
		if (! curLineTokens.isEmpty())
				curLineTokens.remove(0);
	}


	public boolean anyEqualToken() {
			for (Token t: curLineTokens) {
				if (t.kind == equalToken) return true;
			}
		return false;
	}


	/**
	 * Translates the next line in the file to tokens
	 * <p>
	 * Reads one line from sourceFile, separates into tokens,
	 * and adds the tokens to curLineTokens
	 */
	private void readNextLine() {
		curLineTokens.clear();
		// Read the next line:
		String line = null;
		try {
			line = sourceFile.readLine();
			if (line == null) {
				sourceFile.close();
				sourceFile = null;
			} else {
				Main.log.noteSourceLine(curLineNum(), line);
			}
		} catch (IOException e) {
			sourceFile = null;
			scannerError("Unspecified I/O error!");
		}

		if (line == null) {
			for( ; --numIndents > 0; ) {
				curLineTokens.add(new Token(dedentToken, curLineNum()));
			}
			curLineTokens.add(new Token(eofToken, curLineNum()));
		}
		else {
//			System.out.println(line);
			// Skip line if blank or only comment
			int i = 0;
			char c;
			if(line.length() == 0) return;
			while (i < line.length()) {
				c = line.charAt(i++);
				if (c == '#' || i == line.length()) return;
				if (!(c == ' ' || c == '\t')) break;
			}
			
			// Find indents and create tokens accordingly
			line = expandLeadingTabs(line);
			int indent = findIndent(line);
			if (indent > indents[numIndents]) {
				curLineTokens.add(new Token(indentToken,curLineNum()));
				indents[++numIndents] = indent;
			}
			else {
				while(indent < indents[numIndents]) {
					curLineTokens.add(new Token(dedentToken,curLineNum()));
					numIndents--;
				}
				if(indent > indents[numIndents]) {
					scannerError("Indentation error!");
				}
			}
			
			// Separates the next token into one of the three main types,
			// and delegates to support-methods.
			// Support methods return the length of the tokens they create,
			// and this loop skips accordingly
			for(i = indent; i < line.length();) {
				// Skip blank characters that are not part of String literals
				if(line.charAt(i) == ' ' || line.charAt(i) == '\t') i++;
				
				// The character '.' is illegal outside of String literals and float numbers
				// Furthermore, floats must have at least one number before the '.',
				// so any '.' read by this loop is invalid
				else if(line.charAt(i) == '.') scannerError("Invalid use of '.'");
				
				// If comment, skip rest of line
				else if(line.charAt(i) == '#') break;
				
				// Variable names and keywords
				else if(isLetterAZ(line.charAt(i))){
					i = nameToken(line, i);
				}
				// String literals
				else if(line.charAt(i) == '\'' || line.charAt(i) == '"') {
					i = stringToken(line, i, line.charAt(i));
				}
				
				// Numbers
				else if(isDigit(line.charAt(i))) {
					i = valueToken(line, i);
				}
				
				// Operators
				else {
					i = operatorToken(line, i);
				}
			}

			// Terminate line:
			curLineTokens.add(new Token(newLineToken,curLineNum()));
		}

		for (Token t: curLineTokens) Main.log.noteToken(t);
	}

	/**
	 * Adds one token starting with a letter to curLineTokens, and returns the size of the token.
	 * <p>
	 * This method takes a String representing the current line, and starting from the given index,
	 * adds the appropriate token to the curLineTokens list. It returns the length of the token.
	 * 
	 * @param line			The current line from the file
	 * @param startIndex	The place in the line to start from
	 * @param symbol		The string literal symbol (' or ")
	 * @return				Index of next char in String Line
	 */
	private int stringToken(String line, int startIndex, char symbol) {
		int index = startIndex + 1;
		Token token;
		if(line.charAt(startIndex) == symbol) {
			while(line.charAt(index) != symbol) {
				if(index == line.length() - 1) {	// If there's a NEWLINE in the String 
					scannerError("String literal not terminated!");
				}
				index++;
			}
			token = new Token(stringToken, curLineNum());
			token.stringLit = line.substring(startIndex + 1, index++);
			curLineTokens.add(token);
		}
		return index;
	}

	/**
	 * Make a nameToken, convert it to it's respected TokenKind, and add it to 
	 * curLineTokens. Returns index of last char used.
	 * <p>
	 * This method takes a String representing the current line, and starting from the given index,
	 * adds the appropriate token to the curLineTokens list. It returns the length of the token.
	 * 
	 * @param line			The current line from the file
	 * @param startIndex	The place in the line to start from
	 * @return				Index of next char in String Line
	 */
	private int nameToken(String line, int startIndex){
		int i = startIndex;
		Token token;
		while(i < line.length() && (isLetterAZ(line.charAt(i)) || isDigit(line.charAt(i)))){
			i++;
		}
		token = new Token(nameToken,curLineNum());
		token.name = line.substring(startIndex, i);
		token.checkResWords();
		curLineTokens.add(token);
		return i;
	}

	/**
	 * Adds one token starting with a number to curLineTokens, and returns the size of the token.
	 * <p>
	 * This method takes a String representing the current line, and starting from the given index,
	 * adds the appropriate token to the curLineTokens list. It returns the length of the token.
	 * 
	 * @param line			The current line from the file
	 * @param startIndex	The place in the line to start from
	 * @return				Index of next char in String Line
	 */
	private int valueToken(String line, int startIndex){
		int index = startIndex + 1;
		Token token;

		// Special case: Number has leading zeroes
		// Treat each leading zero as separate number with value 0
		while(index < line.length() && isDigit(line.charAt(index)) && line.charAt(startIndex) == '0') {
			token = new Token(integerToken, curLineNum());
			token.integerLit = 0;
			curLineTokens.add(token);
			startIndex++;
			index++;
		}

		// Scan through until NEWLINE or non-digit char
		while(index < line.length() && isDigit(line.charAt(index))) {
			index++;
		}

		// If current char is '.', handle as float, else handle as int
		if(index < line.length() && line.charAt(index) == '.') {
			index++;
			if(index >= line.length() || !isDigit(line.charAt(index))) {
				scannerError("Illegal float literal: " + line.substring(startIndex, index) + "!");
			}
			while(index < line.length() && isDigit(line.charAt(index))) {
				index++;
			}
			token = new Token(floatToken, curLineNum());
			token.floatLit = Double.parseDouble(line.substring(startIndex, index));
		} else {
			token = new Token(integerToken, curLineNum());
			token.integerLit = Integer.parseInt(line.substring(startIndex, index));
		}

		curLineTokens.add(token);
		return index;
	}

	/**
	 * Adds one token starting with an operator character to curLineTokens, and returns the size of the token.
	 * <p>
	 * This method takes a String representing the current line, and starting from the given index,
	 * adds the appropriate token to the curLineTokens list. It returns the length of the token.
	 * 
	 * @param line			The current line from the file
	 * @param index			The place in the line to start from
	 * @return				Index of next char in String Line
	 */
	private int operatorToken(String line, int index) {
		String operator = line.substring(index, index + 1);
		if(index < line.length() - 1) {
			switch(line.substring(index, index + 2)) {
			case "==":
			case "//":
			case ">=":
			case "<=":
			case "!=":
				operator = line.substring(index, index + 2);
			}
		}

		Token token = new Token(nameToken, curLineNum());
		token.name = operator;
		token.checkOperator();
		curLineTokens.add(token);
		return operator.length() + index;
	}


	public int curLineNum() {
		return sourceFile!=null ? sourceFile.getLineNumber() : 0;
	}

	private int findIndent(String s) {
		int indent = 0;

		while (indent<s.length() && s.charAt(indent)==' ') indent++;
		return indent;
	}

	private String expandLeadingTabs(String s) {
		String newS = "";
		for (int i = 0;  i < s.length();  i++) {
			char c = s.charAt(i);
			if (c == '\t') {
			do {
				newS += " ";
			} while (newS.length()%tabDist != 0);
			} else if (c == ' ') {
			newS += " ";
			} else {
			newS += s.substring(i);
			break;
			}
		}
		return newS;
	}


	private boolean isLetterAZ(char c) {
		return ('A'<=c && c<='Z') || ('a'<=c && c<='z') || (c=='_');
	}


	private boolean isDigit(char c) {
		return '0'<=c && c<='9';
	}


	public boolean isCompOpr() {
		TokenKind k = curToken().kind;
		return (k == lessToken ||
				k == greaterToken ||
				k == doubleEqualToken ||
				k == greaterEqualToken ||
				k == lessEqualToken ||
				k == notEqualToken);
	}


	public boolean isFactorPrefix() {
		TokenKind k = curToken().kind;
		return (k == plusToken || k == minusToken);
	}


	public boolean isFactorOpr() {
		TokenKind k = curToken().kind;
		return k == astToken ||
				k == slashToken ||
				k == percentToken ||
				k == doubleSlashToken;
	}


	public boolean isTermOpr() {
		TokenKind k = curToken().kind;
		return (k == plusToken || k == minusToken);
	}
}
