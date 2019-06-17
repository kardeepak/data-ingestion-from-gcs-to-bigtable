package com.blp.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.avro.data.Json;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;


public class Main {

    /*
    Set values for the runtime arguments
    */
    public interface Options extends DataflowPipelineOptions, StreamingOptions {

        @Description("The GCS File to read from.")
        @Default.String("gs://mongodb-historical-dump/tenMinAggData_collections/*.json")
        String getInputFile();
        void setInputFile(String value);
    }

    /*
    Main Method invoked when program rus
    */
    public static void main(String args[]) throws IOException {
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        options.setJobName("tenmin-aggregate-data-ingestion-to-bigtable");
        options.setStreaming(false);
        run(options);
    }

    /*
    Creating pipeline for writing in GCS and Bigtable.
    */
    public static PipelineResult run(Options options) {
        try {
            Pipeline p = Pipeline.create(options);

            PCollection<String> messages = p.apply("Read Messages", TextIO.read().from(options.getInputFile()));
            BigtableWriter.writeToBigtable(messages);

            return p.run();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
