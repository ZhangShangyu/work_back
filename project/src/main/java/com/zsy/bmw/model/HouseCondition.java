package com.zsy.bmw.model;


public class HouseCondition extends BaseEntity {

    private Character priceType;
    private Character areaType;
    private Character roomType;

    public Character getPriceType() {
        return priceType;
    }

    public void setPriceType(Character priceType) {
        this.priceType = priceType;
    }

    public Character getAreaType() {
        return areaType;
    }

    public void setAreaType(Character areaType) {
        this.areaType = areaType;
    }

    public Character getRoomType() {
        return roomType;
    }

    public void setRoomType(Character roomType) {
        this.roomType = roomType;
    }
}
