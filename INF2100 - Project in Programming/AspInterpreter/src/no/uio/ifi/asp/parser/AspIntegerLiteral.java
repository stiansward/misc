package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;

public class AspIntegerLiteral extends AspAtom {
	Token intLit;

	public AspIntegerLiteral(int n, Token intLit) {
		super(n);
		this.intLit = intLit;
	}

	public static AspIntegerLiteral parse(Scanner s){
		enterParser("integer literal");

		AspIntegerLiteral ail = new AspIntegerLiteral(s.curLineNum(), s.curToken());
		s.readNextToken();

		leaveParser("integer literal");
		return ail;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite("" + intLit.integerLit);
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeIntValue(intLit.integerLit);
	}
}
