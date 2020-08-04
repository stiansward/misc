package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;

public class AspExprStmt extends AspStmt {
	AspExpr ae;

	AspExprStmt(int n) {
		super(n);
	}
	
	public static AspExprStmt parse(Scanner s) {
		enterParser("expr stmt");

		AspExprStmt aes = new AspExprStmt(s.curLineNum());
		aes.ae = AspExpr.parse(s);

		leaveParser("expr stmt");
		return aes;
	}

	@Override
	public void prettyPrint() {
		ae.prettyPrint();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return ae.eval(curScope);
	}
}
