package dataload;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public interface DataLoader {

    Dataset<Row> loadDataSet(SparkSession sparkSession);

    Dataset<Row> cleanDataSet(Dataset<Row> dataset);

    Dataset<Row> dropDuplicates(Dataset<Row> dataset);
}
