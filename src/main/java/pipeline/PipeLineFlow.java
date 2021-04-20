package pipeline;

import dataload.DataLoaderImpl;
import datawrite.DataWriterImpl;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import repository.DBSetup;
import spark.SparkContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.apache.spark.sql.functions.count;
import static org.apache.spark.sql.functions.lit;
import static utils.Constants.*;

public class PipeLineFlow {

    private final DataLoaderImpl dataLoader;

    private final DataWriterImpl dataWriter;

    private final DBSetup dbSetup;

    public PipeLineFlow(DataLoaderImpl dataLoader, DataWriterImpl dataWriter, DBSetup dbSetup) {
        this.dataLoader = dataLoader;
        this.dataWriter = dataWriter;
        this.dbSetup = dbSetup;
    }

    public void startDataPipeline() throws IOException {

        Properties connectionProperties = getConnectionProperties();

        SparkContext sparkContext = new SparkContext();

        CompletableFuture.allOf(

                CompletableFuture.runAsync(() -> dbSetup.updateQuery(productsQuery, connectionProperties)),

                CompletableFuture.runAsync(() -> dbSetup.updateQuery(aggregateQuery, connectionProperties))

//                CompletableFuture.runAsync(() -> dbSetup.updateQuery(aggTempQuery, connectionProperties)),
//
//                CompletableFuture.runAsync(() -> dbSetup.updateQuery(prodTempQuery, connectionProperties))

        ).join();

        SparkSession sparkSession = sparkContext.getSparkSession();

        Dataset<Row> dataset = dataLoader.loadDataSet(sparkSession);

        Dataset<Row> nonDupData = dataLoader.dropDuplicates(dataset);

        CompletableFuture.allOf(CompletableFuture.runAsync(() -> {

            dataWriter.productUpsert(connectionProperties, nonDupData);

        }), CompletableFuture.runAsync(() -> {

            Dataset<Row> aggregateData = nonDupData.groupBy("name").agg(count(lit(1))
                    .alias("no. of products"));

            dataWriter.aggregateUpsert(connectionProperties, aggregateData);

        })).join();

//        CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
//
//            writeProducts(nonDupData, connectionProperties);
//
//        }), CompletableFuture.runAsync(() -> {
//
//            writeAggregate(nonDupData, connectionProperties);
//
//        })).join();

        sparkSession.close();
    }
    public void writeProducts(Dataset<Row> dataset, Properties connectionProperties) {

        dataWriter.dbWrite(connectionProperties, dataset, "temp_products");

        dbSetup.updateQuery(updateProducts, connectionProperties);

        dbSetup.updateQuery(dropDupProducts, connectionProperties);

        dbSetup.updateQuery(mergeProducts, connectionProperties);

        dbSetup.updateQuery(dropProdTemp, connectionProperties);
    }

    public void writeAggregate(Dataset<Row> dataset, Properties connectionProperties) {

        Dataset<Row> aggregateData = dataset.groupBy("name").agg(count(lit(1))
                .alias("no. of products"));

        dataWriter.dbWrite(connectionProperties, aggregateData, "temp_aggregate");

        dbSetup.updateQuery(updateAggregate, connectionProperties);

        dbSetup.updateQuery(dropDupAggregate, connectionProperties);

        dbSetup.updateQuery(mergeAggregate, connectionProperties);

        dbSetup.updateQuery(dropAggTemp, connectionProperties);
    }

    public Properties getConnectionProperties() throws IOException {

        Properties connectionProperties = new Properties();

        InputStream inputStream = PipeLineFlow.class.getClassLoader()
                .getResourceAsStream("application.properties");

        connectionProperties.load(inputStream);

        return connectionProperties;
    }
}
