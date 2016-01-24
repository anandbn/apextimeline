package dto;

import play.data.validation.Constraints.Required;

public class LogFileRequest {
	@Required
	public Integer minRunTime;

	public String logId;

}
