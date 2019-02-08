package org.cts.oneframework.webapicucumber.stepdefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cts.oneframework.webapicucumber.stepdefinition.DefaultStepDefinition;
import org.testng.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import static org.hamcrest.Matchers.equalTo;

public class APIstepdefinition {
	

	RequestSpecification request;
	Response response;
	
	@Given("^BaseUri is available for \"([^\"]*)\"$")
	public void baseuri_is_available_the_myTest(String strUri) {

		String strUriVal=DefaultStepDefinition.currentIterationMap.get().get(strUri);

		RestAssured.baseURI =strUriVal;


		request = RestAssured.given();
	}
	
	@Given("^Proxy Host \"([^\"]*)\" and Port \"([^\"]*)\" is set$")
	public void proxy_Host_and_Port_is_set(String host, String port) {
		RestAssured.proxy(host,Integer.parseInt(port));
		RestAssured.useRelaxedHTTPSValidation("TLS");
	}

	@Given("^BaseUri is \"([^\"]*)\"$")
	public void baseuri_is(String strUri) {

		RestAssured.baseURI =strUri;
		request = RestAssured.given();
	}
	
	@Given("^Static \"([^\"]*)\" information are loaded for the WebApi$")
	public void static_Headers_information_are_loaded_for_the_WebApi(String strHeaders) {

		String lsStaticHEaders=DefaultStepDefinition.currentIterationMap.get().get(strHeaders);
		if(!lsStaticHEaders.isEmpty()) {
			String staticHeaders[]=lsStaticHEaders.split("\\n");
			for(String strHeader:staticHeaders) {
				String Head[]=strHeader.split(":");
				request.header(Head[0],Head[1]);
			}
		}
	}
	
	@Given("^User provides correct headers for ([^\"]*)$")
	public void user_provides_correct_headers_for(String strDynamicHeaders) {

	    if(!strDynamicHeaders.isEmpty()) {
	    
	    }
	}
	
	@Given("^User provides correct values for BasicAuthentication \"([^\"]*)\"$")
	public void user_provides_correct_values_for_BasicAuthentication(String strBasicAuthentication) {

	    PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
	    
		String lsBasicAuthentication=DefaultStepDefinition.currentIterationMap.get().get(strBasicAuthentication);
	    if(!lsBasicAuthentication.isEmpty()) {
	    	String authenticationDetails[]=lsBasicAuthentication.split("\\n");
			for(String filedDetails:authenticationDetails) {
				String filed[]=filedDetails.split(":");
				if(filed[0].contains("user") && !filed[1].isEmpty() )
					authScheme.setUserName(filed[1]);
				if(filed[0].contains("pass") && !filed[1].isEmpty() )
					authScheme.setPassword(filed[1]);
			}
	    	
		    RestAssured.authentication = authScheme;
	    }
	}
	
	
	@When("User performs \"([^\"]*)\" operation with ([^\"]*) with correct values as par ([^\"]*)")
	public void user_performs_operation_with_Param_Param_with_correct_values_as_par(String strOperation, String strListofParam, String strSampleJsonReq) {

	    if(!strOperation.isEmpty()) {
	    	if(strOperation.equalsIgnoreCase("Get") && strListofParam.isEmpty()) {
				response=request.when().get();

	    }
	    	else if ((strOperation.equalsIgnoreCase("Get") && !strListofParam.isEmpty()))
	    	{
	    		String reqParam[]=strListofParam.split("~");
	   			for(String param: reqParam) {
	 				String valParam=DefaultStepDefinition.currentIterationMap.get().get(param);   		
	 				request.param(param, valParam);
				}    		
	   			response=request.when().get();
	    	}
	    	else if(strOperation.equalsIgnoreCase("post")) {
	    		String jsnReqTem="";
	    		if(!strSampleJsonReq.isEmpty()) {
	    			try {
						jsnReqTem = new String(Files.readAllBytes(Paths.get(".\\src\\test\\resources\\data\\"+strSampleJsonReq)));

					} catch (IOException e) {

						e.printStackTrace();
					}
	    		}
	    		
	    		if(!strListofParam.isEmpty()) {
	    			String reqParam[]=strListofParam.split("~");
	    			for(String param: reqParam) {
	    				String valParam=DefaultStepDefinition.currentIterationMap.get().get(param);   		
	    				jsnReqTem=updateJsonRequest(jsnReqTem,param,valParam);
	    			}
	    		}
  
	    		request.body(jsnReqTem);
	    		response=request
	    				.when()
	    				.post();

	    	}
	    	
	    }
	}

	@When("User performs \"([^\"]*)\" operation with")
	public void user_performs_operation_with(String strOperation, Map<String,String> requestFields) {
		if (strOperation.equalsIgnoreCase("Get")) {
	        for (Map.Entry<String, String> field : requestFields.entrySet()) {
	        	request.param(field.getKey(), field.getValue());
	        }
	        response=request.when().get();
		}else if(strOperation.equalsIgnoreCase("post")) {
			JSONObject requestParams = new JSONObject();
	        for (Map.Entry<String, String> field : requestFields.entrySet()) {
	            if(StringUtils.isNumeric(field.getValue())){
	            	requestParams.put(field.getKey(), Integer.parseInt(field.getValue()));
	            }
	            else{
	            	requestParams.put(field.getKey(), field.getValue());
	            }
	        }
	        request.contentType(ContentType.JSON);
	        request.body(requestParams.toString());
	        response=request.when().post();
		}
	}
	
	private String updateJsonRequest(String jsnReqTem, String param, String valParam) {
		
		jsnReqTem=jsnReqTem.replace("\""+param+"\":\"\"", "\""+param+"\":\""+valParam+"\"");

		return jsnReqTem;
	}
	
	ValidatableResponse json;
	
	@Then("Verify the \"([^\"]*)\" in for the Response")
	public void verify_the_in_for_the_Response(String strResponseCode) {
	    int statCode=Integer.parseInt(DefaultStepDefinition.currentIterationMap.get().get(strResponseCode));
	    json = response.then().statusCode(statCode);
	}

	@Then("Verify the \"([^\"]*)\" status code for the Response")
	public void verify_the_status_code_for_the_Response(String strResponseCode) {

	    json = response.then().statusCode(Integer.parseInt(strResponseCode));
	}
	
	@Then("Verify the Expected Value for ([^\"]*) should match with response")
	public void verify_the_Expected_Value_for_Field_Field_should_match_with_response(String strListofResponseFields) {

	    String[] stResponseFields=strListofResponseFields.split("~");
	    for(String responseField:stResponseFields) {
	    	fnVerifyKeyVal(responseField, DefaultStepDefinition.currentIterationMap.get().get(responseField) );
	    }
	}
	
	private void fnVerifyKeyVal(String responseField, String responseFieldVal) {

		if(StringUtils.isNumeric(responseFieldVal)){
	        json.body(responseField, equalTo(Integer.parseInt(responseFieldVal)));
	    }
	    else{
	        json.body(responseField, equalTo(responseFieldVal));
	    }
	}
	
    @Then("response includes the following$")
    public void response_equals(Map<String,String> responseFields){

        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            if(StringUtils.isNumeric(field.getValue())){
                json.body(field.getKey(), equalTo(Integer.parseInt(field.getValue())));
            }
            else{
                json.body(field.getKey(), equalTo(field.getValue()));
            }
        }
    }
    
    @Then("Verify response path has following values$")
    public void verify_response_path_has_following_values(Map<String,String> responseFields) {
        for (Map.Entry<String, String> field : responseFields.entrySet()) {

            if(StringUtils.isNumeric(field.getValue())){
            	
            	Assert.assertEquals(response.jsonPath().getInt(field.getKey()),Integer.parseInt(field.getValue()));

            }
            else{
     	
            	Assert.assertEquals(response.jsonPath().getString(field.getKey()),field.getValue());
            }
        }
    }
    
    @Given("^BaseUri is available for \"([^\"]*)\" with ID from last response$")
	public void baseuri_is_available_the_myTestOrchastrated(String strUri) {
    	
    	String strid= response.jsonPath().getString("_id");

		String strUriVal=DefaultStepDefinition.currentIterationMap.get().get(strUri);

		RestAssured.baseURI =strUriVal+"/"+strid;
		
		

		request = RestAssured.given();
	}

}