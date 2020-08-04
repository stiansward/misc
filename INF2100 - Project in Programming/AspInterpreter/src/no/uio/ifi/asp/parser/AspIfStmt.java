package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;
import java.util.ArrayList;

public class AspIfStmt extends AspStmt {
	AspExpr ifTest;
	AspSuite ifSuite;
	ArrayList<AspExpr> elifTests;
	ArrayList<AspSuite> elifSuites;
	AspSuite elseSuite;

	AspIfStmt(int n) {
		super(n);
		elifTests = new ArrayList<AspExpr>();
		elifSuites = new ArrayList<AspSuite>();
	}
	
	public static AspIfStmt parse(Scanner s) {
		enterParser("if stmt");
		AspIfStmt ifStmt = new AspIfStmt(s.curLineNum());
		skip(s, ifToken);
		ifStmt.ifTest = AspExpr.parse(s);
		skip(s, colonToken);
		ifStmt.ifSuite = AspSuite.parse(s);
		while (s.curToken().kind == elifToken) {
			skip(s, elifToken);
			ifStmt.elifTests.add(AspExpr.parse(s));
			skip(s, colonToken);
			ifStmt.elifSuites.add(AspSuite.parse(s));
		}
		if (s.curToken().kind == elseToken) {
			skip(s, elseToken);
			skip(s, colonToken);
			ifStmt.elseSuite = AspSuite.parse(s);
		}

		leaveParser("if stmt");
		return ifStmt;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite("if ");
		ifTest.prettyPrint();
		Main.log.prettyWrite(":");
		ifSuite.prettyPrint();
		for (int i = 0; i < elifTests.size(); i++) {
			Main.log.prettyWriteLn();
			Main.log.prettyWrite("elif ");
			elifTests.get(i).prettyPrint();
			Main.log.prettyWrite(":");
			elifSuites.get(i).prettyPrint();
		}
		if (elseSuite != null) {
			Main.log.prettyWriteLn();
			Main.log.prettyWrite("else:");
			elseSuite.prettyPrint();
		}
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		int i = 0;
		if (ifTest.eval(curScope).getBoolValue("if test", this) == true) {
			trace("if True alt #1: ...");
			return ifSuite.eval(curScope);
		} else {
			while (i < elifTests.size() && elifTests.get(i).eval(curScope).getBoolValue("elif test", this) == false) i++;
			if (i < elifTests.size()) {
	    		trace("if True alt #" + (i+2) + ": ...");
				return elifSuites.get(i).eval(curScope);
			}
			else if (elseSuite != null) {
	    		trace("else: ...");
				return elseSuite.eval(curScope);
			}
		}
		return new RuntimeNoneValue();
	}
}