package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.*;

public class AspFactor extends AspSyntax {
	ArrayList<AspFactorPrefix> prefixes;
	ArrayList<AspPrimary> primaries;
	ArrayList<AspFactorOpr> factorOprs;

	AspFactor(int n) {
		super(n);
		prefixes = new ArrayList<AspFactorPrefix>();
		primaries = new ArrayList<AspPrimary>();
		factorOprs = new ArrayList<AspFactorOpr>();
	}
	
	public static AspFactor parse(Scanner s) {
		enterParser("factor");

		AspFactor fac = new AspFactor(s.curLineNum());
		while (true) {
			if (s.isFactorPrefix()) {
				fac.prefixes.add(AspFactorPrefix.parse(s));
			} else fac.prefixes.add(null);
			fac.primaries.add(AspPrimary.parse(s));
			if (!s.isFactorOpr()) break;
			fac.factorOprs.add(AspFactorOpr.parse(s));
		}

		leaveParser("factor");
		return fac;
	}

	@Override
	void prettyPrint() {
		for (int i = 0; i < primaries.size(); i++) {
			if (prefixes.get(i) != null) {
				prefixes.get(i).prettyPrint();
			}
			primaries.get(i).prettyPrint();
			if (i < primaries.size() - 1) {
				factorOprs.get(i).prettyPrint();
			}
		}
	}

	@Override
	RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue v = primaries.get(0).eval(curScope);
		if(prefixes.get(0) != null){
			TokenKind tk = prefixes.get(0).operator.kind;
			switch(tk){
				case minusToken: v = v.evalNegate(this); break;
				case plusToken: v = v.evalPositive(this); break;
				default: Main.panic("Illegal prefix, " + "tk");
			}
		}

		for(int i = 1; i < primaries.size(); i++){
			RuntimeValue next = primaries.get(i).eval(curScope);
			if(prefixes.get(i) != null){
				TokenKind tk = prefixes.get(i).operator.kind;
				switch(tk){
					case minusToken: next = next.evalNegate(this); break;
					case plusToken: next = next.evalPositive(this); break;
					default: Main.panic("Illegal prefix, " + "tk");
				}
			}
			TokenKind tk = factorOprs.get(i-1).op.kind;
			switch(tk){
			case astToken:
				v = v.evalMultiply(next, this);
				break;
			case slashToken:
				v = v.evalDivide(next, this);
				break;
			case doubleSlashToken:
				v = v.evalIntDivide(next, this);
				break;
			case percentToken:
				v = v.evalModulo(next, this);
				break;
			default:
				Main.panic("Illegal factor operator, " + tk + "!");
			}
		}
		return v;
	}
}
