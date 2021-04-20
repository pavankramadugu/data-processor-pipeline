package spark;

import org.apache.spark.sql.SparkSession;

public class SparkContext {
    public SparkSession getSparkSession() {

        return SparkSession.builder()
                .master("local[*]")
                .appName("product-data-load-pipeline")
                .getOrCreate();
    }
}
