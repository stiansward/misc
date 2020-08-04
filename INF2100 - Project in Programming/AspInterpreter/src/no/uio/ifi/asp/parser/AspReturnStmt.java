package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspReturnStmt extends AspStmt {
	AspExpr expr;

	AspReturnStmt(int n) {
		super(n);
	}
	
	public static AspReturnStmt parse(Scanner s) {
		enterParser("return stmt");

		AspReturnStmt ret = new AspReturnStmt(s.curLineNum());
		skip(s, returnToken);
		ret.expr = AspExpr.parse(s);

		leaveParser("return stmt");
		return ret;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("return ");
		expr.prettyPrint();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeReturnValue res = new RuntimeReturnValue(expr.eval(curScope), this.lineNum);
		trace("return " + res.getValue().showInfo());
		throw res;
	}
}
