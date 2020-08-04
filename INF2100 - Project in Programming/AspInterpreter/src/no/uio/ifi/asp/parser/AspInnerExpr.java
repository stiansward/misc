package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspInnerExpr extends AspAtom {
	AspExpr expr;

	public AspInnerExpr(int n) {
		super(n);
	}

	public static AspInnerExpr parse(Scanner s){
		enterParser("inner expr");

		skip(s, leftParToken);
		AspInnerExpr aie = new AspInnerExpr(s.curLineNum());
		aie.expr = AspExpr.parse(s);
		skip(s, rightParToken);

		leaveParser("inner expr");
		return aie;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite("(");
		expr.prettyPrint();
		Main.log.prettyWrite(")");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return expr.eval(curScope);
	}
}
