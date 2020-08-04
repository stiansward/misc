package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeDictValue extends RuntimeValue{
	LinkedHashMap<String, RuntimeValue> map = new LinkedHashMap<>();

	public RuntimeDictValue(){}

	@Override
	public String typeName() {
		return "dictionary";
	}

	@Override
	public String showInfo() {
		String res = "{";
		int nCounted = 0;
		List<String> keys = new ArrayList<String>();
		keys.addAll(map.keySet());
		Collections.reverse(keys);
		for (String s : keys) {
			if (nCounted++ > 0) res += ", ";
			res += "'" + s + "' : " + map.get(s).showInfo();
		}
		return res + "}";
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where){
		return showInfo();
	}

	// Get methods
	public boolean getBoolValue(String what, AspSyntax where) {
		return !map.isEmpty();
	}

	// Add to LinkedHashMap
	public RuntimeValue add(RuntimeValue key, RuntimeValue value, AspSyntax where){
		if(!(key instanceof RuntimeStringValue)){
			runtimeError(key.typeName() + " cannot be interpreted as string!", where);
		}
		return map.put(key.getStringValue("add to dictionary", where), value);
	}

	// Eval methods
	@Override
	public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
		add(inx, val, where);
	}
	
	@Override
	public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
		RuntimeValue value = map.get(v.getStringValue("eval subscription [...]", where));
		if(value == null){
			value = new RuntimeNoneValue();
		}
		return value;
	}
	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		return new RuntimeIntValue(map.size());
	}
}
