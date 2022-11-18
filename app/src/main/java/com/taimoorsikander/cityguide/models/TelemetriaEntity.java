package com.taimoorsikander.cityguide.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = TelemetriaEntity.TABLA)
public class TelemetriaEntity {

    // Timestamp;co2;humidity;light;soilTemp1;soilTemp2;temperature
    // 2022-10-21 00:13:18;273;47;0;21;22;25
    static public final String TABLA = "telemetrias";

    @PrimaryKey(autoGenerate = true)
    protected int uid;

    protected String timestamp;
    protected int co2;
    protected int humidity;
    protected int light;
    protected int soiltemp1;
    protected int soiltemp2;
    protected int temperature;


    public TelemetriaEntity(String timestamp, int co2, int humidity, int light, int soiltemp1, int soiltemp2, int temperature) {
        this.timestamp = timestamp;
        this.co2 = co2;
        this.humidity = humidity;
        this.light = light;
        this.soiltemp1 = soiltemp1;
        this.soiltemp2 = soiltemp2;
        this.temperature = temperature;
    }

    public int getUid() {
        return uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getSoiltemp1() {
        return soiltemp1;
    }

    public void setSoiltemp1(int soiltemp1) {
        this.soiltemp1 = soiltemp1;
    }

    public int getSoiltemp2() {
        return soiltemp2;
    }

    public void setSoiltemp2(int soiltemp2) {
        this.soiltemp2 = soiltemp2;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String toString(){
        String sOutput = "";
        sOutput+="Timestamp["+timestamp+"] ";
        sOutput+="CO2["+co2+"ppm] ";
        sOutput+="Humid["+humidity+"%] ";
        sOutput+="Light["+light+"lux] ";
        sOutput+="SoilTemp1["+soiltemp1+"ºC] ";
        sOutput+="SoilTemp2["+soiltemp2+"ºC] ";
        sOutput+="AmbTemp["+temperature+"ºC] ";

        return sOutput;
    }
}
