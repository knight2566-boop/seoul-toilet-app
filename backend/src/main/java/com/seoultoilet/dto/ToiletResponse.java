package com.seoultoilet.dto;

public class ToiletResponse {

    private Long id;
    private String name;
    private String type;
    private String address;
    private Double lat;
    private Double lng;
    private String agency;
    private String phone;
    private String hours;
    private String hoursDetail;
    private Integer maleToilet;
    private Integer maleUrinal;
    private Integer femaleToilet;
    private Integer disabledMale;
    private Integer disabledFemale;
    private Boolean emergencyBell;
    private Boolean diaperTable;
    private Double distanceKm;
    private Double averageRating;
    private Long reviewCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }
    public String getHoursDetail() { return hoursDetail; }
    public void setHoursDetail(String hoursDetail) { this.hoursDetail = hoursDetail; }
    public Integer getMaleToilet() { return maleToilet; }
    public void setMaleToilet(Integer maleToilet) { this.maleToilet = maleToilet; }
    public Integer getMaleUrinal() { return maleUrinal; }
    public void setMaleUrinal(Integer maleUrinal) { this.maleUrinal = maleUrinal; }
    public Integer getFemaleToilet() { return femaleToilet; }
    public void setFemaleToilet(Integer femaleToilet) { this.femaleToilet = femaleToilet; }
    public Integer getDisabledMale() { return disabledMale; }
    public void setDisabledMale(Integer disabledMale) { this.disabledMale = disabledMale; }
    public Integer getDisabledFemale() { return disabledFemale; }
    public void setDisabledFemale(Integer disabledFemale) { this.disabledFemale = disabledFemale; }
    public Boolean getEmergencyBell() { return emergencyBell; }
    public void setEmergencyBell(Boolean emergencyBell) { this.emergencyBell = emergencyBell; }
    public Boolean getDiaperTable() { return diaperTable; }
    public void setDiaperTable(Boolean diaperTable) { this.diaperTable = diaperTable; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Long getReviewCount() { return reviewCount; }
    public void setReviewCount(Long reviewCount) { this.reviewCount = reviewCount; }
}
