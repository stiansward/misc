package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspBooleanLiteral extends AspAtom{
	Token value;

	public AspBooleanLiteral(int n, Token value) {
		super(n);
		this.value = value;
	}

	public static AspBooleanLiteral parse(Scanner s){
		enterParser("boolean literal");

		AspBooleanLiteral abl = new AspBooleanLiteral(s.curLineNum(), s.curToken());
		s.readNextToken();

		leaveParser("boolean literal");
		return abl;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite(value.name);
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return (value.kind == trueToken) ? new RuntimeBoolValue(true) : new RuntimeBoolValue(false);
	}
}
