package com.anand.salesforce.log.operations;


public class DatabaseOperation extends Operation {

	protected Integer rowCount;
	protected String operationType;
	public DatabaseOperation(long execStartTime, String[] logLineTokens) {
		//12:51:16.935 (935687015)|DML_BEGIN|[209]|Op:Insert|Type:Account|Rows:1
		super(execStartTime,logLineTokens);
	}
	public Integer getRowCount() {
		return rowCount;
	}
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

}
