package com.anand.salesforce.log.operations;

public class MethodOperation extends Operation {


	public MethodOperation(long execStartTime, String[] logLineTokens) {
		super(execStartTime,logLineTokens);
		//Get the method Name from METHOD_ENTRY
        if(this.eventSubType==EntryOrExit.ENTRY){
        	this.name = logLineTokens[4];
        }

	}

}
