package com.xuef.scm;

import java.io.*;

/**
 * InputPort is to Scheme as InputStream is to Java.
 * ����Ķ�����Ľӿ��� read()����, ���������ַ����ṹ��Ϊ Pair, Ȼ�󷵻�֮(�������Ϸ��Ļ�)��
 * read()������������Ϊʵ���������ӹ�����IO�ܵ� in, �����Ĳ����� ʵ������, ����java�кܳ�����
 * 
 * TODO
 * ��׼I/O����֧�֣��ڲ����κ��趨��ǰ���£��������ӱ�׼��������ȡ�ַ������ǣ���Ϊ��������
 * Ӧ�þ߱����κ���������ȡ�ַ�����������Ҫ����һ�������ӿ���ʵ�ִ�ĳ����������ȡ�ַ������﷨������
 * @author Peter Norvig, peter@norvig.com http://www.norvig.com Copyright 1998
 *         Peter Norvig, see http://www.norvig.com/license.html
 */

public class InputPort extends SchemeUtils {
	static String EOF = "#!EOF";
	boolean isPushedToken = false;
	boolean isPushedChar = false;
	Object pushedToken = null;
	int pushedChar = -1;
	
	// ͨ���� in IO�ܵ���ȡԭʼ�ַ�������(���������ַ�����ʽ)
	Reader in;
	// buff ��Ϊ�м��ַ�������Ļ�����ʹ��
	StringBuffer buff = new StringBuffer();

	public InputPort(InputStream in) {
		this.in = new InputStreamReader(in);
	}

	public InputPort(Reader in) {
		this.in = in;
	}

	/** 
	 * Read and return a Scheme character or EOF. 
	 */
	public Object readChar() {
		try {
			if (isPushedChar) {
				isPushedChar = false;
				if (pushedChar == -1)
					return EOF;
				else
					return chr((char) pushedChar);
			} else {
				int ch = in.read();
				if (ch == -1)
					return EOF;
				else
					return chr((char) ch);
			}
		} catch (IOException e) {
			warn("On input, exception: " + e);
			return EOF;
		}
	}

	/**
	 * Peek at and return the next Scheme character (or EOF). However, don't
	 * consume the character.
	 */
	public Object peekChar() {
		int p = peekCh();
		if (p == -1)
			return EOF;
		else
			return chr((char) p);
	}

	/** Push a character back to be re-used later. **/
	int pushChar(int ch) {
		isPushedChar = true;
		return pushedChar = ch;
	}

	/** Pop off the previously pushed character. **/
	int popChar() {
		isPushedChar = false;
		return pushedChar;
	}

	/**
	 * Peek at and return the next Scheme character as an int, -1 for EOF.
	 * However, don't consume the character.
	 **/
	public int peekCh() {
		try {
			return isPushedChar ? pushedChar : pushChar(in.read());
		} catch (IOException e) {
			warn("On input, exception: " + e);
			return -1;
		}
	}

	/** 
	 * Read and return a Scheme expression, or EOF. 
	 * ������ʽ�Ϸ������� Pair ��ʽ����
	 * read() && readTail(); 
	 * 		Together, these mutually recursive functions parse Scheme tokens into our interpreter's 
	 * 		internal Java representation of Scheme expressions.
	 * read should be called when a complete Scheme expression needs to be extracted from src. 
	 * It will remove enough tokens to form one expression and return that expression in the correct internal representation.
	 * 
	 */
	public Object read() {
		try {
			Object token = nextToken();
			/**
			 * If the token is the string "nil", return the nil object.
			 * If the token is (, recursively call read_tail and return its result.
			 * If the next token is not a delimiter, then it must be self-evaluating. Return it. 
			 * If none of the above cases apply, raise an error.
			 */
			// If the token is (, recursively call read_tail and return its result.
			if (token == "(")
				return readTail(false);
			else if (token == ")") {
				warn("Extra ) ignored.");
				return read();
			// a delimiter
			} else if (token == ".") {
				warn("Extra . ignored.");
				return read();
			} else if (token == "'")
				return list("quote", read());
			else if (token == "`")
				return list("quasiquote", read());
			else if (token == ",")
				return list("unquote", read());
			else if (token == ",@")
				return list("unquote-splicing", read());
			// If the next token is not a delimiter, then it must be self-evaluating. Return it.
			else return token;
		} catch (IOException e) {
			warn("On input, exception: " + e);
			return EOF;
		}
	}
	
	/**
	 * readTail expects to read the rest of a list or dotted list, assuming the open parenthesis of 
	 * that list has already been removed by read. It will read expressions (and thus remove tokens) 
	 * until the matching closing parenthesis ) is seen. This list of expressions is returned in the correct 
	 * internal representation (i.e. instances of the Pair class).
	 * 
	 * If there are no more tokens, raise an error.
	 * If the token is ), return the nil object.
	 * If the token is ., it is a dotted list. Implement this in Problem 2.
	 * If none of the above cases apply, the src is at the beginning of an expression. Then:
	 * 	Read the next expression (Hint: Which function do we use to read an expression?)
	 * 	Recursively read the rest of the original expression until the matching closing parenthesis.
	 * 	Return the results as a Pair instance.
	 * @param dotOK
	 * @return
	 * @throws IOException
	 */
	Object readTail(boolean dotOK) throws IOException {
		Object token = nextToken();
		if (token == EOF)
			return error("EOF during read.");
		else if (token == ")")
			return null;
		// support for dotted lists
		else if (token == ".") { 
			// (1 2 . 3) should be converted to Pair(1, Pair(2, 3))
			// (1 . 2)  (1 . (2 . 3))
			// A dotted list must have exactly one item after the dot; 
			// anything else is a syntax error.
			Object result = read(); // one item after the dot -- .
			token = nextToken();
			if (token != ")")
				warn("Where's the ')'? Got " + token + " after .");
			return result;
		} else {
			isPushedToken = true;
			pushedToken = token;
			return cons(read(), readTail(true));
		}
	}

	/** Close the port. Return TRUE if ok. **/
	public Object close() {
		try {
			this.in.close();
			return TRUE;
		} catch (IOException e) {
			return error("IOException: " + e);
		}
	}

	/** Is the argument the EOF object? **/
	public static boolean isEOF(Object x) {
		return x == EOF;
	}

	/**
	 * ͨ���˷����������ַ���
	 * @return
	 * @throws IOException
	 */
	Object nextToken() throws IOException {
		int ch;

		// See if we should re-use a pushed char or token
		if (isPushedToken) {
			isPushedToken = false;
			return pushedToken;
		} else if (isPushedChar) {
			ch = popChar();
		} else {
			ch = in.read();
		}

		// Skip whitespace
		while (Character.isWhitespace((char) ch))
			ch = in.read();

		// See what kind of non-white character we got
		switch (ch) {
		case -1:
			return EOF;
		case '(':
			return "(";
		case ')':
			return ")";
		case '\'':
			return "'";
		case '`':
			return "`";
		case ',':
			ch = in.read();
			if (ch == '@')
				return ",@";
			else {
				pushChar(ch);
				return ",";
			}
		case ';':
			// Comment: skip to end of line and then read next token
			while (ch != -1 && ch != '\n' && ch != '\r')
				ch = in.read();
			return nextToken();
		case '"':
			// Strings are represented as char[]
			buff.setLength(0);
			while ((ch = in.read()) != '"' && ch != -1) {
				buff.append((char) ((ch == '\\') ? in.read() : ch));
			}
			if (ch == -1)
				warn("EOF inside of a string.");
			return buff.toString().toCharArray();
		case '#':
			switch (ch = in.read()) {
			case 't':
			case 'T':
				return TRUE;
			case 'f':
			case 'F':
				return FALSE;
			case '(':
				pushChar('(');
				return listToVector(read());
			case '\\':
				ch = in.read();
				if (ch == 's' || ch == 'S' || ch == 'n' || ch == 'N') {
					pushChar(ch);
					Object token = nextToken();
					if (token == "space")
						return chr(' ');
					else if (token == "newline")
						return chr('\n');
					else {
						isPushedToken = true;
						pushedToken = token;
						return chr((char) ch);
					}
				} else {
					return chr((char) ch);
				}
			case 'e':
			case 'i':
			case 'd':
				return nextToken();
			case 'b':
			case 'o':
			case 'x':
				warn("#" + ((char) ch) + " not implemented, ignored.");
				return nextToken();
			default:
				warn("#" + ((char) ch) + " not recognized, ignored.");
				return nextToken();
			}
		default:
			buff.setLength(0);
			int c = ch;
			do {
				buff.append((char) ch);
				ch = in.read();
			} while (!Character.isWhitespace((char) ch) && ch != -1
					&& ch != '(' && ch != ')' && ch != '\'' && ch != ';'
					&& ch != '"' && ch != ',' && ch != '`');
			pushChar(ch);
			// Try potential numbers, but catch any format errors.
			if (c == '.' || c == '+' || c == '-' || (c >= '0' && c <= '9')) {
				try {
					return new Double(buff.toString());
				} catch (NumberFormatException e) {
					;
				}
			}
			return buff.toString().toLowerCase().intern();
		}
	}
}
