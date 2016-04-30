package hod.response.parser;

import java.util.List;

/**
 * Created by vanphongvu on 3/29/16.
 */
public class TrendAnalysisResponse {
    List<TrendCollection> trend_collections;
    public class TrendCollection
    {
        List<Trend> trends;
    }
    public class Trend
    {
        String trend;
        Double measure_percentage_main_group;
        Double measure_value_main_group;
        String main_trend;
        Double score;
        Double measure_percentage_compared_group;
        List<Category> measure;
        List<Category> category;
    }
    class Category
    {
        String column;
        String value;
    }
}
