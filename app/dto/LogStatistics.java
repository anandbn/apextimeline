package dto;

import java.util.ArrayList;
import java.util.List;

import com.anand.salesforce.log.operations.DatabaseOperation;

public class LogStatistics {
	private long totalSOQLTime;
	private long totalDMLTime;
	private long totalTriggerTime;
	private int queryCount;
	private int dmlObjectCount;
	private int triggerCount;
	private List<DatabaseOperation> dbOperations;

	public long addToQueryTime(long elapsedTime){
		return (totalSOQLTime+=elapsedTime);
	}
	public long addToDMLTime(long elapsedTime){
		return (totalDMLTime+=elapsedTime);
	}
	public long addToTriggerTime(long elapsedTime){
		return (totalTriggerTime+=elapsedTime);
	}
	
	public int incrementQueryCount(){
		return ++queryCount;
	}
	public int incrementDmlObjectCount(){
		return ++dmlObjectCount;
	}
	public int incrementTriggerCount(){
		return ++triggerCount;
	}
	
	public void addDBOperation(DatabaseOperation dbOp){
		if(this.dbOperations==null){
			this.dbOperations=new ArrayList<DatabaseOperation>();
		}
		this.dbOperations.add(dbOp);
		
	}
	public long getTotalSOQLTime() {
		return totalSOQLTime;
	}
	public void setTotalSOQLTime(long totalSOQLTime) {
		this.totalSOQLTime = totalSOQLTime;
	}
	public long getTotalDMLTime() {
		return totalDMLTime;
	}
	public void setTotalDMLTime(long totalDMLTime) {
		this.totalDMLTime = totalDMLTime;
	}
	public long getTotalTriggerTime() {
		return totalTriggerTime;
	}
	public void setTotalTriggerTime(long totalTriggerTime) {
		this.totalTriggerTime = totalTriggerTime;
	}
	public int getQueryCount() {
		return queryCount;
	}
	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}
	public int getDmlObjectCount() {
		return dmlObjectCount;
	}
	public void setDmlObjectCount(int dmlObjectCount) {
		this.dmlObjectCount = dmlObjectCount;
	}
	public int getTriggerCount() {
		return triggerCount;
	}
	public void setTriggerCount(int triggerCount) {
		this.triggerCount = triggerCount;
	}
	public List<DatabaseOperation> getDbOperations() {
		return dbOperations;
	}
	public void setDbOperations(List<DatabaseOperation> dbOperations) {
		this.dbOperations = dbOperations;
	}
	
}
