package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;

public abstract class AspStmt extends AspSyntax {

	AspStmt(int n) {
		super(n);
	}
	
	public static AspStmt parse(Scanner s) {
		enterParser("stmt");

		AspStmt as = null;
		if (s.anyEqualToken()) {
			as = AspAssignStmt.parse(s);
		} else {
			switch (s.curToken().kind) {
			case forToken:
				as = AspForStmt.parse(s);
				break;
			case ifToken:
				as = AspIfStmt.parse(s);
				break;
			case whileToken:
				as = AspWhileStmt.parse(s);
				break;
			case returnToken:
				as = AspReturnStmt.parse(s);
				break;
			case passToken:
				as = AspPassStmt.parse(s);
				break;
			case defToken:
				as = AspFuncDef.parse(s);
				break;
			default:
				as = AspExprStmt.parse(s);
				break;
			}
		}
		leaveParser("stmt");
		return as;
	}

	@Override
	public abstract void prettyPrint();

	@Override
	public abstract RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue;
}
