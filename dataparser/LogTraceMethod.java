package dataparser;
import java.util.Stack;

import dataparser.DataParser;

import java.util.Arrays;


// Class to store information about Methods
public class LogTraceMethod {
	private static int numberOfMethod = 1;
	private String methodsName;
	private String classesName;
	private String threadId;
	private String startTime;
	private String parameters[];
	private String returnType;
	private String endTime = "";
	private boolean isStatic;	// Set True if method is static
	private String thisPointer;
	private double runTime;
	private int id;
	private Stack<String> methodStack = new Stack<>();

	
	public LogTraceMethod(String mName, String cName, String tid, String sTime, boolean isStat, String[] params, String rType){
		this.methodsName = mName;
		this.parameters = params;
		this.returnType = rType;
		this.classesName = cName;
		this.threadId = tid;
		this.startTime = sTime;
		this.isStatic = isStat;
		this.endTime = "";
		this.setId(numberOfMethod);
		numberOfMethod++;
	}
	
	public LogTraceMethod(String mName, String cName, String tid, String sTime, boolean sOrNot, String tPointer, String[] params, String rType){
		this(mName, cName, tid, sTime, sOrNot, params, rType);
		this.thisPointer = tPointer;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LogTraceMethod){
			LogTraceMethod logTraceMethod = (LogTraceMethod)o;
			if(this.methodsName.equals(logTraceMethod.getMethodsName()) && Arrays.equals(this.parameters, logTraceMethod.getParameters()) && this.classesName.equals(logTraceMethod.getClassesName())){
				return true;
			}
		}
		return false;
	}


	public String getClassesName() {
		return classesName;
	}
	public void setClassesName(String classesName) {
		this.classesName = classesName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setIsStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getThisPointer() {
		return thisPointer;
	}
	public void setThisPointer(String thisPointer) {
		this.thisPointer = thisPointer;
	}

	public String getMethodsName() {
		return methodsName;
	}

	public void setMethodName(String methodName) {
		this.methodsName = methodName;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public double getRuntime(){
		return runTime;
	}
	
	// Tells if the method ended its execution or not according to the log file
	public boolean hasEnded(){
		if(endTime.equals("")){
			return false;
		}
		else{
			return true;
		}
	}
	// Calculate total runtime of the method
	public void calculateRuntime(){
		if(!endTime.equals("")){
			runTime = DataParser.timeDifference(startTime, endTime);
		}
		else{
			runTime = 0.0; 					//method never ended
		}
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String parameters[]) {
		this.parameters = parameters;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Stack<String> getMethodStack() {
		return methodStack;
	}
	
	public void pushInMethodStack(String s){
		methodStack.push(s);
	}
	
}
