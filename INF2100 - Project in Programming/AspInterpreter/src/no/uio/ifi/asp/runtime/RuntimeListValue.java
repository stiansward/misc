package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.Iterator;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeListValue extends RuntimeValue {
	ArrayList<RuntimeValue> list = new ArrayList<>();

	public RuntimeListValue(){}

	public RuntimeListValue(ArrayList<RuntimeValue> list){
		this.list = list;
	}

	@Override
	protected String typeName() {
		return "list";
	}

	@Override
	public String showInfo() {
		String res = "[";
		Iterator<RuntimeValue> itr = list.iterator();
		while (itr.hasNext()) {
			res += itr.next().showInfo();
			if (itr.hasNext()){
				res += ", ";
			}
		}
		return res + "]";
	}

	// Get methods
	public boolean getBoolValue(String what, AspSyntax where) {
		return !list.isEmpty();
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where) {
		String res = "[";
		for (int i = 0; i < list.size(); i++) {
			res += list.get(i).showInfo();
			if (i < list.size()-1) res += ", ";
		}
		return res + "]";
	}

	public ArrayList<RuntimeValue> getList() {
		return list;
	}
	
	public RuntimeValue get(int index) {
		return list.get(index);
	}
	
	public int size() {
		return list.size();
	}

	// Eval methods
	public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
		int index = (int) v.getIntValue("List subscription", where);
		if(index >= list.size() || index < 0){
			runtimeError("IndexError: " + index + " is out of bounds!", where);
		}
		return list.get(index);
	}
	
	@Override
	public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
		list.set((int)inx.getIntValue("assign element to list", where), val);
	}

	private ArrayList<RuntimeValue> repeatList(int n){
		ArrayList<RuntimeValue> repeatedList = new ArrayList<>(list);
		while(n > 1){
			repeatedList.addAll(list);
			n--;
		}
		return repeatedList;
	}

	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		if(!(v instanceof RuntimeIntValue)){
			runtimeError("'*' undefined for " + typeName() + "*" + typeName()+"!", where);
		}
		int n = (int)v.getIntValue("* operator in list", where);
		if(n < 0){
			runtimeError("'*' undefined for negative numbers on" + typeName()+"!", where);
		}
		return new RuntimeListValue(repeatList(n));
	}

	public void add(RuntimeValue v){
		list.add(v);
		return;
	}

	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		return new RuntimeIntValue(list.size());
	}
}
