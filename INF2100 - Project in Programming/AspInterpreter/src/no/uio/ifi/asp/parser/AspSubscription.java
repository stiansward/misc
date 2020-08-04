package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspSubscription extends AspPrimarySuffix {
	AspExpr expr;

	AspSubscription(int n) {
		super(n);
	}
	
	public static AspSubscription parse(Scanner s) {
		enterParser("subscription");

		skip(s, leftBracketToken);
		AspSubscription sub = new AspSubscription(s.curLineNum());
		sub.expr = AspExpr.parse(s);
		skip(s, rightBracketToken);

		leaveParser("subscription");
		return sub;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("[");
		expr.prettyPrint();
		Main.log.prettyWrite("]");
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return expr.eval(curScope);
	}
}
