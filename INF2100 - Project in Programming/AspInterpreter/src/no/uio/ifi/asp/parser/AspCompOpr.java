package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;

public class AspCompOpr extends AspSyntax {
	TokenKind kind;

	AspCompOpr(int n) {
		super(n);
	}
	
	public static AspCompOpr parse(Scanner s) {
		enterParser("comp opr");

		AspCompOpr compOpr = new AspCompOpr(s.curLineNum());
		compOpr.kind = s.curToken().kind;
		s.readNextToken();

		leaveParser("comp opr");
		return compOpr;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite(" " + kind + " ");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeNoneValue();
	}
}
