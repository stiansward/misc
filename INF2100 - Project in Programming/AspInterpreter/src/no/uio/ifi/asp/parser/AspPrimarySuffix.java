package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspPrimarySuffix extends AspSyntax {
	AspPrimarySuffix suffix;

	AspPrimarySuffix(int n) {
		super(n);
	}
	
	public static AspPrimarySuffix parse(Scanner s) {
		enterParser("primary suffix");

		AspPrimarySuffix suf = new AspPrimarySuffix(s.curLineNum());
		if (s.curToken().kind == leftParToken) {
			suf.suffix = AspArguments.parse(s);
		} else if (s.curToken().kind == leftBracketToken) {
			suf.suffix = AspSubscription.parse(s);
		} else {
			parserError(s.curToken().showInfo(), s.curLineNum());
		}

		leaveParser("primary suffix");
		return suf;
	}

	@Override
	public void prettyPrint() {
		suffix.prettyPrint();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue{
		RuntimeValue res = suffix.eval(curScope);
		return res;
	}
}
