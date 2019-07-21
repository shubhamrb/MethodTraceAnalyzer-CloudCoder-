/**
 * 
 */
package dataparser;
import java.util.Comparator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataparser.InvalidLogFileException;
import dataparser.LogThreads;
import dataparser.LogTraceMethod;

import java.util.HashMap;
import java.util.NoSuchElementException;


//Here all the data from the trace file will be parsed.
public class DataParser{
	// Contains all the threads and their methods.
	
	private HashMap<String, LogThreads> activeLogThread;
	//start-time and end-time of the trace
	private double LogTraceTime; // in seconds 
	
	public DataParser(File fileName) throws InvalidLogFileException{
		Scanner scanner = null;
		try{
			scanner = new Scanner(fileName); 
			activeLogThread = getThreadData(scanner);			// Name of threads
			LogTraceTime = getTraceTime(scanner);				// find the time
			scanLogMethods(scanner);							// scan the methods
		}
		catch(IOException e){
			System.out.println("File Not Found");
		}
	}


	// This method gets all the threads listed in the log file
	private HashMap<String, LogThreads> getThreadData(Scanner scanner) throws InvalidLogFileException{
		boolean found = false;
		Pattern pattern = Pattern.compile("\\s{8}(0x[a-fA-F0-9]+)\\s{2}(.+)"); //example:- [        0x58e0124  main]
		//Active Log Thread in the file
		while(scanner.hasNextLine()){
			found = Pattern.matches("Active Threads :", scanner.nextLine());
			if(found){
				break;
			}
		}
		if(found){
			HashMap<String, LogThreads> threadInfoList = new HashMap<>();
			while(scanner.hasNextLine()){
				String nextLine = scanner.nextLine();
				Matcher matcher = pattern.matcher(nextLine);
				if(matcher.matches()){
					threadInfoList.put(matcher.group(1),(new LogThreads(matcher.group(1),matcher.group(2))));
				}
				else{
					break;
				}
			}
			return threadInfoList;
		}
		throw new InvalidLogFileException("Invalid Log file");
	}
	
	// Get trace time
	private double getTraceTime(Scanner scanner) throws InvalidLogFileException{
		Pattern pattern1 = Pattern.compile("First tracepoint\\s{0,}:\\s{0,9}([0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{9})"); // Reg-ex for First Trace-point : 19:04:23.947000000
		Pattern pattern2 = Pattern.compile("Last tracepoint\\s{0,}:\\s{0,9}([0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{9})"); // Reg-ex for Last Trace-point : 19:04:23.947000000
		String firstTracepoint = "";
		String lastTracePoint = "";
		while(scanner.hasNextLine()){
			String nextLine = scanner.nextLine();
			Matcher matcher = pattern1.matcher(nextLine);
			if(matcher.matches()){
				firstTracepoint = matcher.group(1);
				break;
			}
		}
		while(scanner.hasNextLine()){
			String nextLine = scanner.nextLine();
			Matcher matcher = pattern2.matcher(nextLine);
			if(matcher.matches()){
				lastTracePoint = matcher.group(1);
				break;
			}
		}
		if(!scanner.hasNextLine()){
			throw new InvalidLogFileException("Invalid Log file");
		}
		return timeDifference(firstTracepoint,lastTracePoint);
	}
	
	//HH:mm:ss.SSSSSSSSS
	public static double timeDifference(String startTime, String endTime){
		int startHr = Integer.parseInt(startTime.substring(0,2));
		int startMin = Integer.parseInt(startTime.substring(3,5));
		int startSec = Integer.parseInt(startTime.substring(6,8));
		double startMillisec = Double.parseDouble("0"+startTime.substring(8,startTime.length()));
		int endHour = Integer.parseInt(endTime.substring(0,2));
		int endMin = Integer.parseInt(endTime.substring(3,5));
		int endSec = Integer.parseInt(endTime.substring(6,8));
		double endMillisec = Double.parseDouble("0"+endTime.substring(8,endTime.length()));
		
		return (endHour*3600+endMin*60+endSec+endMillisec) - (startHr*3600+startMin*60+startSec+startMillisec);
		
	}
	
	// Scan all the methods
	private void scanLogMethods(Scanner scanner) {
		Pattern pat = Pattern.compile("J9 timer[(]UTC[)].*");
		String nextLine;
		Matcher matcher;
		boolean found = false;
		while(scanner.hasNext()){
			nextLine = scanner.nextLine();
			matcher = pat.matcher(nextLine);
			if(matcher.matches()){
				found = true;
				break;
			}
		}
		String timePattern = "([0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{9})"; // group 1 :- Contains time in this format 19:04:24.092291382
		String threadIdPattern = "[\\*]?(0x[a-fA-F0-9]+)";	// group 2 :- Contains threadID in this format *0x26e0500
		String methodTraceIdPattern = "(\\S+)"; 	// group 3 :- Contains trace id in this format mt.3
		String typePattern = "(Entry|Exit|Event)";	// group 4 :- Contains Entry or Exit or Event(for stackTraf)
		// group 5 :- java/util/Scanner  		(ClassName)
		// group 6 :- <clinit>			 		(MethodName)
		// group 7 :- (Ljava/lang/Readable;)	(Parameters)
		// group 8 :- Ljava/util/Scanner;		(ReturnType)
		// group 9 :- static method      		(Static or not)
		String traceEntryPattern1 = "[\\*]?[><](.+)\\.(.+)\\((.+)?\\)([\\S]+)\\s(.*)";
		// Use this pattern when there is a stackTrace
		// Example :- [1] sun.io.ByteToCharUTF8.flush (ByteToCharUTF8.java:149)
		// group 5 :- If using this pattern
		String traceEntryPattern2 = "(.+)"; // Trace Entry Pattern For StackTrace
		
		pat = Pattern.compile(timePattern+"\\s+"+threadIdPattern+"\\s+"+methodTraceIdPattern+"\\s+"+typePattern+"\\s+"+traceEntryPattern1);
		Pattern pat2 = Pattern.compile(timePattern+"\\s+"+threadIdPattern+"\\s+"+methodTraceIdPattern+"\\s+"+typePattern+"\\s+"+traceEntryPattern2);
		String methodName;
		String className;
		String threadId;
		String startTime;
		String parameters[];
		String returnType;
		String thisPointer;
		//System.out.println(timePattern+"\\s+"+threadIdPattern+"\\s+"+methodTraceIdPattern+"\\s+"+typePattern+"\\s+"+traceEntryPattern);
		boolean staticOrNot;	// Set True if method is static
		HashMap<String, Stack<LogTraceMethod>> methodStack = new HashMap<>(); 
		for(String th: activeLogThread.keySet()){
			methodStack.put(th, new Stack<LogTraceMethod>());
		}
		LogTraceMethod method;
		while(scanner.hasNext()){
			nextLine = scanner.nextLine();
			matcher = pat.matcher(nextLine);
			if(matcher.matches()){
				startTime = matcher.group(1);
				className = matcher.group(5);
				threadId = matcher.group(2);
				methodName = matcher.group(6);
				parameters = getParameters(matcher.group(7));
				returnType = getReturnType(matcher.group(8));
				if(matcher.group(4).equals("Entry")){
					if(matcher.group(9).contains("static")){
						staticOrNot = true;
						method = new LogTraceMethod(methodName, className, threadId, startTime, staticOrNot, parameters, returnType);
						methodStack.get(method.getThreadId()).push(method);
					}
					else{
						staticOrNot =false;
						Pattern thisPointerPattern = Pattern.compile(".*(0x[0-9a-fA-F]+)");
						matcher = thisPointerPattern.matcher(matcher.group(9));
						if(matcher.matches()){
							thisPointer = matcher.group(1);
							method = new LogTraceMethod(methodName, className, threadId, startTime, staticOrNot, thisPointer, parameters, returnType);
							methodStack.get(method.getThreadId()).push(method);
						}
					}
				}
				else if(matcher.group(4).equals("Exit")){
					try{
						method = methodStack.get(threadId).pop();
						method.setEndTime(matcher.group(1)); 
						activeLogThread.get(matcher.group(2)).addLogMethod(method);
					}
					catch(EmptyStackException ex){
					}
				}
				// If the line does not match 'entry' or 'exit' then check for 'event'
			}
			else{
				Matcher m2 = pat2.matcher(nextLine);
				if(m2.matches()){
					threadId = m2.group(2);
					if(m2.group(4).equals("Event")){
						if(m2.group(5).contains("jstacktrace:")){
							continue;
						}
						try{
							method = methodStack.get(threadId).lastElement();
							method.pushInMethodStack(m2.group(5));
						}
						catch(NoSuchElementException ex){
							continue;
						}
					}
				}
				else{
					continue;
				}
			}
		}
		// Check if stack is empty, if it is not empty that means some methods that started has not exited, Next line of codes
		// handles that situation
		for(Stack<LogTraceMethod> methStack: methodStack.values()){
			if(!methStack.empty()){
				for(LogTraceMethod met: methStack){
					activeLogThread.get(met.getThreadId()).addLogMethod(met);
				}
			}
		}
		
		// Calculate runtime of all the methods in all the thread
		for(LogThreads th: activeLogThread.values()){
			for(LogTraceMethod met: th.getLogMethods()){
					met.calculateRuntime();
			}
		}
	}
	
	// -----------------------------------------------------Helper Methods for scanMethos-------------------------------------------------------------------------------------
	private String[] getParameters(String s){
		String[] params = {"void"};
		if(!(s == null)){
			params = s.split(";");
			return params;
		}
		return params;
	}
	private String getReturnType(String s) {
		if(s.equals("V")){
			return "void";
		}
		else{
			Pattern p = Pattern.compile("[A-Z](.*);");
			Matcher m = p.matcher(s);
			if(m.matches()){
				return m.group(1);
			}
		}
		return "void";
	}
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public HashMap<String, LogThreads> getActiveThreads(){
		return activeLogThread;
	}
	
	// Get all the methods of all the threads
	public List<LogTraceMethod> getAllMethods(){
		List<LogTraceMethod> allMethods = new ArrayList<>(); 
		for(LogThreads th: activeLogThread.values()){
			allMethods.addAll(th.getLogMethods());
		}
		return allMethods;
	}
	
	public double getTraceTime(){
		return LogTraceTime;
	}
	
	public LogTraceMethod getMethodById(int id){
		for(LogThreads th: activeLogThread.values()){
			for(LogTraceMethod met: th.getLogMethods()){
				if(met.getId() == id){
					return met;
				}
			}
		}
		return null;
	}
	
	
	//methods who have completed their execution.
	public ArrayList<LogTraceMethod> sortByRuntime(){
		List<LogTraceMethod> methods = new ArrayList<>();
		for(LogThreads th: activeLogThread.values()){
			for(LogTraceMethod met: th.getLogMethods()){
				if(met.hasEnded()){
					methods.add(met);
				}
			}
		}
		Collections.sort(methods, new DataParser.CustomComparator());
		return (ArrayList<LogTraceMethod>) methods;
	}
	
	//methods who have not completed their execution.
		public ArrayList<LogTraceMethod> getIncompleteMethods(){
			List<LogTraceMethod>  methods = new ArrayList<>();
			for(LogThreads th: activeLogThread.values()){
				for(LogTraceMethod met: th.getLogMethods()){
					if(!met.hasEnded()){
						methods.add(met);
					}
				}
			}
			return (ArrayList<LogTraceMethod>) methods;
		}
			
	// Comparator used for sorting
	static class CustomComparator implements Comparator<LogTraceMethod> {
	    @Override
	    public int compare(LogTraceMethod m1, LogTraceMethod m2) {
	    	double m1Runtime = m1.getRuntime();
	        double m2Runtime = m2.getRuntime();
	        return Double.compare(m1Runtime, m2Runtime)*-1;
	    }
	}
	
 }
	












