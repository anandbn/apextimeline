package com.anand.salesforce.log.operations;

public class QueryOperation extends DatabaseOperation {

	public QueryOperation(long execStartTime, String[] logLineTokens) {
		super(execStartTime,logLineTokens);
		this.operationType="Query";
		if(this.eventSubType==EntryOrExit.BEGIN){
			this.name = logLineTokens[4];
		}
		if(this.eventSubType==EntryOrExit.END){
			String rowToken = logLineTokens[3];
			this.rowCount = Integer.parseInt(rowToken.substring(rowToken.indexOf(':')+1));
		}
	}

}
