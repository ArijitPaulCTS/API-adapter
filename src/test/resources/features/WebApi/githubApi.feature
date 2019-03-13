@APIAutomationGit
Feature: WebAPI_Automation
  I want to use this template for my feature file to Automate a RestAPI
#dev1
  Background: Datafile reading for the feature
    Given A workbook named "WebAPI_myTest" and sheet named "gitHUB" is read
    And Proxy Host "proxy.cognizant.com" and Port "6050" is set

   # added code        
  @getcall @searchGitIssue @XLdata
  Scenario Outline: Search GitHUB Issues
    Given BaseUri is available for "baseUrl"
    And Static "Headers" information are loaded for the WebApi
    And User provides correct headers for <DynamicHeaders>
    And User provides correct values for BasicAuthentication "BasicAuthentication"
    When User performs "Get" operation with <ListOfParameters> with correct values as par <BaseRequestJson>
    Then Verify the "statuscode" in for the Response
    And Verify response path has following values
        |labels[0].name|design1|
        |state|open|
    Examples: 
      | DynamicHeaders | BaseRequestJson  | ListOfParameters |
      |       abc      |									| 					       |

      
