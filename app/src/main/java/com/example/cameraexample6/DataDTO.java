package com.example.cameraexample6;

public class DataDTO {
    private String PictureUri;//사진 uri
    private Double latitude;//위도
    private Double longitude;//경도
    private Double temperature;//온도
    private String weather; //날씨
    public DataDTO(){
    }

    public DataDTO(String pictureUri, Double latitude, Double longitude, Double temperature, String weather) {
        this.PictureUri = pictureUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.weather = weather;
    }

    public String getPictureUri() {
        return PictureUri;
    }

    public void setPictureUri(String pictureUri) {
        PictureUri = pictureUri;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTemperature() { return temperature; }

    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public String getWeather() { return weather; }

    public void setWeather(String weather) { this.weather = weather; }
}
