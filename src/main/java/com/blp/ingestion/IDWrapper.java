package com.blp.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

public class IDWrapper {
    @JsonProperty("SId")
    public String sid;
    @JsonProperty("AssetId")
    public String assetId;
    @JsonProperty("ST")
    public HashMap<String, String> st;
}
