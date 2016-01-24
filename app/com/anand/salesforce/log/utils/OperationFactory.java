package com.anand.salesforce.log.utils;

import com.anand.salesforce.log.operations.DMLOperation;
import com.anand.salesforce.log.operations.ExecutionOperation;
import com.anand.salesforce.log.operations.MethodOperation;
import com.anand.salesforce.log.operations.Operation;
import com.anand.salesforce.log.operations.QueryOperation;
import com.anand.salesforce.log.operations.TriggerExecutionOperation;

public class OperationFactory {

	public static Operation createOperationForLogLine(long execStartTime,String logLine){
	    String[] tokens = logLine.split("\\|");
	    if(tokens[1].startsWith("METHOD_")){
	    	return new MethodOperation(execStartTime,tokens);
	    }else if(tokens[1].startsWith("SOQL_EXECUTE_")){
	    	return new QueryOperation(execStartTime,tokens);
	    }else if(tokens[1].startsWith("DML_")){
	    	return new DMLOperation(execStartTime,tokens);
	    }else if(tokens[1].startsWith("EXECUTION_")){
	    	return new ExecutionOperation(execStartTime,tokens);
	    }else if(tokens[1].startsWith("CODE_UNIT_")){
	    	//Check if this is a trigger
	    	if(tokens[tokens.length-1].indexOf("trigger")>-1){
	    		//this is a trigger code execution
	    		return new TriggerExecutionOperation(execStartTime,tokens);
	    	}
	    }
	    return null;
		
	}
}
