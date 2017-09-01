package com.zsy.bmw.dao;

import com.zsy.bmw.model.Agent;
import org.apache.ibatis.annotations.Param;


public interface UserMapper {

    void insert(Agent agent);

    Agent findUserByName(@Param("name") String name);

    Agent getUserByNameAndPassword(Agent agent);
}
