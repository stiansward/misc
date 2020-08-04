package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;

public class AspFloatLiteral extends AspAtom{
	Token flt;

	public AspFloatLiteral(int n, Token flt) {
		super(n);
		this.flt = flt;
	}

	public static AspFloatLiteral parse(Scanner s){
		enterParser("float literal");

		AspFloatLiteral afl = new AspFloatLiteral(s.curLineNum(), s.curToken());
		s.readNextToken();

		leaveParser("float literal");
		return afl;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite(String.format("%.6f", flt.floatLit));
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeFloatValue(flt.floatLit);
	}
}
