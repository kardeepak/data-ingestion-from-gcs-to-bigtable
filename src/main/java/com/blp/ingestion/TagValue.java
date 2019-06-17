package com.blp.ingestion;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagValue {
    private ArrayList<Double> Max;
    private ArrayList<Double> Min;
    private ArrayList<Double> Sum;
    private ArrayList<Double> Avg;
    private ArrayList<Double> Std;

    @JsonGetter("Max")
    public ArrayList<Double> getMax() {
        return Max;
    }

    @JsonSetter("Max")
    public void setMax(ArrayList<Double> max) {
        Max = max;
    }

    @JsonGetter("Min")
    public ArrayList<Double> getMin() {
        return Min;
    }

    @JsonSetter("Min")
    public void setMin(ArrayList<Double> min) {
        Min = min;
    }

    @JsonGetter("Sum")
    public ArrayList<Double> getSum() {
        return Sum;
    }

    @JsonSetter("Sum")
    public void setSum(ArrayList<Double> sum) {
        Sum = sum;
    }

    @JsonGetter("Avg")
    public ArrayList<Double> getAvg() {
        return Avg;
    }

    @JsonSetter("Avg")
    public void setAvg(ArrayList<Double> avg) {
        Avg = avg;
    }

    @JsonGetter("Std")
    public ArrayList<Double> getStd() {
        return Std;
    }

    @JsonSetter("Std")
    public void setStd(ArrayList<Double> std) {
        Std = std;
    }
}
