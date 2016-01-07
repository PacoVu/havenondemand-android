package hod.response.parser;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vuv on 9/24/2015.
 */
public class HODResponseParser {
    private List<HODErrorObject> errors;

    public HODResponseParser()
    {
        errors = new ArrayList<HODErrorObject>();
    }
    private void ResetErrorList()
    {
        errors.clear();
    }
    private void AddError(HODErrorObject error)
    {
        errors.add(error);
    }
    public List<HODErrorObject> GetLastError()
    {
        return errors;
    }
    public String ParseJobID(String jsonString)
    {
        String jobID = "";
        try {
            JSONObject mainObject = new JSONObject(jsonString);
            if (!mainObject.isNull("jobID")) {
                jobID = mainObject.getString("jobID");
            } else {
                HODErrorObject error = new HODErrorObject();
				error.error = HODErrorCode.INVALID_HOD_RESPONSE;
				error.reason = "Unrecognized response from HOD";
				this.AddError(error);
            }
        } catch (Exception ex) {
            HODErrorObject error = new HODErrorObject();
            error.error = HODErrorCode.UNKNOWN_ERROR;
            error.reason = "Unknown error";
            error.detail = ex.getMessage();
            this.AddError(error);
            return jobID;
        }
        return jobID;
    }
    public Object ParseStandardResponse(String hodApp, String jsonStr)
    {
        this.ResetErrorList();
        Object obj = null;
        String result = jsonStr;
        if (jsonStr.length() == 0) {
            HODErrorObject error = new HODErrorObject();
            error.error = HODErrorCode.INVALID_HOD_RESPONSE;
            error.reason = "Empty response";
            this.AddError(error);
            return obj;
        }
        try {
            JSONObject mainObject = new JSONObject(jsonStr);
            JSONObject actions = null;
            if (!mainObject.isNull("actions")) {
                actions = mainObject.getJSONArray("actions").getJSONObject(0);
                String action = actions.getString("action");
                String status = actions.getString("status");
                if (status.equals("finished"))
                    result = actions.getJSONObject("result").toString();
                else if (status.equals("failed")) {
                    JSONArray errorArr = actions.getJSONArray("errors");
                    int count = errorArr.length();
                    for (int i = 0; i < count; i++) {
                        JSONObject error = errorArr.getJSONObject(i);
                        HODErrorObject err = new HODErrorObject();
                        err.error = error.getInt("error");
                        err.reason = error.getString("reason");
                        if (!error.isNull("detail"))
                            err.detail = error.getString("detail");
                        this.AddError(err);
                    }
                    return null;
                } else if (status.equals("queued")) {
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.QUEUED;
                    error.reason = "Task is in queue.";
                    error.jobID = mainObject.getString("jobID");
                    this.AddError(error);
                    return null;
                } else if (status.equals("in progress")) {
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.IN_PROGRESS;
                    error.reason = "Task is in progress.";
                    error.jobID = mainObject.getString("jobID");
                    this.AddError(error);
                    return null;
                } else {
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.UNKNOWN_ERROR;
                    error.reason = "Unknown error";
                    error.jobID = mainObject.getString("jobID");
                    this.AddError(error);
                    return null;
                }
            }
            Gson gsonObj = new Gson();
            switch (hodApp) {
                case SupportedApps.RECOGNIZE_SPEECH:
                    obj = gsonObj.fromJson(result, RecognizeSpeechResponse.class);
                    break;
                case SupportedApps.CANCEL_CONNECTOR_SCHEDULE:
                    obj = gsonObj.fromJson(result, CancelConnectorResponse.class);
                    break;
                case SupportedApps.CONNECTOR_HISTORY:
                    obj = gsonObj.fromJson(result, ConnectorHistoryResponse.class);
                    break;
                case SupportedApps.CONNECTOR_STATUS:
                    obj = gsonObj.fromJson(result, ConnectorStatusResponse.class);
                    break;
                case SupportedApps.CREATE_CONNECTOR:
                    obj = gsonObj.fromJson(result, CreateConnectorResponse.class);
                    break;
                case SupportedApps.DELETE_CONNECTOR:
                    obj = gsonObj.fromJson(result, DeleteConnectorResponse.class);
                    break;
                case SupportedApps.RETRIEVE_CONFIG_ATTR:
                    obj = gsonObj.fromJson(result, RetrieveConnectorConfigurationAttributeResponse.class);
                    break;
                case SupportedApps.RETRIEVE_CONFIG_FILE:
                    obj = gsonObj.fromJson(result, RetrieveConnectorConfigurationFileResponse.class);
                    break;
                case SupportedApps.START_CONNECTOR:
                    obj = gsonObj.fromJson(result, StartConnectorResponse.class);
                    break;
                case SupportedApps.STOP_CONNECTOR:
                    obj = gsonObj.fromJson(result, StopConnectorResponse.class);
                    break;
                case SupportedApps.UPDATE_CONNECTOR:
                    obj = gsonObj.fromJson(result, UpdateConnectorResponse.class);
                    break;
                case SupportedApps.EXPAND_CONTAINER:
                    obj = gsonObj.fromJson(result, ExpandContainerResponse.class);
                    break;
                case SupportedApps.STORE_OBJECT:
                    obj = gsonObj.fromJson(result, StoreObjectResponse.class);
                    break;
                case SupportedApps.VIEW_DOCUMENT:
                    obj = gsonObj.fromJson(result, ViewDocumentResponse.class);
                    break;
                case SupportedApps.GET_COMMON_NEIGHBORS:
                    obj = gsonObj.fromJson(result, GetCommonNeighborsResponse.class);
                    break;
                case SupportedApps.GET_NEIGHBORS:
                    obj = gsonObj.fromJson(result, GetNeighborsResponse.class);
                    break;
                case SupportedApps.GET_NODES:
                    obj = gsonObj.fromJson(result, GetNodesResponse.class);
                    break;
                case SupportedApps.GET_SHORTEST_PATH:
                    obj = gsonObj.fromJson(result, GetShortestPathResponse.class);
                    break;
                case SupportedApps.GET_SUB_GRAPH:
                    obj = gsonObj.fromJson(result, GetSubgraphResponse.class);
                    break;
                case SupportedApps.SUGGEST_LINKS:
                    obj = gsonObj.fromJson(result, SuggestLinksResponse.class);
                    break;
                case SupportedApps.SUMMARIZE_GRAPH:
                    obj = gsonObj.fromJson(result, SummarizeGraphResponse.class);
                    break;
                case SupportedApps.OCR_DOCUMENT:
                    obj = gsonObj.fromJson(result, OCRDocumentResponse.class);
                    break;
                case SupportedApps.RECOGNIZE_BARCODES:
                    obj = gsonObj.fromJson(result, BarcodeRecognitionResponse.class);
                    break;
                case SupportedApps.DETECT_FACES:
                    obj = gsonObj.fromJson(result, FaceDetectionResponse.class);
                    break;
                case SupportedApps.RECOGNIZE_IMAGES:
                    obj = gsonObj.fromJson(result, ImageRecognitionResponse.class);
                    break;
                case SupportedApps.PREDICT:
                    obj = gsonObj.fromJson(result, PredictResponse.class);
                    break;
                case SupportedApps.RECOMMEND:
                    obj = gsonObj.fromJson(result, RecommendResponse.class);
                    break;
                case SupportedApps.TRAIN_PREDICTOR:
                    obj = gsonObj.fromJson(result, TrainPredictionResponse.class);
                    break;
                case SupportedApps.CREATE_QUERY_PROFILE:
                    obj = gsonObj.fromJson(result, CreateQueryProfileResponse.class);
                    break;
                case SupportedApps.DELETE_QUERY_PROFILE:
                    obj = gsonObj.fromJson(result, DeleteQueryProfileResponse.class);
                    break;
                case SupportedApps.RETRIEVE_QUERY_PROFILE:
                    obj = gsonObj.fromJson(result, RetrieveQueryProfileResponse.class);
                    break;
                case SupportedApps.UPDATE_QUERY_PROFILE:
                    obj = gsonObj.fromJson(result, UpdateQueryProfileResponse.class);
                    break;
                case SupportedApps.FIND_RELATED_CONCEPTS:
                    obj = gsonObj.fromJson(result, FindRelatedConceptsResponse.class);
                    break;
                case SupportedApps.AUTO_COMPLETE:
                    obj = gsonObj.fromJson(result, AutoCompleteResponse.class);
                    break;
                case SupportedApps.EXTRACT_CONCEPTS:
                    obj = gsonObj.fromJson(result, ConceptExtractionResponse.class);
                    break;
                case SupportedApps.EXPAND_TERMS:
                    obj = gsonObj.fromJson(result, ExpandTermsResponse.class);
                    break;
                case SupportedApps.HIGHLIGHT_TEXT:
                    obj = gsonObj.fromJson(result, HighlightTextResponse.class);
                    break;
                case SupportedApps.IDENTIFY_LANGUAGE:
                    obj = gsonObj.fromJson(result, LanguageIdentificationResponse.class);
                    break;
                case SupportedApps.ANALYZE_SENTIMENT:
                    obj = gsonObj.fromJson(result, SentimentAnalysisResponse.class);
                    break;
                case SupportedApps.TOKENIZE_TEXT:
                    obj = gsonObj.fromJson(result, TextTokenizationResponse.class);
                    break;
                case SupportedApps.ADD_TO_TEXT_INDEX:
                    obj = gsonObj.fromJson(result, AddToTextIndexResponse.class);
                    break;
                case SupportedApps.CREATE_TEXT_INDEX:
                    obj = gsonObj.fromJson(result, CreateTextIndexResponse.class);
                    break;
                case SupportedApps.DELETE_TEXT_INDEX:
                    obj = gsonObj.fromJson(result, DeleteTextIndexResponse.class);
                    break;
                case SupportedApps.DELETE_FROM_TEXT_INDEX:
                    obj = gsonObj.fromJson(result, DeleteFromTextIndexResponse.class);
                    break;
                case SupportedApps.INDEX_STATUS:
                    obj = gsonObj.fromJson(result, IndexStatusResponse.class);
                    break;
                case SupportedApps.LIST_RESOURCES:
                    obj = gsonObj.fromJson(result, ListResourcesResponse.class);
                    break;
                case SupportedApps.RESTORE_TEXT_INDEX:
                    obj = gsonObj.fromJson(result, RestoreTextIndexResponse.class);
                    break;
                default:
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.NONSTANDARD_RESPONSE;
                    error.reason = "Non standard response";
                    error.detail = "Define a custom response object and use the ParseCustomResponse() method.";
                    if (actions != null && !actions.isNull("jobID"))
                        error.jobID = actions.getString("jobID");
                    this.AddError(error);
                    break;
            }
        } catch (Exception ex) {
            HODErrorObject error = new HODErrorObject();
            error.error = HODErrorCode.INVALID_HOD_RESPONSE;
            error.reason = "Unrecognized response from HOD";
            error.detail = ex.getMessage();
            this.AddError(error);
            return null;
        }
        return obj;
    }
    public Object ParseCustomResponse(Class<?> T, String jsonStr)
    {
        Object obj = null;
        String result = jsonStr;
        if (jsonStr.length() == 0) {
            HODErrorObject error = new HODErrorObject();
            error.error = HODErrorCode.INVALID_HOD_RESPONSE;
            error.reason = "Empty response";
            this.AddError(error);
            return obj;
        }
        try {
            JSONObject mainObject = new JSONObject(jsonStr);
            //JSONObject actions = null;
            if (!mainObject.isNull("actions"))
            {
                JSONObject actions = mainObject.getJSONArray("actions").getJSONObject(0);
                String action = actions.getString("action");
                String status = actions.getString("status");
                if (status.equals("finished"))
                    result = actions.getJSONObject("result").toString();
                else if (status.equals("failed")) {
                    JSONArray errorArr = actions.getJSONArray("errors");
                    int count = errorArr.length();
                    for (int i = 0; i < count; i++) {
                        JSONObject error = errorArr.getJSONObject(i);
                        HODErrorObject err = new HODErrorObject();
                        err.error = error.getInt("error");
                        err.reason = error.getString("reason");
                        if (!error.isNull("detail"))
                            err.detail = error.getString("detail");
                        this.AddError(err);
                    }
                    return null;
                } else if (status.equals("queued")) {
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.QUEUED;
                    error.reason = "Task is in queue.";
                    error.jobID = mainObject.getString("jobID");
                    this.AddError(error);
                    return null;
                } else if (status.equals("in progress")) {
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.IN_PROGRESS;
                    error.reason = "Task is in progress.";
                    error.jobID = mainObject.getString("jobID");
                    this.AddError(error);
                    return null;
                }
                else {
                    HODErrorObject error = new HODErrorObject();
                    error.error = HODErrorCode.UNKNOWN_ERROR;
                    error.reason = "Unknown error";
                    error.jobID = mainObject.getString("jobID");
                    this.AddError(error);
                    return null;
                }
            }
            Gson gsonObj = new Gson();
            obj = gsonObj.fromJson(result, T);
        } catch (Exception ex) {
            HODErrorObject error = new HODErrorObject();
            error.error = HODErrorCode.INVALID_HOD_RESPONSE;
            error.reason = "Unrecognized response from HOD";
            error.detail = ex.getMessage();
            this.AddError(error);
        }
        return obj;
    }
}
