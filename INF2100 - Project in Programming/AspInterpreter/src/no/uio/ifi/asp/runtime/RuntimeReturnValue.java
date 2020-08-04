package no.uio.ifi.asp.runtime;

// For part 4:

@SuppressWarnings("serial")
public class RuntimeReturnValue extends Exception {
	public int lineNum;
	public RuntimeValue value;

	public RuntimeReturnValue(RuntimeValue v, int lNum) {
		value = v;  lineNum = lNum;
	}
	
	public RuntimeValue getValue() {
		return value;
	}
}
