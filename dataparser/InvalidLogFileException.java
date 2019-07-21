package dataparser;


// Exception that is throw when the File cannot be parsed
public class InvalidLogFileException extends Exception {
	public InvalidLogFileException(String s){
		super(s);
	}

}
