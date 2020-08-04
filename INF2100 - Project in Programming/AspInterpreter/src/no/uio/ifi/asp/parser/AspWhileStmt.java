package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;

import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspWhileStmt extends AspStmt {
	AspExpr test;
	AspSuite body;
	
	AspWhileStmt(int n) {
		super(n);
	}
	
	public static AspWhileStmt parse(Scanner s) {
		enterParser("while stmt");

		AspWhileStmt aws = new AspWhileStmt(s.curLineNum());
		skip(s, whileToken);
		aws.test = AspExpr.parse(s);
		skip(s, colonToken);
		aws.body = AspSuite.parse(s);

		leaveParser("while stmt");
		return aws;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("while ");
		test.prettyPrint();
		Main.log.prettyWrite(":");
		body.prettyPrint();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		while (true) {
			RuntimeValue t = test.eval(curScope);
			if (! t.getBoolValue("while loop test", this)) break;
			trace("while True: ...");
			body.eval(curScope);
		}
		trace("while False:");
		return new RuntimeNoneValue();
	}
}
