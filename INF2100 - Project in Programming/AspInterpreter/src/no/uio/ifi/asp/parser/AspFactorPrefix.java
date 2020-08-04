package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;

public class AspFactorPrefix extends AspSyntax {
	Token operator;

	AspFactorPrefix(int n) {
		super(n);
	}
	
	public static AspFactorPrefix parse(Scanner s) {
		enterParser("factor prefix");

		AspFactorPrefix prefix = new AspFactorPrefix(s.curLineNum());
		prefix.operator = s.curToken();
		s.readNextToken();

		leaveParser("factor prefix");
		return prefix;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite(operator.name + " ");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeNoneValue();
	}
}
