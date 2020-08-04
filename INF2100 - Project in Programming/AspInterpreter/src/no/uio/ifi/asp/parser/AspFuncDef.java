package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeFunc;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFuncDef extends AspStmt {
	AspName name;
	ArrayList<AspName> args;
	AspSuite body;

	AspFuncDef(int n) {
		super(n);
		args = new ArrayList<AspName>();
	}
	
	public static AspFuncDef parse(Scanner s) {
		enterParser("func def");

		AspFuncDef funcDef = new AspFuncDef(s.curLineNum());
		skip(s, defToken);
		funcDef.name = AspName.parse(s);
		skip(s, leftParToken);
		while (s.curToken().kind == nameToken) {
			funcDef.args.add(AspName.parse(s));
			if (s.curToken().kind == rightParToken) break;
			skip(s, commaToken);
		}
		skip(s, rightParToken);
		skip(s, colonToken);
		funcDef.body = AspSuite.parse(s);

		leaveParser("func def");
		return funcDef;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("def ");
		name.prettyPrint();
		Main.log.prettyWrite(" (");
		for (int i = 0; i < args.size(); i++) {
			args.get(i).prettyPrint();
			if (i < args.size() - 1) {
				Main.log.prettyWrite(", ");
			}
		}
		Main.log.prettyWrite("):");
		body.prettyPrint();
		Main.log.prettyWriteLn();
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		trace("def " + name.name.name);
		RuntimeFunc func = new RuntimeFunc(name.name.name);
		func.defScope = new RuntimeScope(curScope);
		func.args = args;
		func.suite = body;
		curScope.assign(func.name, func);
		return func;
	}
}
