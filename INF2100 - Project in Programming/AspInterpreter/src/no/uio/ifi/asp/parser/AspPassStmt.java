package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspPassStmt extends AspStmt {
	Token token;

	AspPassStmt(int n) {
		super(n);
	}
	
	public static AspPassStmt parse(Scanner s) {
		enterParser("pass stmt");

		AspPassStmt pass = new AspPassStmt(s.curLineNum());
		pass.token = s.curToken();
		skip(s, passToken);

		leaveParser("pass stmt");
		return pass;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWriteLn(token.name);
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) {
		trace("pass:" + this.toString().substring(22));
		return new RuntimeNoneValue();
	}
}
