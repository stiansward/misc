package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspProgram extends AspSyntax {
	ArrayList<AspStmt> stmts = new ArrayList<>();

	AspProgram(int n) {
		super(n);
	}

	public static AspProgram parse(Scanner s) {
		enterParser("program");

		AspProgram ap = new AspProgram(s.curLineNum());
		while (s.curToken().kind != eofToken) {
			ap.stmts.add(AspStmt.parse(s));
			while (s.curToken().kind == dedentToken || s.curToken().kind == newLineToken) {
				s.readNextToken();
			}
		}

		leaveParser("program");
		return ap;
	}

	@Override
	public void prettyPrint() {
		for (AspStmt stmt : stmts) {
			stmt.prettyPrint();
			Main.log.prettyWriteLn();
		}
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		for (AspStmt stmt : stmts) {
			stmt.eval(curScope);
		}
		return null;
	}
}
