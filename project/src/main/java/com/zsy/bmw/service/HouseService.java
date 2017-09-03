package com.zsy.bmw.service;

import com.github.pagehelper.PageHelper;
import com.zsy.bmw.dao.HouseMapper;
import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    public List<House> getHouseByCreator(String creatorName) {
        return houseMapper.getHouseByCreator(creatorName);
    }

    public List<House> getHouse(HouseCondition condition) {
        House _house = new House();
        _house.setRows(12);
        executePagination(_house);
        List<House> houses = getHouseByCondition(condition);
        for (House house : houses) {
            house.setHeadImg(houseMapper.getHeadImg(house.getId()));
        }
        return houses;
    }

    private List<House> getHouseByCondition(HouseCondition condition) {
        List<House> houses;
        Character areaType = condition.getAreaType();
        Character priceType = condition.getPriceType();
        Character roomType = condition.getRoomType();

        if (areaType != null) {
            houses = getHouseByArea(areaType);
        } else if (priceType != null) {
            houses = getHouseByPrice(priceType);
        } else if (roomType != null) {
            houses = getHouseByRoom(roomType);
        } else {
            houses = houseMapper.getHouseByTime();
        }
        return houses;
    }

    private List<House> getHouseByArea(Character areaType) {
        Integer min = 0;
        Integer max = 50;
        switch (areaType) {
            case 'b':
                min = 50;
                max = 100;
                break;
            case 'c':
                min = 100;
                max = 10000;
                break;
            default:
                break;
        }
        return houseMapper.getHouseByArea(min, max);
    }

    private List<House> getHouseByPrice(Character priceType) {
        Integer min = 0;
        Integer max = 100;
        switch (priceType) {
            case 'b':
                min = 100;
                max = 150;
                break;
            case 'c':
                min = 150;
                max = 10000;
                break;
            default:
                break;
        }
        return houseMapper.getHouseByPrice(min, max);
    }

    private List<House> getHouseByRoom(Character roomType) {
        Integer count = 1;
        switch (roomType) {
            case 'b':
                count = 2;
                break;
            case 'c':
                count = 3;
                break;
            case 'd':
                count = 4;
                break;
            case 'f':
                count = 5;
                break;
            default:
                break;
        }
        return houseMapper.getHouseByRoom(count);
    }


    public void saveHouse(House house) {
        houseMapper.insertHouse(house);
        houseMapper.insertHouseExtend(house);
        for (String imgUrl : house.getImgUrls()) {
            houseMapper.insertHouseImg(house.getId(), imgUrl);
        }
    }

    public House getHouseDetail(Integer id) {
        House house = houseMapper.getHouseById(id);
        if (house != null) {
            house = getExtendInfo(house);
            House houseExt = houseMapper.getHouseExtendById(id);
            house.setAddress(houseExt.getAddress());
            house.setYear(houseExt.getYear());
            house.setComName(houseExt.getComName());
            house.setDes(houseExt.getDes());
            house.setImgUrls(houseMapper.getHouseImgs(id));
        }
        return house;
    }

    private House getExtendInfo(House house) {
        House houseExt = houseMapper.getHouseExtendById(house.getId());
        house.setAddress(houseExt.getAddress());
        house.setYear(houseExt.getYear());
        house.setComName(houseExt.getComName());
        house.setDes(houseExt.getDes());
        return house;
    }


    private void executePagination(House house) {
        if (house.getPageNum() != null && house.getRows() != null) {
            PageHelper.startPage(house.getPageNum(), house.getRows());
        }
    }

}