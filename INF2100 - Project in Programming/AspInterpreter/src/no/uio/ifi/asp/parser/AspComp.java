package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;

public class AspComp extends AspSyntax {
	ArrayList<AspTerm> terms;
	ArrayList<AspCompOpr> compOprs;

	AspComp(int n) {
		super(n);
		terms = new ArrayList<AspTerm>();
		compOprs = new ArrayList<AspCompOpr>();
	}
	
	public static AspComp parse(Scanner s) {
		enterParser("comparison");

		AspComp comp = new AspComp(s.curLineNum());
		comp.terms.add(AspTerm.parse(s));
		while (s.isCompOpr()) {
			comp.compOprs.add(AspCompOpr.parse(s));
			comp.terms.add(AspTerm.parse(s));
		}

		leaveParser("comparison");
		return comp;
	}

	@Override
	void prettyPrint() {
		for(int i = 0; i < terms.size(); i++) {
			terms.get(i).prettyPrint();
			if (i < terms.size() - 1) {
				compOprs.get(i).prettyPrint();
			}
		}
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue result = terms.get(0).eval(curScope);
		RuntimeValue leftValue = null;
		RuntimeValue rightValue = null;
		for (int i = 1; i < terms.size(); i++) {
			leftValue = terms.get(i-1).eval(curScope);
			rightValue = terms.get(i).eval(curScope);
			TokenKind k = compOprs.get(i-1).kind;
			switch (k) {
			case lessToken:
				result = leftValue.evalLess(rightValue, this); break;
			case greaterToken:
				result = leftValue.evalGreater(rightValue, this); break;
			case doubleEqualToken:
				result = leftValue.evalEqual(rightValue, this); break;
			case greaterEqualToken:
				result = leftValue.evalGreaterEqual(rightValue, this); break;
			case lessEqualToken:
				result = leftValue.evalLessEqual(rightValue, this); break;
			case notEqualToken:
				result = leftValue.evalNotEqual(rightValue, this); break;
			default:
				RuntimeValue.runtimeError("Illegal operator " + k.toString(), terms.get(i).lineNum);
			}
			if(!result.getBoolValue("less than", this)){
				break;
			}
		}
		return result;
	}
}
