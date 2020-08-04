package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;
import java.util.ArrayList;

public class AspSuite extends AspSyntax {
	ArrayList<AspStmt> stmts;
	public AspSuite(int n) {
		super(n);
		stmts = new ArrayList<AspStmt>();
	}

	public static AspSuite parse(Scanner s){
		enterParser("suite");

		AspSuite suite = new AspSuite(s.curLineNum());
		skip(s, newLineToken);
		skip(s, indentToken);
		while(s.curToken().kind != dedentToken){
			suite.stmts.add(AspStmt.parse(s));
			while (s.curToken().kind == newLineToken) {
				s.readNextToken();
			}
		}
		skip(s, dedentToken);

		leaveParser("suite");
		return suite;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWriteLn();
		Main.log.prettyIndent();
		for (int i = 0; i < stmts.size(); i++) {
			stmts.get(i).prettyPrint();
			if (i < stmts.size() - 1) {
				Main.log.prettyWriteLn();
			}
		}
		Main.log.prettyDedent();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue ret = null;
		for (AspStmt stmt : stmts) {
			ret = stmt.eval(curScope);
		}
		return ret;
	}
}
