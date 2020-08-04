package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import no.uio.ifi.asp.main.Main;

public class AspName extends AspAtom {
	public Token name;

	AspName(int n) {
		super(n);
	}

	public static AspName parse(Scanner s) {
		enterParser("name");

		AspName n = new AspName(s.curLineNum());
		test(s, nameToken);
		n.name = s.curToken();
		skip(s, nameToken);

		leaveParser("name");
		return n;
	}

	@Override
	public void prettyPrint() {
		Main.log.prettyWrite(name.name);
	}

	@Override
	public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
		return curScope.find(name.name, this);
	}
}
