package com.anand.salesforce.log.operations;


public class DMLOperation extends DatabaseOperation {

	public DMLOperation(long execStartTime, String[] logLineTokens) {
		super(execStartTime,logLineTokens);
		if(this.eventSubType == EntryOrExit.BEGIN){
			this.operationType=logLineTokens[3].substring(3);
		}
        if(this.eventSubType==EntryOrExit.BEGIN){
        	this.name = logLineTokens[4];
        	rowCount = Integer.parseInt(logLineTokens[5].substring(logLineTokens[5].indexOf(':')+1));
        }
	}

}
