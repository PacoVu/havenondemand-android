# HODClient Library for Android. V2.0

----
## Overview
HODClient library for Android is a lightweight Java based API, which helps you easily integrate your Android app with HP Haven OnDemand Services.

HODClient V2.0 supports bulk input (source inputs can be an array) where an HOD API is capable of doing so.

HODClient library requires a minimum Android API level 10.

----
## Integrate HODClient into an Android project
1. Download the HODClient library project for Android. 
2. Create a new or open an existing Android project
3. Select the app main folder and click on the File menu then choose Import Module option.
>![](/images/importlibrary1.jpg)
4. Browse to HODClient folder and click OK. The HODClient folder should be created into your project.
![](/images/importlibrary2.jpg)
5. Open the main project build.gradle and add packaging options and the dependency as follows:
    
    android {

        packagingOptions {
            exclude 'META-INF/DEPENDENCIES'
            exclude 'META-INF/NOTICE'
            exclude 'META-INF/LICENSE'
            exclude 'META-INF/LICENSE.txt'
            exclude 'META-INF/NOTICE.txt'
            exclude 'META-INF/ASL2.0'
        }
    
    }
    
    dependencies {

        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile project(':hodclient')
    
    }

----
## API References
**Constructor**

    HODClient(String apiKey, IHODClientCallback callback)

*Description:* 

* Constructor. Creates and initializes an HODClient object.

*Parameters:*

* apiKey: your developer apikey.
* callback: class that implements the IHODClientCallback interface.

*Example code:*
## 
    
    import com.hod.api.hodclient.HODClient;
    import com.hod.api.hodclient.IHODClientCallback;

    public class MyActivity extends Activity implements IHODClientCallback 
    {
        HODClient hodClient = new HODClient("your-api-key", this);
        
	@Override
        public void requestCompletedWithJobID(String response){ }
        
	@Override
        public void requestCompletedWithContent(String response){ }
    	
        @Override
        public void onErrorOccurred(String errorMessage){ }
    }


----
**Function GetRequest**

    void GetRequest(Map<String,Object> params, String hodApp, REQ_MODE mode)

*Description:*
 
* Sends a HTTP GET request to call a Haven OnDemand API.

*Parameters:*

* params: a HashMap object containing key/value pair parameters to be sent to a Haven OnDemand API, where the keys are the parameters of that API.

>Note: In the case of a parameter type is an array<>, the value must be defined as a List<String> object. 

>E.g.:

```
Map<String, Object> params = new HashMap<String, Object>();
List<String> urls = new ArrayList<String>();
urls.add("http://www.cnn.com");
urls.add("http://www.bbc.com");
params.put("url", urls);
params.put("unique_entities", "true");

List<String> entities = new ArrayList<String>();
entities.add("people_eng");
entities.add("places_eng");
params.put("entity_type", entities);
```

* hodApp: a string to identify a Haven OnDemand API. E.g. "extractentities". Current supported apps are listed in the HODApps class.
* mode [REQ_MODE.SYNC | REQ_MODE.ASYNC]: specifies API call as Asynchronous or Synchronous.

*Response:*

* If the mode is "ASYNC", response will be returned via the requestCompletedWithJobID(String response) callback function.
* If the mode is "SYNC", response will be returned via the requestCompletedWithContent(String response) callback function.
* If there is an error occurred, the error message will be sent via the onErrorOccurred(String errorMessage) callback function.

*Example code:*
## 
    // Call the Entity Extraction API to find people and places from CNN website

    String hodApp = HODApps.ENTITY_EXTRACTION;
    Map<String,Object> params =  new HashMap<String,Object>();
    params.put("url", "http://www.cnn.com");
    List<String> entities = new ArrayList<String>();
    entities.add("people_eng");
    entities.add("places_eng");
    params.put("entity_type", entities);
    hodClient.GetRequest(params, hodApp, HODClient.REQ_MODE.SYNC);

----
**Function PostRequest**

    void PostRequest(Map<String,Object> params, String hodApp, REQ_MODE mode)

*Description:* 

* Sends a HTTP POST request to call a Haven OnDemand API.

*Parameters:*

* params: a HashMap object containing key/value pair parameters to be sent to a Haven OnDemand API, where the keys are the parameters of that API. 

>Note: In the case of a parameter type is an array<>, the value must be defined as a List<String> object. 

>E.g.:

```
Map<String, Object> params = new HashMap<String, Object>();
List<String> urls = new ArrayList<String>();
urls.add("http://www.cnn.com");
urls.add("http://www.bbc.com");
params.put("url", urls);
params.put("unique_entities", "true");

List<String> entities = new ArrayList<String>();
entities.add("people_eng");
entities.add("places_eng");
params.put("entity_type", entities);
```

* hodApp: a string to identify a Haven OnDemand API. E.g. "ocrdocument". Current supported apps are listed in the IODApps class.
* mode [REQ_MODE.SYNC | REQ_MODE.ASYNC]: specifies API call as Asynchronous or Synchronous.

*Response:*

* If the mode is "ASYNC", response will be returned via the requestCompletedWithJobID(String response) callback function.
* If the mode is "SYNC", response will be returned via the requestCompletedWithContent(String response) callback function.
* If there is an error occurred, the error message will be sent via the onErrorOccurred(String errorMessage) callback function.

*Example code:*
## 
    // Call the OCR Document API to scan text from an image file

    String hodApp = HODApps.OCR_DOCUMENT;
    Map<String,Object> params =  new HashMap<String,Object>();
    params.put("file", "full/path/filename.jpg");
    params.put("mode", "document_photo");
    hodClient.PostRequest(params, hodApp, HODClient.REQ_MODE.ASYNC);

----
**Function GetJobResult**

    void GetJobResult(String jobID)

*Description:*

* Sends a request to Haven OnDemand to retrieve content identified by the jobID.

*Parameter:*

* jobID: the job ID returned from a Haven OnDemand API upon an asynchronous call.

*Response:*
 
* Response will be returned via the requestCompletedWithContent(String response)

*Example code:*
## 
    // Parse a JSON string contained a jobID and call the function to get the actual content from Haven OnDemand server

    @Override
    public void requestCompletedWithJobID(String response) 
    { 
        try {
            JSONObject mainObject = new JSONObject(response);
            if (!mainObject.isNull("jobID")) {
                jobID = mainObject.getString("jobID");
                hodClient.GetJobResult(jobID);
            }
        } catch (Exception ex) { }
    }

----
## API callback functions
In your class, you will need to inherit the IHODClientCallback interface and implement callback functions to receive responses from the server

    public class MyClass implements IHODClientCallback {
    
        @Override
        public void requestCompletedWithJobID(String response) {}
    
        @Override
        public void requestCompletedWithContent(String response) {}
    
        @Override
        public void onErrorOccurred(string errorMessage){}
    
    }
# 
When you call the GetRequest() or PostRequest() with the ASYNC mode, the response will be returned to this callback function. The response is a JSON string containing the jobID.

    @Override
    public void requestCompletedWithJobID(String response) 
    {
    
    }
# 
When you call the GetRequest() or PostRequest() with the SYNC mode or call the GetJobReslt() function, the response will be returned to this callback function. The response is a JSON string containing the actual result of the service.

    @Override
    public void requestCompletedWithContent(String response) 
    {
    
    }
# 
If there is an error occurred, the error message will be returned to this callback function.

    @Override
    public void onErrorOccurred(string errorMessage)
    {
    
    }

----
## Demo code 1: 

**Call the Entity Extraction API to extract people and places from bbc.com and cnn.com websites with a synchronous GET request**

    import com.hod.api.hodclient.IHODClientCallback;
    import com.hod.api.hodclient.HODApps;
    import com.hod.api.hodclient.HODClient;
    
    public class MyActivity extends Activity implements IHODClientCallback {

        HODClient hodClient;
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            
            hodClient = new HODClient("your-apikey", this);
            
            useHODClient();
        }

        private void useHODClient() {
            String hodApp = HODApps.ENTITY_EXTRACTION;
            Map<String, Object> params = new HashMap<String, Object>();
            
            List<String> urls = new ArrayList<String>();
            urls.add("http://www.cnn.com");
            urls.add("http://www.bbc.com");
            params.put("url", urls);
            params.put("unique_entities", "true");
    
            List<String> entities = new ArrayList<String>();
            entities.add("people_eng");
            entities.add("places_eng");
            params.put("entity_type", entities);
            
            hodClient.GetRequest(params, hodApp, HODClient.REQ_MODE.SYNC);
        }
        
        // implement callback functions
        @Override
        public void requestCompletedWithContent(String response) { 
            try {
                JSONObject mainObject = new JSONObject(response);
                JSONArray entitiesArray = mainObject.getJSONArray("entities");
                int count = entitiesArray.length();
                int i = 0;
                String people = "";
                String places = "";

                if (count > 0) {
                    for (i = 0; i < count; i++) {
                        JSONObject entity = entitiesArray.getJSONObject(i);
                        String type = entity.getString("type");
                        if (type.equals("places_eng")) {
                            places += entity.getString("normalized_text") + "\n";
                            // parse any other interested information about a place
                        } else if (type.equals("people_eng")) {
                            people += entity.getString("normalized_text");
                            // parse any other interested information about a place
                        }
                    }
                }
            } catch (Exception ex) { 
                // handle exception
            }	
        }
        
        @Override
        public void onErrorOccurred(String errorMessage) { 
            // handle error if any
        }
    }

----

## Demo code 2:
 
**Call the OCR Document API to recognize text from an image with an asynchronous POST request**

    import com.hod.api.hodclient.IHODClientCallback;
    import com.hod.api.hodclient.HODApps;
    import com.hod.api.hodclient.HODClient;
    
    public class MyActivity extends Activity implements IHODClientCallback {

        HODClient hodClient;
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            
            hodClient = new HODClient("your-apikey", this);
            
            useHODClient();
        }

        private void useHODClient() {
            String hodApp = HODApps.OCR_DOCUMENT;
            Map<String, Object> params = new HashMap<String, Object>();
            
            params.put("file", "path/and/filename");
            params.put("mode", "document_photo");

            hodClient.PostRequest(params, hodApp, HODClient.REQ_MODE.ASYNC);
        }
        
        // implement delegated functions
        
        /**************************************************************************************
        * An async request will result in a response with a jobID. We parse the response to get
        * the jobID and send a request for the actual content identified by the jobID.
        **************************************************************************************/ 
        @Override
        public void requestCompletedWithJobID(String response) { 
            try {
                JSONObject mainObject = new JSONObject(response);
                if (!mainObject.isNull("jobID")) {
                    jobID = mainObject.getString("jobID");
                    hodClient.GetJobResult(jobID);
                }
            } catch (Exception ex) {
                ;//HandleException(response);
            }
        }

        @Override
        public void requestCompletedWithContent(String response) { 
            try {
                JSONObject mainObject = new JSONObject(response);
                JSONArray textBlockArray = mainObject.getJSONArray("actions");
                int count = textBlockArray.length();
                String recognizedText = "";
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        JSONObject actions = textBlockArray.getJSONObject(i);
                        JSONObject result = actions.getJSONObject("result");
                        if (!result.isNull("text_block")) {
                            JSONArray textArray = result.getJSONArray("text_block");
                            count = textArray.length();
                            if (count > 0) {
                                for (int n = 0; n < count; n++) {
                                    JSONObject texts = textArray.getJSONObject(n);
                                    recognizedText += texts.getString("text");
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) { 
                // handle exception
            }	
        }
        
        @Override
        public void onErrorOccurred(String errorMessage) { 
            // handle error if any
        }
    }

----
## License
Licensed under the MIT License.

HODClient library uses the Apache httpmime-4.3.2 and httpcore-4.3.2 libraries. For your convenience, the library project included the httpmime-4.3.2.jar and httpcore-4.3.jar files. The dependent components are licensed under the Apache License 2.0. Please see the files called LICENSE.txt and NOTICE.txt for more information.