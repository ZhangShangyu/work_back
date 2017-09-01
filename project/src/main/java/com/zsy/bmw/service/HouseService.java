package com.zsy.bmw.service;

import com.github.pagehelper.PageHelper;
import com.zsy.bmw.dao.HouseMapper;
import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class HouseService {
    @Autowired
    private HouseMapper houseMapper;


    public List<House> getHouseByCreator(String creatorName) {
        return houseMapper.getHouseByCreator(creatorName);
    }


    public List<House> getTopHouse() {
        House _house = new House();
        _house.setRows(12);
        executePagination(_house);
        List<House> houses = houseMapper.getTopHouses();
        for (House house : houses) {
            house.setHeadImg(houseMapper.getHeadImg(house.getId()));
        }
        return houses;
    }

    public void saveHouse(House house) {
        houseMapper.insertHouse(house);
        for (String imgUrl : house.getImgUrls()) {
            houseMapper.insertHouseImg(house.getId(), imgUrl);
        }
    }

    public House getHouseDetail(Integer id) {
        House house = houseMapper.getHouseById(id);
        if (house != null) {
            house.setImgUrls(houseMapper.getHouseImgs(id));
        }
        return house;
    }

    public List<House> getHouseByCondition(HouseCondition condition) {
        handleCondition(condition);
//        List<Integer> houseIds = getHouseIdBySolr(condition);
        List<House> houses = new ArrayList<>();
//        for (Integer id : houseIds) {
//            House house = houseMapper.getHouseById(id);
//            house.setHeadImg(houseMapper.getHeadImg(id));
//            List<String> tags = new ArrayList<>();
//            tags.add(house.getRegion());
//            tags.add(house.getType());
//            tags.add(house.getDec());
//            if (!"".equals(house.getRoute())) {
//                tags.add(house.getRoute());
//            }
//            if (!"".equals(house.getStation())) {
//                tags.add(house.getStation());
//            }
//            house.setTagNames(tags);
//            houses.add(house);
//        }
        return houses;
    }


    private void handleCondition(HouseCondition condition) {
        String price = condition.getPrice();
        if (price != null) {
            String minPrice = price.split("-")[0];
            String maxPrice = price.split("-")[1];
            condition.setMinPrice(Integer.parseInt(minPrice));
            condition.setMaxPrice(Integer.parseInt(maxPrice));
        }
        String area = condition.getArea();
        if (area != null) {
            String minArea = area.split("-")[0];
            String maxArea = area.split("-")[1];
            condition.setMinArea(Integer.parseInt(minArea));
            condition.setMaxArea(Integer.parseInt(maxArea));
        }
    }

    private void executePagination(House house) {
        if (house.getPageNum() != null && house.getRows() != null) {
            PageHelper.startPage(house.getPageNum(), house.getRows());
        }
    }

}
