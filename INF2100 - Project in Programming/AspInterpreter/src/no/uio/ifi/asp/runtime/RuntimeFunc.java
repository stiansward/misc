package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.parser.AspName;
import no.uio.ifi.asp.parser.AspSuite;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFunc extends RuntimeValue {
	public String name;
	public ArrayList<AspName> args;
	public RuntimeScope defScope;
	public AspSuite suite;
	
	public RuntimeFunc(String name) {
		this.name = name;
	}

	@Override
	protected String typeName() {
		return "function";
	}
	
	public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> v, AspSyntax where) {
		RuntimeScope scope = new RuntimeScope(defScope);
		if (v.size() != args.size()) {
			RuntimeValue.runtimeError("Wrong number of parameters to "+name
										+"!\nMust be "+args.size()+" and not "+v.size()+"!",where);
		}
		for (int i = 0; i < args.size(); i++) {
			scope.assign(args.get(i).name.name, v.get(i));
		}
		try {
			return suite.eval(scope);
		} catch (RuntimeReturnValue e) {
			return e.value;
		}
	}
}
