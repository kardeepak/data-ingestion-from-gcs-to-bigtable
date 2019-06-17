package com.blp.ingestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.bigtable.v2.Mutation;
import com.google.protobuf.ByteString;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.io.gcp.bigtable.BigtableIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.common.protocol.types.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


@DefaultCoder(AvroCoder.class)

public class BigtableWriter {

    private final static String PROJECT_ID = "platform-prod-blp";
    private final static String INSTANCE_ID = "blp-prod-ssd";
    private final static String TABLE_ID    = "10min_aggregates";
    private final static String COLUMN_FAMILY = "agg";
    private final static long TIME_PERIOD = 600;

    /*
    Write to Big Table
    */
    public static void writeToBigtable(PCollection<String> dp) // removing options parameter
    {
        try{
            if(dp != null)   {
                PCollection<KV<ByteString, Iterable<Mutation>>> mutations = toMutations(dp);
                if(mutations != null){
                    mutations.apply("Write to Bigtable",
                        BigtableIO.write()
                            .withProjectId(PROJECT_ID)
                            .withInstanceId(INSTANCE_ID)
                            .withTableId(TABLE_ID)
                    );
                }
            }
            else {
                System.out.println("Invalid Format custom log printed.");
            }
        
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
    Parse data from the Object class got from pub sub
    */

    private static PCollection<KV<ByteString, Iterable<Mutation>>> toMutations(PCollection<String> dp) {
        try{
            return dp.apply("Convert to bigtable rows", ParDo.of(new DoFn<String, KV<ByteString, Iterable<Mutation>>>() {
                @ProcessElement
                public void processElement(ProcessContext c) throws Exception {

                    try {
                        ObjectNode dp = (ObjectNode) Util.objMapper.readTree(c.element());
                        ArrayNode timeStamps = (ArrayNode) dp.get("TS");

                        for (int i = 0; i < timeStamps.size(); i++) {
                            String startTime = timeStamps.get(i).get("$numberLong").textValue();
                            String siteId = dp.get("_id").get("SId").textValue();
                            String assetId = dp.get("_id").get("AssetId").textValue();
                            String rowKeyPrefix = String.join("#", startTime, assetId, siteId);

                            if (dp.has("Tags")) {
                                JsonNode tags = dp.get("Tags");
                                Iterator<String> it = tags.fieldNames();
                                while (it.hasNext()) {
                                    String tagName = it.next();
                                    JsonNode values = tags.get(tagName);
                                    List<Mutation> mutations = new ArrayList<>();
                                    String rowKey = rowKeyPrefix + "#" + tagName;
                                    Double value;

                                    try {
                                        value = values.get("Max").get(i).doubleValue();
                                        if (value != null) addCell(mutations, COLUMN_FAMILY, "Max", value);
                                    } catch (Exception e) {
                                        System.out.println(e.toString());
                                    }

                                    try {
                                        value = values.get("Min").get(i).doubleValue();
                                        if (value != null) addCell(mutations, COLUMN_FAMILY, "Min", value);
                                    } catch (Exception e) {
                                        System.out.println(e.toString());
                                    }

                                    try {
                                        value = values.get("Sum").get(i).doubleValue();
                                        if (value != null) addCell(mutations, COLUMN_FAMILY, "Sum", value);
                                    } catch (Exception e) {
                                        System.out.println(e.toString());
                                    }

                                    try {
                                        value = values.get("Avg").get(i).doubleValue();
                                        if (value != null) addCell(mutations, COLUMN_FAMILY, "Avg", value);
                                    } catch (Exception e) {
                                        System.out.println(e.toString());
                                    }

                                    try {
                                        value = values.get("Std").get(i).doubleValue();
                                        if (value != null) addCell(mutations, COLUMN_FAMILY, "Std", value);
                                    } catch (Exception e) {
                                        System.out.println(e.toString());
                                    }

                                    if(mutations.size() > 0)
                                        c.output(KV.of(ByteString.copyFromUtf8(rowKey), mutations));
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }));
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static void addCell(List<Mutation> mutations, String colFamily, String cellName, double cellValue) {
        addCell(mutations, colFamily, cellName, Double.toString(cellValue));
    }

    private static void addCell(List<Mutation> mutations,String colFamily, String cellName, String cellValue) {
        if (cellValue.length() > 0) {
            ByteString value = ByteString.copyFromUtf8(cellValue);
            ByteString colname = ByteString.copyFromUtf8(cellName);
            Mutation m =
                    Mutation.newBuilder()
                            .setSetCell(
                                    Mutation.SetCell.newBuilder()
                                            .setValue(value)
                                            .setFamilyName(colFamily)
                                            .setColumnQualifier(colname)
                                            .setTimestampMicros(-1)
                            ).build();
            mutations.add(m);
        }
    }

}
