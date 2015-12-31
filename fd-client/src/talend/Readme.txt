1. Version:TOS_DI-20150908_1633-V6.0.1
	   extenal link: (SourceForge.net)

2. FD (FlockData)

3. Project Description:

4. Code Snippet:
	try{
  	ImportProfile importProfile = ClientConfiguration.getImportParams(context.Param1);
  
 	 //Sample Header data from mike
 	  String[] headers = new String[]{"Title", "Tag", "TagVal", "ValTag", "Origin",  	"Year", "Gold Medals", "Category", "xRef"};
      
   	 //Sample actual data from mike
          String[] data = new String[]{"TitleTests", "TagName", "Gold", "8", "New Zealand",  	"2008", "12", null, "qwerty" };
    
	Map<String, Object> jsonData =  TransformationHelper.convertToMap(headers, data,  	importProfile);
     
	System.out.println("jsonData size ============== "  +jsonData.size()+ "\n"  	+"jsonData ============== " + jsonData);
	
	// Calling entityInputBean
  	EntityInputBean eib = routines.EntityRoutine.transformToEntity(jsonData,  	importProfile);
  	System.out.println("eib======================" +eib);
 
 	}	catch(Exception e){
  		System.out.println("Exception=======================" +e.getMessage());
    	throw e;
   		}

5. File Last Edited : 12/31/2015

6. What you need to run or use it :

	

7.What you need to know to get it going :

	Context	: Repository/Context/FDContext
	Routine : Repository/Code/FDContext
	job	: Repository/JobDesing/FDataTranafor

8. Configuration Instructions:
	Param1 : "C:/fd/column-parsing.json"
	Param2 : "C:/fd/pac.txt"
	Param2 : "C:/fd/contentprofile.json"

9. Installation Instructions :
	
	Below is the link for downloading Talend new version as well as older versions. 
	
	Link : http://sourceforge.net/projects/talend-studio/files/Talend%20Open%20Studio/

10. How to Run Programs :

	a) Run from Talend :

		Once the Job is Created,Then Click on Run Button under the Run tab Component (or press F6 Function Key).

	b) Run From Command Line :
	 
		To Run The job from command line First we need to create Context Parameters. Below is the Link To Create context parameters in talend.
	
	Link :	http://www.robertomarchetto.com/how_to_parameter_values_talend_job_command_line

		After created the context parameters we need to Export the Job To generate .bat file. 
		(Right Clock on jobname -->Build job-->Browse location to save file --> check the checkbox of extract the zip file --> Finish.)

		open cmd.exe --> follow below steps. 
		 For example with the command:

		JobTest_run.bat --context_param ParamA=ValueA --context_param ParamB=ValueB

11. Sample Data :
	
	Path :	D:\ForkSample\flockdata.org\fd-client\src\talend\data

12. Instruction to grab the latest code and detailed instructions to build it (or quick overview and "Read INSTALL")

	Path :	https://github.com/hgurramk/flockdata.org/tree/master/fd-client/src/talend
		
13.Contact Information :
	Name	: Hem Naidu
	Email	: hnaidu@htconsultancy.us
	