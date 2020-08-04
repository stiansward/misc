package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;

public class AspTermOpr extends AspSyntax {
	Token op;

	AspTermOpr(int n) {
		super(n);
	}
	
	public static AspTermOpr parse(Scanner s) {
		enterParser("term opr");

		AspTermOpr opr = new AspTermOpr(s.curLineNum());
		opr.op = s.curToken();
		s.readNextToken();

		leaveParser("term opr");
		return opr;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite(" " + op.name + " ");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeNoneValue();
	}
}
