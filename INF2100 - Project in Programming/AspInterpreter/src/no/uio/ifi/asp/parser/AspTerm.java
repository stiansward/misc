package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.*;

public class AspTerm extends AspSyntax {
	ArrayList<AspFactor> factors;
	ArrayList<AspTermOpr> termOprs;

	AspTerm(int n) {
		super(n);
		factors = new ArrayList<AspFactor>();
		termOprs = new ArrayList<AspTermOpr>();
	}
	
	public static AspTerm parse(Scanner s) {
		enterParser("term");

		AspTerm term = new AspTerm(s.curLineNum());
		term.factors.add(AspFactor.parse(s));
		while (s.isTermOpr()) {
			term.termOprs.add(AspTermOpr.parse(s));
			term.factors.add(AspFactor.parse(s));
		}

		leaveParser("term");
		return term;
	}

	@Override
	void prettyPrint() {
		for(int i = 0; i < factors.size(); i++) {
			factors.get(i).prettyPrint();
			if (i < factors.size() - 1) {
				termOprs.get(i).prettyPrint();
			}
		}
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = factors.get(0).eval(curScope);
		for (int i = 1; i < factors.size(); ++i) {
			TokenKind k = termOprs.get(i-1).op.kind;
			switch (k) {
			case minusToken:
				v = v.evalSubtract(factors.get(i).eval(curScope), this); break;
			case plusToken:
				v = v.evalAdd(factors.get(i).eval(curScope), this); break;
			default:
				RuntimeValue.runtimeError("Illegal term operator " + k.toString(), factors.get(i).lineNum);
			}
		}
		if (v == null) return new RuntimeNoneValue();
		return v;
	}
}
