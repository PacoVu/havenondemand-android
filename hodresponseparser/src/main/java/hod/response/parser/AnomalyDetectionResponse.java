package hod.response.parser;

import java.util.List;

/**
 * Created by vanphongvu on 3/29/16.
 */
public class AnomalyDetectionResponse {
    List<Result> result;

    public class Result
    {
        long row;
        double row_anomaly_score;
        List<Anomaly> anomalies;
    }
    public class Anomaly
    {
        String type;
        double anomaly_score;
        List<Column> columns;
    }
    class Column
    {
    String column;
    String value;
    }
}
