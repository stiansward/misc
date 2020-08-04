package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;

public abstract class AspAtom extends AspSyntax {

	AspAtom(int n) {
		super(n);
	}

	static AspAtom parse(Scanner s) {
		enterParser("atom");

		AspAtom atom = null;
		switch (s.curToken().kind) {
		case falseToken:
			atom = AspBooleanLiteral.parse(s); break;
		case trueToken:
			atom = AspBooleanLiteral.parse(s); break;
		case floatToken:
			atom = AspFloatLiteral.parse(s); break;
		case integerToken:
			atom = AspIntegerLiteral.parse(s); break;
		case leftBraceToken:
			atom = AspDictDisplay.parse(s); break;
		case leftBracketToken:
			atom = AspListDisplay.parse(s); break;
		case leftParToken:
			atom = AspInnerExpr.parse(s); break;
		case nameToken:
			atom = AspName.parse(s); break;
		case noneToken:
			atom = AspNoneLiteral.parse(s); break;
		case stringToken:
			atom = AspStringLiteral.parse(s); break;
		default:
			parserError("Expected an expression atom but found a " +
						s.curToken().kind + "!", s.curLineNum());
		}

		leaveParser("atom");
		return atom;
	}

	@Override
	abstract void prettyPrint();
	@Override
	abstract RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue;
}
