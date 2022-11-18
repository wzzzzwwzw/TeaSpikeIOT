package com.taimoorsikander.cityguide.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Co2 {

    @SerializedName("ts")
    @Expose
    private Long ts;
    @SerializedName("value")
    @Expose
    private String value;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Co2() {
    }

    /**
     * 
     * @param value
     * @param ts
     */
    public Co2(Long ts, String value) {
        super();
        this.ts = ts;
        this.value = value;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
