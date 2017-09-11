package com.zsy.bmw.service;

import com.github.pagehelper.PageHelper;
import com.zsy.bmw.dao.HouseMapper;
import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import com.zsy.bmw.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    private static Logger logger = LoggerFactory.getLogger(HouseService.class);

    public List<House> getHouseByCreator(Integer agentId) {
        return houseMapper.getHouseByCreator(agentId);
    }


    @Cacheable(value = "houseList", keyGenerator = "keyGenerator")
    public List<House> getHouse(HouseCondition condition) {
        executePagination(condition.getPageNum());
        List<House> houses = getHouseByCondition(condition);
        for (House house : houses) {
            String imgName = houseMapper.getHeadImg(house.getId());
            house.setHeadImg(handleHouseImgUrl(imgName));
        }
        return houses;
    }

    private String handleHouseImgUrl(String imgName) {
        String imgUrl = "";
        if (imgName != null) {
            if (imgName.contains("http")) {
                return imgName;
            } else {
                return Constant.IMG_PREFIX + imgName;
            }
        }
        return imgUrl;
    }

    private List<House> getHouseByCondition(HouseCondition condition) {
        List<House> houses;
        Character areaType = condition.getAreaType();
        Character priceType = condition.getPriceType();
        Character roomType = condition.getRoomType();
        Character sortType = condition.getSortType();

        String sortKey = "id desc";
        if (sortType != null) {
            if (sortType == 'b') {
                sortKey = "area desc";
            } else if (sortType == 'c') {
                sortKey = "price desc";
            }
        }

        if (areaType != null) {
            houses = getHouseByArea(areaType, sortKey);
        } else if (priceType != null) {
            houses = getHouseByPrice(priceType, sortKey);
        } else if (roomType != null) {
            houses = getHouseByRoom(roomType, sortKey);
        } else {
            houses = houseMapper.getHouseByTime();
        }
        return houses;
    }

    private List<House> getHouseByArea(Character areaType, String sortKey) {
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
        return houseMapper.getHouseByArea(min, max, sortKey);
    }

    private List<House> getHouseByPrice(Character priceType, String sortKey) {
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
        return houseMapper.getHouseByPrice(min, max, sortKey);
    }

    private List<House> getHouseByRoom(Character roomType, String sortKey) {
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
        return houseMapper.getHouseByRoom(count, sortKey);
    }


    public void saveHouse(House house) {
        houseMapper.insertHouse(house);
        houseMapper.insertHouseExtend(house);
        if (house.getImgUrls() != null) {
            for (String imgUrl : house.getImgUrls()) {
                houseMapper.insertHouseImg(house.getId(), imgUrl);
            }
        }
    }

    @Cacheable(value = "houseDetail", keyGenerator = "keyGenerator")
    public House getHouseDetail(Integer id) {

        House house = houseMapper.getHouseById(id);
        if (house != null) {
            house = getExtendInfo(house);
            House houseExt = houseMapper.getHouseExtendById(id);
            house.setAddress(houseExt.getAddress());
            house.setYear(houseExt.getYear());
            house.setComName(houseExt.getComName());
            house.setDes(houseExt.getDes());
            house.setImgUrls(getHouseImgUrlList(house.getId()));
        }
        return house;
    }

    private List<String> getHouseImgUrlList(Integer houseId) {
        List<String> result = new ArrayList<>();
        List<String> imgNames = houseMapper.getHouseImgs(houseId);
        for (String imgName : imgNames) {
            result.add(handleHouseImgUrl(imgName));
        }
        return result;
    }

    private House getExtendInfo(House house) {
        House houseExt = houseMapper.getHouseExtendById(house.getId());
        house.setAddress(houseExt.getAddress());
        house.setYear(houseExt.getYear());
        house.setComName(houseExt.getComName());
        house.setDes(houseExt.getDes());
        return house;
    }


    private void executePagination(Integer pageNum) {
        PageHelper.startPage(pageNum, Constant.HOUSE_ROWS);
    }


    public void genTestData(Integer count) {
        Runnable runner = () -> {
            for (int i = 0; i < count; ++i) {
                House house = getTestHouse();
                saveHouse(house);
                if (i % 1000 == 0) {
                    logger.info("finish " + i);
                }
            }

        };
        new Thread(runner).start();
    }

    private House getTestHouse() {
        House house = new House();
        house.setName(getName());
        house.setArea(getRandom(0, 150));
        house.setRoom(getRandom(1, 5));
        house.setHall(getRandom(0, 4));
        house.setPrice((float) getRandom(10, 2000));
        house.setPosition(getRandom(0, 20));
        house.setAllPos(getRandom(20, 30));
        house.setYear(getRandom(1990, 2020));
        house.setComName(getName());
        house.setDes(getName());

        String[] address = {"杨浦", "黄浦", "静安", "闵行", "嘉定", "浦东", "宝山", "徐汇"};
        house.setAddress(address[getRandom(0, 8)]);
        String[] imgs = {"https://pic1.ajkimg.com/display/xinfang/bf4a9ccb39e707ce3de7e0f53a514508/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/af05eac6e92d659be827ba2c9d5796f4/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/1fce8b54f922521061914b2ea35ecbfa/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/52e178897960bc3f9225f8ed925fffd7/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/9dc660e0be8f83f1495a8227b8f63a13/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/e648c0f51085c9d44b2a4e05f1e68cbe/180x135m.jpg",
                "https://pic1.ajkimg.com/display/xinfang/0583cd060291deafcb2367fa0d3978cf/180x135m.jpg",
                "https://pic1.ajkimg.com/display/aifang/b7a0141af6b4e8b2c7b40901dd24f08b/180x135c.jpg",
                "https://pic1.ajkimg.com/display/aifang/ed526939e5a70956f925affd1ab20ad6/180x135c.jpg",
                "https://pic1.ajkimg.com/display/aifang/52e178897960bc3f9225f8ed925fffd7/133x100c.jpg",
                "https://pic1.ajkimg.com/display/aifang/e648c0f51085c9d44b2a4e05f1e68cbe/133x100c.jpg",
                "https://pic1.ajkimg.com/display/aifang/a36321b0f3ff8cf2c878ba7dc928d856/133x100c.jpg"};

        List<String> _imgs = new ArrayList<>();
        _imgs.add(imgs[getRandom(0, 5)]);
        _imgs.add(imgs[getRandom(6, 11)]);

        house.setImgUrls(_imgs);

        return house;
    }

    private int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    private String getName() {
        String name = "保利建工西郊锦庐首创旭辉城绿地海湾宫园巧筑天健萃园上海长滩城";
        return String.valueOf(name.charAt(getRandom(0, 30))) +
                name.charAt(getRandom(0, 30)) +
                name.charAt(getRandom(0, 30)) +
                name.charAt(getRandom(0, 30));
    }

}
