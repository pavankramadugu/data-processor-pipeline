package dataload;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DataLoaderImpl implements DataLoader {


    @Override
    public Dataset<Row> loadDataSet(SparkSession sparkSession) {

        Dataset<Row> dataset = sparkSession.read()
                .option("header", "true")
                .option("numPartitions ", 5)
                .csv("products.csv");

        return cleanDataSet(dataset);
    }

    @Override
    public Dataset<Row> cleanDataSet(Dataset<Row> dataset) {

        return dataset.na().drop();
    }

    @Override
    public Dataset<Row> dropDuplicates(Dataset<Row> dataset) {

        return dataset.dropDuplicates("sku");
    }
}
