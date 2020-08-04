package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspForStmt extends AspStmt {
	AspName name;
	AspExpr expr;
	AspSuite suite;

	AspForStmt(int n) {
		super(n);
	}
	
	public static AspForStmt parse(Scanner s) {
		enterParser("for stmt");

		AspForStmt stmt = new AspForStmt(s.curLineNum());
		skip(s, forToken);
		stmt.name = AspName.parse(s);
		skip(s, inToken);
		stmt.expr = AspExpr.parse(s);
		skip(s, colonToken);
		stmt.suite = AspSuite.parse(s);

		leaveParser("for stmt");
		return stmt;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("for ");
		name.prettyPrint();
		Main.log.prettyWrite(" in ");
		expr.prettyPrint();
		Main.log.prettyWrite(":");
		suite.prettyPrint();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeListValue list = (RuntimeListValue)expr.eval(curScope);
		for (int i = 0; i < list.size(); i++) {
			trace("for #"+(i+1)+": " + name.name.name + " = " + list.get(i).getStringValue("for statement", this));
			curScope.assign(name.name.name, list.get(i));
			suite.eval(curScope);
		}
		return new RuntimeNoneValue();
	}
}
