package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeFunc;
import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspPrimary extends AspSyntax {
	AspAtom atom;
	ArrayList<AspPrimarySuffix> suffixes;

	public AspPrimary(int n) {
		super(n);
		suffixes = new ArrayList<AspPrimarySuffix>();
	}
	
	public static AspPrimary parse(Scanner s) {
		enterParser("primary");

		AspPrimary prim = new AspPrimary(s.curLineNum());
		prim.atom = AspAtom.parse(s);
		while (s.curToken().kind == leftParToken ||
				s.curToken().kind == leftBracketToken) {
			prim.suffixes.add(AspPrimarySuffix.parse(s));
		}

		leaveParser("primary");
		return prim;
	}

	@Override
	public void prettyPrint() {
		atom.prettyPrint();
		for (AspPrimarySuffix suf : suffixes) {
			suf.prettyPrint();
		}
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		if (suffixes.size() == 0) {
			// Value
			return atom.eval(curScope);
		}
		else if (suffixes.get(0).suffix instanceof AspArguments) {
			// Function call
			AspName name = (AspName) atom;
			RuntimeFunc func = (RuntimeFunc) curScope.find(name.name.name, this);
			RuntimeListValue list = (RuntimeListValue) suffixes.get(0).eval(curScope);
			trace("Call function " + func.name + " with params " + list.getStringValue("call", this));
			RuntimeValue v = func.evalFuncCall(list.getList(), this);
			if (v instanceof RuntimeNoneValue) {
				trace("None");
			}
			return v;
		} else {
			// Subscription(s)
			RuntimeValue v = atom.eval(curScope);
			for (int i = 0; i < suffixes.size(); i++) {
				v = v.evalSubscription(suffixes.get(i).eval(curScope), this);
			}
			return v;
		}
	}
}
