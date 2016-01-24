package com.anand.salesforce.log.operations;


public class TriggerExecutionOperation extends DatabaseOperation {
	protected TriggerEvent triggerEvent;
	protected String objectName;
	
	public static enum TriggerEvent{
		BeforeInsert,BeforeUpdate,BeforeDelete,
		AfterInsert,AfterUpdate,AfterDelete,AfterUndelete
	}

	
	public TriggerExecutionOperation(long execStartTime, String[] logLineTokens) {
        super(execStartTime,logLineTokens);
        this.operationType="TRIGGER";
		this.eventId = "TRIGGER";
        String[] timeTokens = logLineTokens[0].split(" ");
        this.startTime= Long.valueOf(timeTokens[1].substring(1,timeTokens[1].length()-1));
        this.timeStamp=timeTokens[0];
        String[] eventTokens = logLineTokens[1].split("_");
        this.eventType = eventTokens[0]+"_"+eventTokens[1];
        this.eventSubType =Enum.valueOf(EntryOrExit.class, eventTokens[eventTokens.length-1]);
		if(this.eventSubType == EntryOrExit.STARTED){
			this.parseAndInitTriggerDetails(logLineTokens[4]);
			this.eventId=this.name;
		}
		if(this.eventSubType == EntryOrExit.FINISHED){
			this.parseAndInitTriggerDetails(logLineTokens[2]);
			this.eventId=this.name;
		}
		
        
	}
	
	private void parseAndInitTriggerDetails(String triggerLogTxt){
		//STARTED: triggerName    on Object        trigger event <eventType>  for [list of objects]
		//Eg.      AccountTrigger on PersonAccount trigger event BeforeInsert for [new]
		String[] triggerTokens = triggerLogTxt.split(" ");
		this.name=triggerTokens[0];
		this.objectName=triggerTokens[2];
		this.triggerEvent=TriggerEvent.valueOf(triggerTokens[5]);
 		
	}

	public TriggerEvent getTriggerEvent() {
		return triggerEvent;
	}

	public void setTriggerEvent(TriggerEvent triggerEvent) {
		this.triggerEvent = triggerEvent;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
}
