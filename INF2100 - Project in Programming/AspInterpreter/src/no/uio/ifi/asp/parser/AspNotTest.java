package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspNotTest extends AspSyntax {
	boolean not;
	AspComp comp;

	AspNotTest(int n) {
		super(n);
		not = false;
	}
	
	public static AspNotTest parse(Scanner s) {
		enterParser("not test");

		AspNotTest ant = new AspNotTest(s.curLineNum());
		if (s.curToken().kind == notToken) {
			ant.not = true;
			s.readNextToken();
		}
		ant.comp = AspComp.parse(s);

		leaveParser("not test");
		return ant;
	}

	@Override
	void prettyPrint() {
		if (not) Main.log.prettyWrite("not ");
		comp.prettyPrint();
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = comp.eval(curScope);
		if (not) {
			v = v.evalNot(this);
		}
		if (v == null) return new RuntimeNoneValue();
		return v;
	}
}
