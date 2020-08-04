package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;

public class AspStringLiteral extends AspAtom {
	Token strToken;
	public AspStringLiteral(int n) {
		super(n);
	}

	public static AspStringLiteral parse(Scanner s){
		enterParser("string literal");

		AspStringLiteral strLit = new AspStringLiteral(s.curLineNum());
		strLit.strToken = s.curToken();
		s.readNextToken();

		leaveParser("string literal");
		return strLit;
	}

	@Override
	void prettyPrint() {
		if (strToken.stringLit.contains("\"")) {
		Main.log.prettyWrite("'" + strToken.stringLit + "'");
		} else {
			Main.log.prettyWrite("\"" + strToken.stringLit + "\"");
		}
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return new RuntimeStringValue(strToken.stringLit);
	}
}
