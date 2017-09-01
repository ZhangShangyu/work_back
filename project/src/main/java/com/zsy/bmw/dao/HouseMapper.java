package com.zsy.bmw.dao;

import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface HouseMapper {

    List<House> getTopHouses();

    String getHeadImg(@Param("houseId") Integer houseId);

    void insertHouse(House house);

    void insertHouseImg(@Param("houseId") Integer houseId, @Param("imgUrl") String imgUrl);

    House getHouseById(@Param("houseId") Integer houseId);

    List<String> getHouseImgs(@Param("houseId") Integer houseId);

    List<House> getHouseIdsByCondition(HouseCondition condition);

    List<House> getHouseByCreator(@Param("creatorName") String creatorName);

}
