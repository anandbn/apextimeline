package com.anand.salesforce.log.operations;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Operation {
	protected String name;
	protected String eventId;
	protected String timeStamp;
	protected long startTime;
	protected long endTime;
	protected String eventType;
	@JsonIgnore
	protected EntryOrExit eventSubType;
	protected long execStartTime;
	public static enum EntryOrExit{
		ENTRY,EXIT,BEGIN,END,STARTED,FINISHED
	}
	
	protected List<Operation> operations;

	public Operation(){
		
	}
	public Operation(long execStartTime, String[] logLineTokens) {
        super();
        this.eventId = logLineTokens[2];
        String[] timeTokens = logLineTokens[0].split(" ");
        this.startTime= Long.valueOf(timeTokens[1].substring(1,timeTokens[1].length()-1));
        this.timeStamp=timeTokens[0];
        String[] eventTokens = logLineTokens[1].split("_");
        this.eventType = eventTokens[0];
        this.eventSubType =Enum.valueOf(EntryOrExit.class, eventTokens[eventTokens.length-1]);
        this.execStartTime=execStartTime;
        
	}

	public Operation(String eventId, String startTime) {
		this.eventId=eventId;
        this.startTime= Long.valueOf(startTime);
	}
	

	public void setEndTime(String endTime) {
		this.endTime = Long.valueOf(endTime);
	}

	@JsonProperty
	public long getElapsedMillis() {
		return (this.endTime - this.startTime) / 1000000;
	}

	
	@JsonProperty
	public long getElapsedSinceStart() {
		return (this.endTime - this.execStartTime) / 1000000;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public EntryOrExit getEventSubType() {
		return eventSubType;
	}
	public void setEventSubType(EntryOrExit eventSubType) {
		this.eventSubType = eventSubType;
	}
	public long getExecStartTime() {
		return execStartTime;
	}
	public void setExecStartTime(long execStartTime) {
		this.execStartTime = execStartTime;
	}
	public boolean add(Operation arg0) {
		if(operations==null){
			operations = new ArrayList<Operation>();
		}
		return operations.add(arg0);
	}
	
	public boolean hasOperations(){
		return !(operations==null || operations.isEmpty());
	}

	@Override
	public String toString() {
		return "Operation [type="+this.eventType+", eventId=" + eventId
				+ ", execStartTime=" + execStartTime+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", getElapsedMillis()=" + getElapsedMillis() + ", getElapsedSinceStart()=" + getElapsedSinceStart() 
				+ ", longOperations = "+(operations==null?0:operations.size())+"]";
	}

}
