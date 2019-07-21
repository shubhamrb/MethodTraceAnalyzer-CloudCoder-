package dataparser;

import java.util.ArrayList;
import java.util.List;

import dataparser.CommonMethods;

public class LogComparator {
	private List<LogTraceMethod> criticalMethods1;		// methods with higher time and the incomplete methods
	private List<LogTraceMethod> criticalMethods2;							
	private List<CommonMethods> commonCriticalMethods1 = new ArrayList<>();	// Methods that are critical in log1 and also present in log2
	private List<CommonMethods> commonCriticalMethods2 = new ArrayList<>();

	public LogComparator(DataParser dataparser1, DataParser dataparser2) {
		
		criticalMethods1 = new ArrayList<>();
		criticalMethods1.addAll(dataparser1.getIncompleteMethods());
		List<LogTraceMethod> super30Methods1 = dataparser1.sortByRuntime();
		if(super30Methods1.size()>=30){
			super30Methods1 = super30Methods1.subList(0, 30);
		}
		criticalMethods1.addAll(super30Methods1);
		
		criticalMethods2 = new ArrayList<>();
		criticalMethods2.addAll(dataparser2.getIncompleteMethods());
		List<LogTraceMethod> super30Methods2 = dataparser2.sortByRuntime();
		if(super30Methods2.size()>=30){
			super30Methods2 = super30Methods2.subList(0, 30);
		}
		criticalMethods2.addAll(super30Methods2);
		
		// Populating common Methods in Model "LogTraceMethod"
		for(LogTraceMethod met:criticalMethods1){
			for(LogTraceMethod mt:dataparser2.getAllMethods()){
				if(met.equals(mt)){
					commonCriticalMethods1.add(new CommonMethods(met.getMethodsName(), met.getClassesName(), met.isStatic(), met.getParameters(), met.getReturnType(), met.getRuntime(), mt.getRuntime(), met.getMethodStack(), mt.getMethodStack()));
					break;
				}
			}
		}
		for(LogTraceMethod met:criticalMethods2){
			for(LogTraceMethod mt:dataparser1.getAllMethods()){
				if(met.equals(mt)){
					commonCriticalMethods2.add(new CommonMethods(met.getMethodsName(), met.getClassesName(), met.isStatic(), met.getParameters(), met.getReturnType(), met.getRuntime(), mt.getRuntime(), met.getMethodStack(), mt.getMethodStack()));
					break;
				}
			}
		}	}
	
	// an array of Common Methods
	public List<CommonMethods>[] getCommonCriticalMethods(){
		List<CommonMethods>[] commonCriticalMethods = new List[2];
		commonCriticalMethods[0] = commonCriticalMethods1;
		commonCriticalMethods[1] = commonCriticalMethods2;
		return commonCriticalMethods;
	}

}
