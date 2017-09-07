package com.zsy.bmw.controller;

import com.zsy.bmw.model.House;
import com.zsy.bmw.model.HouseCondition;
import com.zsy.bmw.service.HouseService;
import com.zsy.bmw.utils.Constant;
import com.zsy.bmw.utils.Result;
import com.zsy.bmw.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(value = "/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UploadFileUtil uploadFileUtil;

    @RequestMapping("/list")
    public Result getTopHouse(HouseCondition condition) {
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(houseService.getHouse(condition));
        return result;
    }

    @RequestMapping("/save")
    public Result uploadHouse(@RequestBody House house) {
        houseService.saveHouse(house);
        return new Result(Constant.OK_CODE, Constant.OK);
    }

    @RequestMapping("/detail")
    public Result getHouseDetail(@RequestParam("id") Integer houseId) {
        House house = houseService.getHouseDetail(houseId);
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(house);
        return result;
    }


    @RequestMapping("house-by-me")
    public Result getHouseByCreator(@RequestParam("id") Integer agentId) {
        List<House> houses = houseService.getHouseByCreator(agentId);
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(houses);
        return result;
    }

    @RequestMapping(value = "/upload")
    public Result uploadPic(@RequestParam("file") MultipartFile uploadFile) {
        System.out.println("request enter");
        if (uploadFile == null || uploadFile.isEmpty()) {
            System.out.println("request null");
            return new Result(Constant.ERROR_CODE1, Constant.PARAM_ERROR);
        }
        String fileUrl;
        try {
            fileUrl = uploadFileUtil.saveUploadedFiles(uploadFile);
        } catch (IOException e) {
            System.out.println("request exception");
            e.printStackTrace();
            return new Result(Constant.ERROR_CODE2, Constant.SAVE_FILE_ERROR);
        }
        Result result = new Result(Constant.OK_CODE, Constant.OK);
        result.setData(fileUrl);
        System.out.println("request end  " + fileUrl);
        return result;
    }

}
