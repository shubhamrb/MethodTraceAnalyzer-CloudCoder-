package dataparser;

import java.util.Stack;


// Model Class the methods that are common in both the log Files

public class CommonMethods{
	private String mName;  //Methods Name
	private String cName;  // Class Name
	private String params[];  //Parameters
	private String rType;     	// Return type of methods
	private boolean isStatic;    // method is static or not
	private double runTime1;		//run time for methods of first log file
	private double runtime2;		//run time for methods of second log file
	private Stack<String> methodStack1 = new Stack<>();			//Stack for 
	private Stack<String> methodStack2 = new Stack<>();
	
	public CommonMethods(String mName, String cName, boolean sOrNot, String[] params, String rType, double rt1, double rt2, Stack<String> ms1, Stack<String> ms2){
		this.setMName(mName);
		this.setCName(cName);
		this.setParams(params);
		this.setRType(rType);
		this.setStatic(sOrNot);
		this.setRunTime1(rt1);
		this.setRuntime2(rt2);
		this.setMethodStack1(ms1);
		this.setMethodStack2(ms2);
	}

	public String getMName() {
		return mName;
	}

	public void setMName(String mName) {
		this.mName = mName;
	}

	public String getCName() {
		return cName;
	}

	public void setCName(String cName) {
		this.cName = cName;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String params[]) {
		this.params = params;
	}

	public String getRType() {
		return rType;
	}

	public void setRType(String rType) {
		this.rType = rType;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean staticOrNot) {
		this.isStatic = staticOrNot;
	}

	public double getRunTime1() {
		return runTime1;
	}

	public void setRunTime1(double runTime1) {
		this.runTime1 = runTime1;
	}

	public double getRuntime2() {
		return runtime2;
	}

	public void setRuntime2(double runtime2) {
		this.runtime2 = runtime2;
	}

	public Stack<String> getMethodStack1() {
		return methodStack1;
	}

	public void setMethodStack1(Stack<String> methodStack1) {
		this.methodStack1 = methodStack1;
	}

	public Stack<String> getMethodStack2() {
		return methodStack2;
	}

	public void setMethodStack2(Stack<String> methodStack2) {
		this.methodStack2 = methodStack2;
	}
}
