package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeBoolValue extends RuntimeValue {
	boolean boolValue;

	public RuntimeBoolValue(boolean v) {
		boolValue = v;
	}

	@Override
	protected String typeName() {
		return "boolean";
	}

	@Override 
	public String toString() {
		return (boolValue ? "True" : "False");
	}

	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return boolValue;
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where) {
		return toString();
	}
	
	@Override
	public long getIntValue(String what, AspSyntax where) {
		return (boolValue) ? 1 : 0;
	}
	
	@Override
	public double getFloatValue(String what, AspSyntax where) {
		return (boolValue) ? 1.0 : 0.0;
	}

	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeNoneValue) {
			return new RuntimeBoolValue(false);
		}
		runtimeError("Type error for ==.", where);
		return null;  // Required by the compiler
	}

	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(! boolValue);
	}

	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeNoneValue) {
			return new RuntimeBoolValue(true);
		}
		runtimeError("Type error for !=.", where);
		return null;  // Required by the compiler
	}
}
