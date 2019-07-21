package dataparser;

import java.util.ArrayList;
import java.util.List;

import dataparser.LogTraceMethod;
// A helper class to store information of threads
public class LogThreads {
	private String thID;
	private String thName;
	private List<LogTraceMethod> logMethods = new ArrayList<>();
	public LogThreads(String tid, String tname){
		this.setThID(tid);
		this.setThName(tname);
	}
	public String getThName() {
		return thName;
	}
	public void setThName(String thName) {
		this.thName = thName;
	}
	public String getThID() {
		return thID;
	}
	public void setThID(String thID) {
		this.thID = thID;
	}
	public List<LogTraceMethod> getLogMethods() {
		return logMethods;
	}
	public void addLogMethod(LogTraceMethod method) {
		logMethods.add(method);
	}
}
