package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.Scanner;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeLibrary extends RuntimeScope {
	private Scanner keyboard = new Scanner(System.in);

	public RuntimeLibrary() {
		// float
		assign("float", new RuntimeFunc("float") {
			/*
			 * Returns a RuntimeFloatValue based on the float value
			 * of the parameter
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("float call");
				checkNumParams(params, 1, "float", where);
				return new RuntimeFloatValue(params.get(0).getFloatValue("float call", where));
			}
		});

		// input
		assign("input", new RuntimeFunc("input") {
			/*
			 * Returns a RuntimeStringValue with whatever the user
			 * gives as input
			 * Prints the message given as a parameter
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("input call");
				checkNumParams(params, 1, "input", where);
				System.out.print(params.get(0).getStringValue("input", where));
				return new RuntimeStringValue(keyboard.nextLine());
			}
		});

		// int
		assign("int", new RuntimeFunc("int") {
			/*
			 * Returns a RuntimeIntValue based on the integer value
			 * of the object given as parameter
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("int call");
				checkNumParams(params, 1, "int", where);
				return new RuntimeIntValue(params.get(0).getIntValue("int call", where));
			}
		});

		// len
		assign("len", new RuntimeFunc("len") {
			/*
			 * Returns a RuntimeIntValue based on the length
			 * of the object given as parameter
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("len call");
				checkNumParams(params, 1, "len", where);
				return params.get(0).evalLen(where);
			}
		});

		// print
		assign("print", new RuntimeFunc("print") {
			/*
			 * Prints the String representations of the 
			 * given parameters with one blank between
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("print call");
				String res = "";
				for (int i = 0; i < params.size(); i++) {
					if (i > 0) res += " ";
					res += params.get(i).getStringValue("print call", where);
				}
				System.out.println(res);
				return new RuntimeNoneValue();
			}
		});

		// range
		assign("range", new RuntimeFunc("range") {
			/*
			 * Returns a RuntimeListValue object based on an ArrayList that
			 * has been initialized to increment between the first and the
			 * second parameter in the function call
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("range call");
				checkNumParams(params, 2, "range", where);
				ArrayList<RuntimeValue> v = new ArrayList<RuntimeValue>();
				for (int i = (int)params.get(0).getIntValue("range", where);
							i < (int)params.get(1).getIntValue("range", where);i++) {
					v.add(new RuntimeIntValue(i));
				}
				return new RuntimeListValue(v);
			}
		});

		// str
		assign("str", new RuntimeFunc("str") {
			/*
			 * Returns a RuntimeStringValue based on the string value
			 * of the object given as parameter
			 */
			@Override
			public RuntimeValue evalFuncCall(
					ArrayList<RuntimeValue> params,
					AspSyntax where) {
//				System.out.println("str call");
				checkNumParams(params, 1, "str", where);
				return new RuntimeStringValue(params.get(0).getStringValue("str", where));
			}
		});
	}


	private void checkNumParams(ArrayList<RuntimeValue> actArgs, 
				int nCorrect, String id, AspSyntax where) {
		if (actArgs.size() != nCorrect)
			RuntimeValue.runtimeError("Wrong number of parameters to "+id+"!",where);
	}
}
