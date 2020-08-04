package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;
import java.util.ArrayList;

public class AspArguments extends AspPrimarySuffix {
	ArrayList<AspExpr> exprs;
	public AspArguments(int n) {
		super(n);
		exprs = new ArrayList<AspExpr>();
	}

	public static AspArguments parse(Scanner s){
		enterParser("arguments");

		AspArguments arguments = new AspArguments(s.curLineNum());
		s.readNextToken();
		while(s.curToken().kind !=  rightParToken){
			arguments.exprs.add(AspExpr.parse(s));
			if (s.curToken().kind == rightParToken) break;
			skip(s, commaToken);
		}
		skip(s, rightParToken);

		leaveParser("arguments");
		return arguments;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("(");
		for (int i = 0; i < exprs.size(); i++) {
			exprs.get(i).prettyPrint();
			if (i < exprs.size() - 1) {
				Main.log.prettyWrite(", ");
			}
		}
		Main.log.prettyWrite(")");
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeListValue res = new RuntimeListValue();
		for (AspExpr e : exprs) {
			res.add(e.eval(curScope));
		}
		return res;
	}
}
