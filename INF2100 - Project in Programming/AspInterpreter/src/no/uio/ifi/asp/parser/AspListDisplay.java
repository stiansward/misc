package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspListDisplay extends AspAtom {
	ArrayList<AspExpr> exprs;

	public AspListDisplay(int n) {
		super(n);
		exprs = new ArrayList<AspExpr>();
	}

	public static AspListDisplay parse(Scanner s){
		enterParser("list display");

		AspListDisplay ald = new AspListDisplay(s.curLineNum());
		s.readNextToken();
		while(s.curToken().kind != rightBracketToken){
			ald.exprs.add(AspExpr.parse(s));
			if(s.curToken().kind == rightBracketToken) break;
			skip(s, commaToken);
		}
		s.readNextToken();

		leaveParser("list display");
		return ald;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite("[");
		for (int i = 0; i < exprs.size(); i++) {
			exprs.get(i).prettyPrint();
			if (i < exprs.size() - 1) {
				Main.log.prettyWrite(", ");
			}
		}
		Main.log.prettyWrite("]");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeListValue v = new RuntimeListValue();
		for (AspExpr e : exprs) {
			v.add(e.eval(curScope));
		}
		return v;
	}
}
