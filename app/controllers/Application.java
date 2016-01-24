package controllers;

import javax.inject.Inject;

import play.*;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.*;

import com.anand.salesforce.log.utils.SFDCLogParser;
import com.anand.salesforce.log.operations.DatabaseOperation;
import com.anand.salesforce.log.operations.Operation;
import com.anand.salesforce.log.operations.TriggerExecutionOperation;

import dto.LogFileRequest;
import dto.LogStatistics;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http.*;


public class Application extends Controller {

    private static final String GET_LOGS_URL_BASE= "/services/data/v29.0/";
    private static final String GET_RAW_LOG_URL = "tooling/sobjects/ApexLog/";
    private static final String GET_LOGS_QUERY = "select Id,LogUser.Name,LogUserId,LogLength,Operation,Application,Status," +
                                                 "DurationMilliseconds,StartTime " +
                                                 "from ApexLog order by SystemModstamp desc limit 100";

    @Inject WSClient ws;

    public Result index() {

        
        System.out.println(">>>>host:"+request().host());
        if(     !request().host().equalsIgnoreCase("localhost") && 
                request().getHeader("X-Forwarded-Proto") !=null &&
                !request().getHeader("X-Forwarded-Proto").equalsIgnoreCase("https"))
        {
            return redirect("https://" + request().host() + request().uri());
        }
        
        return ok(index.render(new Boolean(false)));
        

    }

    public Result sampleTimeline() {
        return ok(sample.render());
    }
    public Promise<Result> logs() {
        JsonNode json = request().body().asJson();
        String sessionId = json.findPath("sessionId").asText();
        String instanceUrl = json.findPath("instanceUrl").asText();
        return  ws.url(instanceUrl+GET_LOGS_URL_BASE+"query/")
                  .setQueryParameter("q",GET_LOGS_QUERY)
                  .setHeader("Authorization", "Bearer "+sessionId)
                  .setHeader("Accept","application/json")
                  .get()
                  .map(response -> ok(response.asJson()));
    }

    
    public Promise<Result> userInfo() {
        JsonNode json = request().body().asJson();
        String sessionId = json.findPath("sessionId").textValue();
        String idUrl = json.findPath("idUrl").textValue();
        return  ws.url(idUrl)
                  .setQueryParameter("q",GET_LOGS_QUERY)
                  .setHeader("Authorization", "Bearer "+sessionId)
                  .setHeader("Accept","application/json")
                  .get()
                  .map(response -> ok(response.asJson()));

    }
    

    public Promise<Result> showTimelinev2(String logId) {
        final Integer minRunTime=100;
        String tokenStr = request().cookie("token").value();
        JsonNode sessionToken = Json.parse(tokenStr);
        String sessionId = sessionToken.get("access_token").textValue();
        String instanceUrl = sessionToken.get("instance_url").textValue();
        return ws.url(instanceUrl+GET_LOGS_URL_BASE+GET_RAW_LOG_URL+logId+"/Body/")
                 .setHeader("Authorization", "Bearer "+sessionId)
                 .get()
                 .map(response -> {
                        SFDCLogParser parser = new SFDCLogParser();
                        Operation top = parser.parseLogFile(response.getBody(),minRunTime);
                        if(top!=null){
                            List<Operation> oprList = parser.getFlattenedDataForUI(top);
                            LogStatistics logStats = new LogStatistics();
                            parser.getDatabaseOperations(top,logStats);
                            ObjectMapper mapper =new ObjectMapper();
                            JsonNode json = Json.toJson(oprList);
                            return ok(showTimeLine.render(  json,
                                                            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(top),
                                                            logStats
                                                    )
                            );
                        }else{
                            return ok(index.render(new Boolean(true)));
                            
                        }
                    }
                 );        
    }
    public Result oauthredirect() {
        return ok(oauthredirect.render());
    }
    

    @SuppressWarnings("deprecation")
    public Result showTimeline() throws Exception {
        
        
        Form<LogFileRequest> filledForm = Form.form(LogFileRequest.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest();
        } else {
            final LogFileRequest resource = filledForm.get();
            MultipartFormData body = request().body().asMultipartFormData();
            MultipartFormData.FilePart  resourceFile = body.getFile("logFile");
            if(resourceFile!=null){
                File file = resourceFile.getFile();
                SFDCLogParser parser = new SFDCLogParser();
                Operation top = parser.parseLogFile(file,resource.minRunTime);
                
                if(top!=null){
                    List<Operation> oprList = parser.getFlattenedDataForUI(top);
                    LogStatistics logStats = new LogStatistics();
                    parser.getDatabaseOperations(top,logStats);
                    ObjectMapper mapper =new ObjectMapper();
                    JsonNode json = Json.toJson(oprList);
                    if("application/json".equalsIgnoreCase(request().getHeader("Accept")) ||
                        "text/json".equalsIgnoreCase(request().getHeader("Accept"))){
                        return ok(Json.toJson(top));
                    }else{
                        return ok(showTimeLine.render(json,
                                                  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(top),
                                                  logStats)
                            );
                    }
                }else{
                    if("application/json".equalsIgnoreCase(request().getHeader("Accept")) ||
                            "text/json".equalsIgnoreCase(request().getHeader("Accept"))){
                        Map<String,String> resp = new HashMap<String,String>();
                        resp.put("msg","Could not parse log file. Check if log file is incomplete.");
                        return internalServerError(Json.toJson(resp));
                        
                    }else{
                        return ok(index.render(new Boolean(true)));
                    }
                }   
            }else{  
                return ok(index.render( new Boolean(false)
                                )
                        );
            }
        }
    }
    

}
