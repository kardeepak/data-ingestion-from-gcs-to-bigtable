package com.blp.ingestion;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

/*
Used for parsing the parent data
*/
@DefaultCoder(AvroCoder.class)
public class DataParent  {
    @JsonProperty("_id")
    public IDWrapper id;
    @JsonProperty("ET")
    public HashMap<String, String> endTime;
    @JsonProperty("TS")
    public ArrayList<HashMap<String, String>> timeStamps;
    @JsonProperty("Tags")
    public HashMap<String, TagValue> tags;
}
