package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;
import java.util.ArrayList;

public class AspDictDisplay extends AspAtom {
	ArrayList<AspStringLiteral> sls = new ArrayList<AspStringLiteral>();
	ArrayList<AspExpr> aexprs = new ArrayList<AspExpr>();

	public AspDictDisplay(int n) {
		super(n);
	}

	public static AspDictDisplay parse(Scanner s){
		enterParser("dict display");

		AspDictDisplay add = new AspDictDisplay(s.curLineNum());
		skip(s, leftBraceToken);
		while (s.curToken().kind != rightBraceToken) {
			test(s, stringToken);
			add.sls.add(AspStringLiteral.parse(s));
			skip(s, colonToken);
			add.aexprs.add(AspExpr.parse(s));
			if (s.curToken().kind == rightBraceToken) break;
			skip(s, commaToken);
		}
		skip(s, rightBraceToken);

		leaveParser("dict display");
		return add;
	}

	@Override
	void prettyPrint() {
		Main.log.prettyWrite("{");
		for (int i = 0; i < sls.size(); i++) {
			sls.get(i).prettyPrint();
			Main.log.prettyWrite(":");
			aexprs.get(i).prettyPrint();
			if (i < sls.size() - 1) {
				Main.log.prettyWrite(", ");
			}
		}
		Main.log.prettyWrite("}");
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeDictValue v = new RuntimeDictValue();
		for (int i = aexprs.size() - 1; i >= 0; i--) {
			v.add(sls.get(i).eval(curScope), aexprs.get(i).eval(curScope), this);
		}
		return v;
	}
}
