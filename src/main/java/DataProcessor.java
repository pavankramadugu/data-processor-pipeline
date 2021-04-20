import dataload.DataLoaderImpl;
import datawrite.DataWriterImpl;
import pipeline.PipeLineFlow;
import repository.DBSetup;

import java.io.IOException;

public class DataProcessor {

    public static void main(String[] args) throws IOException {
        DataLoaderImpl dataLoader = new DataLoaderImpl();

        DataWriterImpl dataWriter = new DataWriterImpl();

        DBSetup dbSetup = new DBSetup();

        PipeLineFlow flow = new PipeLineFlow(dataLoader, dataWriter, dbSetup);

        flow.startDataPipeline();
    }
}
