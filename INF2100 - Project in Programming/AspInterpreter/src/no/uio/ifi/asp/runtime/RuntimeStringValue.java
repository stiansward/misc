package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeStringValue extends RuntimeValue {
	String strValue;

	public RuntimeStringValue(String s){
		this.strValue = s;
	}

	public String getStringValue(String what, AspSyntax where) {
		return strValue;
	}

	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return strValue.equals("") ? false : true;
	}
	
	@Override
	public long getIntValue(String what, AspSyntax where) {
		long res = 0;
		try {
			res = Integer.parseInt(strValue);
		} catch (NumberFormatException e) {
			runtimeError(strValue + " is not a number!", where);
		}
		return res;
	}
	
	@Override
	public double getFloatValue(String what, AspSyntax where) {
		double res = 0;
		try {
			res = Float.parseFloat(strValue);
		} catch (NumberFormatException e) {
			runtimeError(strValue + " is not a number!", where);
		}
		return res;
	}

	@Override
	public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeStringValue(strValue + v.getStringValue("+ operator", where));
		}
		runtimeError("'+' undefined for "+typeName()+"!", where);
		return null;
	}

	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue){
			long n = v.getIntValue("* operator", where);
			if(n >= 0){
				String strRepeated = new String(new char[(int) n]).replace("\0", strValue);
				return new RuntimeStringValue(strRepeated);
			}
			else runtimeError("Can't repeat a string a negative number of times", where);
		}
		runtimeError("'*' undefined for "+typeName()+"!", where);
		return null;
	}

	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeBoolValue(strValue.equals(v.getStringValue("== operator", where)));
		}
		else return new RuntimeBoolValue(false);
	}

	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeBoolValue(!strValue.equals(v.getStringValue("!= operator", where)));
		}
		else return new RuntimeBoolValue(false);
	}

	@Override
	public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeBoolValue(strValue.compareTo(v.getStringValue("< operator", where)) < 0);
		}
		else return new RuntimeBoolValue(false);
	}

	@Override
	public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeBoolValue(strValue.compareTo(v.getStringValue("<= operator", where)) <= 0);
		}
		else return new RuntimeBoolValue(false);
	}

	@Override
	public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeBoolValue(strValue.compareTo(v.getStringValue("> operator", where)) > 0);
		}
		else return new RuntimeBoolValue(false);
	}

	@Override
	public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
		if(v instanceof RuntimeStringValue){
			return new RuntimeBoolValue(strValue.compareTo(v.getStringValue(">= operator", where)) >= 0);
		}
		else return new RuntimeBoolValue(false);
	}

	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		if(strValue != null){
			return new RuntimeIntValue(strValue.length());
		}
		return new RuntimeIntValue(0);
	}

	@Override
	public String showInfo() {
		if (strValue.indexOf("'") >= 0){
			return "\"" + strValue + "\"";
		}
		else
			return "'" + strValue + "'";
	}
	
	protected String typeName(){
		return "string";
	}

	@Override
	public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
		if(!(v instanceof RuntimeIntValue)){
			runtimeError("IndexError: Can't index with " + v.typeName(), where);
		}
		int index = (int)v.getIntValue("String subscription", where);
		if(index < 0 || index >= strValue.length()){
			runtimeError("IndexError: " + index + " is out of bounds!", where);
		}
		return new RuntimeStringValue(String.valueOf(strValue.charAt(index)));
	}
}
