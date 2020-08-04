package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspAssignStmt extends AspStmt {
	AspName name;
	ArrayList<AspSubscription> subs;
	AspExpr expr;

	AspAssignStmt(int n) {
		super(n);
		subs = new ArrayList<AspSubscription>();
	}
	
	public static AspAssignStmt parse(Scanner s) {
		enterParser("assignment");

		AspAssignStmt ass = new AspAssignStmt(s.curLineNum());
		test(s, nameToken);
		ass.name = AspName.parse(s);
		while (s.curToken().kind == leftBracketToken) {
			ass.subs.add(AspSubscription.parse(s));
		}
		skip(s, equalToken);
		ass.expr = AspExpr.parse(s);
		
		leaveParser("assignment");
		return ass;
	}

	@Override
	public void prettyPrint() {
		name.prettyPrint();
		for (AspSubscription as : subs) {
			as.prettyPrint();
		}
		Main.log.prettyWrite(" = ");
		expr.prettyPrint();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		RuntimeValue res = expr.eval(curScope);
		String valName = name.name.name;
		if(subs.isEmpty()){
			curScope.assign(valName, res);
		} else {
			RuntimeValue value = curScope.find(valName, this);
			RuntimeValue v;
			if (value == null){
				RuntimeValue.runtimeError(valName + " does not exist", this);
			}
			for(int i = 0; i < subs.size()-1; i++){
				v = subs.get(i).eval(curScope);
				if (v.getIntValue("assign statement", this) >= value.evalLen(this).getIntValue("assign statement", this)) {
					RuntimeValue.runtimeError("List index " + v.getIntValue("assign statement", this) + " out of range!", this);
				}
				valName += "[" + v.getStringValue("assign statement", this) + "]";
				value = value.evalSubscription(v, this);
			}
			if (subs.get(subs.size()-1).eval(curScope).getIntValue("assign statement", this) 
					>= value.evalLen(this).getIntValue("asign statement", this)) {
				RuntimeValue.runtimeError("List index " + subs.get(subs.size()-1).eval(curScope).getIntValue("assign statement", this)
										+ " out of range!", this);
			}
			v = subs.get(subs.size()-1).eval(curScope);
			valName += "[" + v.getStringValue("assign statement", this) + "]";
			value.evalAssignElem(v, res, this);
		}
		trace(valName + " = " + res.showInfo());
		return new RuntimeNoneValue();
	}
}
