package com.anand.salesforce.log.utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.anand.salesforce.log.operations.DMLOperation;
import com.anand.salesforce.log.operations.Operation;
import com.anand.salesforce.log.operations.DatabaseOperation;
import com.anand.salesforce.log.operations.Operation.EntryOrExit;
import com.anand.salesforce.log.operations.TriggerExecutionOperation;

import dto.LogStatistics;
public class SFDCLogParser {
	public static final SimpleDateFormat DATE_FORMAT= new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
	public static void main(String args[]) throws Exception{
		SFDCLogParser parser = new SFDCLogParser();
		ObjectMapper mapper = new ObjectMapper();
		Operation opr = parser.parseLogFile(new FileReader(args[0]),Integer.parseInt(args[1]));
		
		//mapper.writeValue(System.out, opr);
		LogStatistics logStats = new LogStatistics();
		parser.getDatabaseOperations(opr,logStats);
		mapper.writeValue(System.out, logStats);
		
	}
	

	public Operation parseLogFile(File logFile,Integer timeThreshold) throws Exception{
		return parseLogFile(new FileReader(logFile),timeThreshold);
		
	}	
	public Operation parseLogFile(String logData,Integer timeThreshold) throws Exception{
		return parseLogFile(new StringReader(logData),timeThreshold);
	}	

	public Operation parseLogFile(Reader readerIn,Integer timeThreshold) throws Exception{
	
		
		Stack<Operation> oprStack1 = new Stack<Operation>();
		BufferedReader reader = new BufferedReader(readerIn);
		Operation currOp,prevOp,parentOpr;
		String currLine;
		currLine = reader.readLine();
		long execStartTime=-1;
		try{
			while((currLine = reader.readLine())!=null){
				if(currLine.indexOf('|')>=0){
					currOp = OperationFactory.createOperationForLogLine(execStartTime,currLine);
					if(currOp!=null){
						if(currOp.getEventType().equalsIgnoreCase("EXECUTION") && currOp.getEventSubType()==Operation.EntryOrExit.STARTED){
							execStartTime=currOp.getStartTime();
						}
						prevOp = oprStack1.isEmpty()?null:oprStack1.pop();
						if(prevOp!=null && prevOp.getEventId().equalsIgnoreCase(currOp.getEventId())){
							
							prevOp.setEndTime(currOp.getStartTime());
							
							//Set the row count for SOQL queries
							if(	currOp.getEventType().equalsIgnoreCase("SOQL") && currOp.getEventSubType() == EntryOrExit.END){
								((DatabaseOperation)prevOp).setRowCount(((DatabaseOperation)currOp).getRowCount());
							}
							if(	prevOp.getElapsedMillis()>=timeThreshold ||
								prevOp.hasOperations() ||
								prevOp.getEventType().equalsIgnoreCase("SOQL") ||
								prevOp.getEventType().equalsIgnoreCase("DML") ||
								prevOp.getEventType().equalsIgnoreCase("EXECUTION") ||
								prevOp instanceof TriggerExecutionOperation){
								//Pop the parent & add it to paren't long running operations
								parentOpr = oprStack1.isEmpty()?null:oprStack1.pop();
								if(parentOpr !=null){
									parentOpr.add(prevOp);
									//Add the parent back to the stack
									oprStack1.push(parentOpr);
								}else if(oprStack1.isEmpty()){
									//In the top most method, make sure you push the 
									//previous operation back into the stack
									oprStack1.push(prevOp);
								}
							}
							
						}else{
							if(prevOp!=null){
								oprStack1.push(prevOp);
							}
							oprStack1.push(currOp);
						}
					}
				}
			}
			
			return oprStack1.pop();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}finally{
			try{
				reader.close();
			}catch(Exception ex){
				
			}
		}
	}
	

	public List<Operation> getFlattenedDataForUI(Operation operation){
		List<Operation> oprList = new ArrayList<Operation>();
		oprList.add(operation);
		if(operation.hasOperations()){
			for(Operation opr : operation.getOperations()){
				if(opr.hasOperations()){
					oprList.addAll(getFlattenedDataForUI(opr));
				}else{
					oprList.add(opr);
				}
			}
		}
		return oprList;
	}

	public void getDatabaseOperations(Operation operation,LogStatistics logStats){
		if(operation.hasOperations()){
			for(Operation opr : operation.getOperations()){
				if(opr.hasOperations()){
					getDatabaseOperations(opr,logStats);
				}
				if(	opr instanceof TriggerExecutionOperation){
					logStats.addToTriggerTime(opr.getElapsedMillis());
					logStats.incrementTriggerCount();
				
				}
				if(	opr.getEventType().equalsIgnoreCase("SOQL") || 
					opr.getEventType().equalsIgnoreCase("DML") ){
					logStats.addDBOperation((DatabaseOperation)opr);
					if(opr.getEventType().equalsIgnoreCase("SOQL")){
						logStats.addToQueryTime(opr.getElapsedMillis());
						logStats.incrementQueryCount();
					}
					if(opr.getEventType().equalsIgnoreCase("DML")){
						logStats.addToDMLTime(opr.getElapsedMillis());
						logStats.incrementDmlObjectCount();
					}
					
				}

			}
		}
	}

}
