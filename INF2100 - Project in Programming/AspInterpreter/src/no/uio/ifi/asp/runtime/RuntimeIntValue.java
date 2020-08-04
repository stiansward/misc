package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeIntValue extends RuntimeValue {
	long intValue;

	public RuntimeIntValue(long intValue){
		this.intValue = intValue;
	}

	@Override
	protected String typeName() {
		return "int";
	}

	@Override
	public String toString(){
		return String.valueOf(intValue);
	}

	@Override
	public long getIntValue(String what, AspSyntax where) {
		return intValue;
	}

	@Override
	public double getFloatValue(String what, AspSyntax where) {
		return (double)intValue;
	}

	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return intValue != 0;
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where) {
		return toString();
	}

	//LEGAL operators
	@Override
	public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
				return new RuntimeIntValue(intValue + v.getIntValue("+ operator",where));
		} else if (v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(intValue + v.getFloatValue("+ operator",where));
		}
		runtimeError("Type error for +.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeIntValue(intValue - v.getIntValue("- operator",where));
		}else if (v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(intValue - v.getFloatValue("- operator", where));
		}
		runtimeError("Type error for -.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeIntValue(intValue * v.getIntValue("* operator", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue(intValue * v.getFloatValue("* operator", where));
		}
		runtimeError("Type error for *.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			return new RuntimeFloatValue((double)intValue/v.getFloatValue("/ operator", where));
		}
		runtimeError("Type error for /.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeIntValue(intValue/v.getIntValue("// operator", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeIntValue((long)(intValue/v.getFloatValue("// operator", where)));
		}
		runtimeError("Type error for //.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeIntValue(Math.floorMod(intValue, v.getIntValue("% operand", where)));
		}else if(v instanceof RuntimeFloatValue){
			long v1 = intValue;
			double v2 = v.getFloatValue("% operator", where);
			return new RuntimeFloatValue(v1 - v2 * Math.floor(v1 / v2));
		}
		runtimeError("Type error for %.", where);
		return null; // Required by the compiler.
	}

	//Boolean operators
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeBoolValue(intValue == v.getIntValue("== operand", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue((double)intValue == v.getFloatValue("== operand", where));
		}
		runtimeError("Type error for ==.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeBoolValue(intValue > v.getIntValue("> operand", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue((double)intValue > v.getFloatValue("> operand", where));
		}
		runtimeError("Type error for >.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeBoolValue(intValue >= v.getIntValue(">= operand", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue((double)intValue >= v.getFloatValue(">= operand", where));
		}
		runtimeError("Type error for >=.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeBoolValue(intValue < v.getIntValue("< operand", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue((double)intValue < v.getFloatValue("< operand", where));
		}
		runtimeError("Type error for <.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue){
			return new RuntimeBoolValue(intValue <= v.getIntValue("<= operator", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue(intValue <= v.getFloatValue("<= operator", where));
		}
		runtimeError("Type error for <=.", where);
		return null; // Required by the compiler.
	}

	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where){
		if (v instanceof RuntimeIntValue){
			return new RuntimeBoolValue(intValue != v.getIntValue("!= operand", where));
		}else if(v instanceof RuntimeFloatValue){
			return new RuntimeBoolValue((double)intValue != v.getFloatValue("!= operand", where));
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
		return new RuntimeIntValue(-intValue);
	}

	@Override
	public RuntimeValue evalPositive(AspSyntax where) {
		return new RuntimeIntValue(intValue);
	}
	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		System.out.println("Int len! " + intValue);
		runtimeError("'len' undefined for "+typeName()+"!", where);
		return null;  // Required by the compiler!
	}
}
