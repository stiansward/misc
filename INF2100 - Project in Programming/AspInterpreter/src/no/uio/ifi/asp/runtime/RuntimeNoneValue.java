package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeNoneValue extends RuntimeValue {
	@Override
	protected String typeName() {
		return "None";
	}


	@Override 
	public String toString() {
		return "None";
	}


	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return false;
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where) {
		return "None";
	}


	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		return new RuntimeBoolValue(v instanceof RuntimeNoneValue);
	}


	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(true);
	}


	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		return new RuntimeBoolValue(!(v instanceof RuntimeNoneValue));
	}
}
