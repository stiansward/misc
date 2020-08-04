package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFloatValue extends RuntimeValue {
	double floatValue;

	public RuntimeFloatValue(double floatValue){
		this.floatValue = floatValue;
	}

	// Get methods
	@Override
	protected String typeName() {
		return "float";
	}

	@Override
	public String toString(){
		return String.valueOf(floatValue);
	}

	@Override
	public double getFloatValue(String what, AspSyntax where) {
		return floatValue;
	}

	@Override
	public long getIntValue(String what, AspSyntax where) {
		return (long)floatValue;
	}

	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return floatValue != 0;
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where) {
		return "" + floatValue;
	}

	//LEGAL operators
	@Override
	public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(floatValue + v.getFloatValue("+ operand",where));
		}
		runtimeError("Type error for +.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(floatValue - v.getFloatValue("- operand", where));
		}
		runtimeError("Type error for -.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(floatValue*v.getFloatValue("* operand", where));
		}
		runtimeError("Type error for *.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(floatValue/v.getFloatValue("/ operand", where));
		}
		runtimeError("Type error for /.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(Math.floor(floatValue / v.getFloatValue("// operand", where)));
		}
		runtimeError("Type error for //.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			double v1 = floatValue;
			double v2 = v.getFloatValue("% operator", where);
			return new RuntimeFloatValue(v1 - v2 * Math.floor(v1 / v2));
		}
		runtimeError("Type error for %.", where);
		return null; // Required by the compiler.
	}

	//Boolean operators
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(floatValue == v.getFloatValue("== operand", where));
		}
		runtimeError("Type error for ==.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(floatValue > v.getFloatValue("> operand", where));
		}
		runtimeError("Type error for >.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(floatValue >= v.getFloatValue(">= operand", where));
		}
		runtimeError("Type error for >=.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(floatValue < v.getFloatValue("< operand", where));
		}
		runtimeError("Type error for <.", where);
		return null; // Required by the compiler.
	}
	@Override
	public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(floatValue <= v.getFloatValue("<= operand", where));
		}
		runtimeError("Type error for <=.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where){
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(floatValue != v.getFloatValue("!= operand", where));
		}
		runtimeError("Type error for !=.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(!getBoolValue("not operand", where));
	}

	@Override
	public RuntimeValue evalNegate(AspSyntax where) {
		return new RuntimeFloatValue(-floatValue);
	}

	@Override
	public RuntimeValue evalPositive(AspSyntax where) {
		return new RuntimeFloatValue(floatValue);
	}
}
