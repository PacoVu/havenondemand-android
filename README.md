# IODClient Library for Android. V1.0

----
## Overview
IODClient library for Android is a lightweight Java based API, which helps you easily integrate your Android app with HP IDOL OnDemand Services.

IODClient library requires a minimum Android API level 10.

----
## Integrate IODClient into an Android project
1. Download the IODClient library project for Android. 
2. Create a new or open an existing Android project
3. Select the app main folder and click on the File menu then choose Import Module option.
>![](/images/importlibrary1.jpg)
4. Browse to IODClient folder and click OK. The IODClient folder should be created into your project.
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
        compile project(':iodclient')
    
    }

----
## API References
**IODClient(String apiKey, IIODClientCallback callback)**

*Description:* 
* Constructor. Creates and initializes an IODClient object.

*Parameters:*
* apiKey: your developer apikey.
* callback: class that implements the IIODClientCallback interface.

----
**GetRequest(Map\<String,Object\> params, String iodApp, REQ_MODE mode)**

*Description:* 
* Sends a GET request to an IDOL OnDemand API.

*Parameters:*
* params: a HashMap object containing key/value pair parameters to be sent to an IDOL OnDemand API, where the keys are the parameters of an IDOL OnDemand API.

>Note: 

>In the case of a parameter type is an array<>, the key must be defined as "arrays" and the value must be a Map\<String,String\> object with the key is the parameter name and the values separated by commas ",". 
>E.g.:
## 
    Map<String, String> entity_array = new HashMap<String, String>();
    entity_array.put(“entity_type”, “people_eng,places_eng”);
    params.put(“arrays”, entity_array);

* iodApp: a string to identify an IDOL OnDemand API. E.g. "extractentities". Current supported apps are listed in the IODApps class.
* mode [REQ_MODE.SYNC | REQ_MODE.ASYNC]: specifies API call as Asynchronous or Synchronous.

*Return: void.*

**Response:**
* If the mode is “ASYNC”, response will be returned via the requestCompletedWithJobID(String response) callback function.
* If the mode is “SYNC”, response will be returned via the requestCompletedWithContent(String response) callback function.
* If there is an error occurred, the error message will be sent via the onErrorOccurred(String errorMessage) callback function.

----
**PostRequest(Map\<String,Object\> params, String iodApp, REQ_MODE mode)**

*Description:* 
* Sends a POST request to an IDOL OnDemand API.

*Parameters:*
* params: a HashMap object containing key/value pair parameters to be sent to an IDOL OnDemand API, where the keys are the parameters of an IDOL OnDemand API. 

>Note: 

>In the case of a parameter type is an array<>, the key must be defined as "arrays" and the value must be a Map\<String,String\> object with the key is the parameter name and the values separated by commas “,”.
E.g.:
## 
    Map<String, String> entity_array = new HashMap<String, String>();
    entity_array.put(“entity_type”, “people_eng,places_eng”);
    params.put(“arrays”, entity_array);

* iodApp: a string to identify an IDOL OnDemand API. E.g. "ocrdocument". Current supported apps are listed in the IODApps class.
* mode [REQ_MODE.SYNC | REQ_MODE.ASYNC]: specifies API call as Asynchronous or Synchronous.

*Return: void.*

**Response:**
* If the mode is "ASYNC", response will be returned via the requestCompletedWithJobID(String response) callback function.
* If the mode is "SYNC", response will be returned via the requestCompletedWithContent(String response) callback function.
* If there is an error occurred, the error message will be sent via the onErrorOccurred(String errorMessage) callback function.

----
**GetJobResult(String jobID)**

*Description:*
* Sends a request to IDOL OnDemand to retrieve the content identified by the jobID.

**Parameter:**
* jobID: the job ID returned from an IDOL OnDemand API upon an asynchronous call.

**Response:** 
* Response will be returned via the requestCompletedWithContent(String response)

----
## API callback functions
In your class, you will need to inherit the IIODClientCallback interface and implement callback functions to receive responses from the server

    public class MyClass implements IIODClientCallback {
    
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
    public void requestCompletedWithJobID(String response) {}
# 
When you call the GetRequest() or PostRequest() with the SYNC mode, the response will be returned to this callback function. The response is a JSON string containing the actual result of the service.

    @Override
    public void requestCompletedWithContent(String response) {}
# 
If there is an error occurred, the error message will be returned to this callback function.

    @Override
    public void onErrorOccurred(string errorMessage){}

----
## Demo code 1: 

**Call the Entity Extraction API to extract people and places from cnn.com website with a synchronous GET request**

    import com.iod.api.iodclient.IIODClientCallback;
    import com.iod.api.iodclient.IODApps;
    import com.iod.api.iodclient.IODClient;
    
    public class MyActivity extends Activity implements IIODClientCallback {

        IODClient iodClient;
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            
            iodClient = new IODClient("your-apikey", this);
            
            useIODClient();
        }

        private void useIODClient() {
            String iodApp = IODApps.ENTITY_EXTRACTION;
            Map<String, Object> params = new HashMap<String, Object>();
            
            Map<String, String> arrays = new HashMap<String, String>();
            arrays.put("entity_type", "people_eng,places_eng");

            params.put("url", "http://www.cnn.com");
            params.put("arrays", arrays);
            params.put("unique_entities", "true");

            iodClient.GetRequest(params, iodApp, IODClient.REQ_MODE.SYNC);
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

    import com.iod.api.iodclient.IIODClientCallback;
    import com.iod.api.iodclient.IODApps;
    import com.iod.api.iodclient.IODClient;
    
    public class MyActivity extends Activity implements IIODClientCallback {

        IODClient iodClient;
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            
            iodClient = new IODClient("your-apikey", this);
            
            useIODClient();
        }

        private void useIODClient() {
            String iodApp = IODApps.OCR_DOCUMENT;
            Map<String, Object> params = new HashMap<String, Object>();
            
            params.put("file", "path/and/filename");
            params.put("mode", "document_photo");

            iodClient.PostRequest(params, iodApp, IODClient.REQ_MODE.ASYNC);
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
                    iodClient.GetJobResult(jobID);
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

IODClient library uses the Apache httpmime-4.3.2 and httpcore-4.3.2 libraries. For your convenience, the library project included the httpmime-4.3.2.jar and httpcore-4.3.jar files. The dependent components are licensed under the Apache License 2.0. Please see the files called LICENSE.txt and NOTICE.txt for more information.