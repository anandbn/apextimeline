package com.anand.salesforce.log.operations;


public class ExecutionOperation extends Operation {

	public ExecutionOperation(long execStartTime, String[] logLineTokens) {
        super();
		this.eventId = "ENTRY_POINT";
		this.name="MAIN";
        String[] timeTokens = logLineTokens[0].split(" ");
        this.startTime= Long.valueOf(timeTokens[1].substring(1,timeTokens[1].length()-1));
        this.timeStamp=timeTokens[0];
        String[] eventTokens = logLineTokens[1].split("_");
        this.eventType = eventTokens[0];
        this.eventSubType =Enum.valueOf(EntryOrExit.class, eventTokens[eventTokens.length-1]);
        this.execStartTime=execStartTime;
        
	}
}
