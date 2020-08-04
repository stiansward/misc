package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;

public class AspNoneLiteral extends AspAtom {

	public AspNoneLiteral(int n) {
		super(n);
	}

	public static AspNoneLiteral parse(Scanner s){
		enterParser("none literal");

		AspNoneLiteral anl = new AspNoneLiteral(s.curLineNum());
		s.readNextToken();

		leaveParser("none literal");
		return anl;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite("None");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeNoneValue();
	}
}
