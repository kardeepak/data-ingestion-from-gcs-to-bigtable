package com.blp.ingestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;


public class Util {

    public final static ObjectMapper objMapper= new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
}