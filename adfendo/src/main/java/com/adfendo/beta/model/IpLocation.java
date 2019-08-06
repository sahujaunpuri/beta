package com.adfendo.beta.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IpLocation {
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("country_short")
    @Expose
    private String countryShort;
    @SerializedName("country_long")
    @Expose
    private String countryLong;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("status")
    @Expose
    private String status;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountryShort() {
        return countryShort;
    }

    public void setCountryShort(String countryShort) {
        this.countryShort = countryShort;
    }

    public String getCountryLong() {
        return countryLong;
    }

    public void setCountryLong(String countryLong) {
        this.countryLong = countryLong;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
