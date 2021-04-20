package datawrite;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Properties;

public interface DataWriter {

    void productUpsert(Properties connectionProperties, Dataset<Row> dataset);

    void aggregateUpsert(Properties connectionProperties, Dataset<Row> dataset);

    void dbWrite(Properties connectionProperties, Dataset<Row> dataset, String tableName);
}
