package datawrite;

import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Properties;

import static utils.Constants.upsertAggregate;
import static utils.Constants.upsertProducts;

public class DataWriterImpl implements DataWriter, Serializable {

    @Override
    public void productUpsert(Properties connectionProperties, Dataset<Row> dataset) {

        String url = connectionProperties.getProperty("url");
        String user = connectionProperties.getProperty("user");
        String pwd = connectionProperties.getProperty("password");

        dataset.coalesce(200).foreachPartition(new ForeachPartitionFunction<Row>() {
            @Override
            public void call(Iterator<Row> t) throws Exception {
                Connection connection = DriverManager.getConnection(url, user, pwd);
                PreparedStatement preparedStatement = connection.prepareStatement(upsertProducts);
                while (t.hasNext()){
                    Row row = t.next();

                    preparedStatement.setString(1, row.getString(0));
                    preparedStatement.setString(2, row.getString(1));
                    preparedStatement.setString(3, row.getString(2));

                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.close();
            }
        });
    }

    @Override
    public void aggregateUpsert(Properties connectionProperties, Dataset<Row> dataset) {

        String url = connectionProperties.getProperty("url");
        String user = connectionProperties.getProperty("user");
        String pwd = connectionProperties.getProperty("password");

        dataset.coalesce(200).foreachPartition(new ForeachPartitionFunction<Row>() {
            @Override
            public void call(Iterator<Row> t) throws Exception {
                Connection connection = DriverManager.getConnection(url, user, pwd);
                PreparedStatement preparedStatement = connection.prepareStatement(upsertAggregate);
                while (t.hasNext()){
                    Row row = t.next();

                    preparedStatement.setString(1, row.getString(0));
                    preparedStatement.setLong(2, row.getLong(1));

                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.close();
            }
        });
    }
}
