package me.myocr.ocr.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.myocr.ocr.model.ResultBean;
import me.myocr.ocr.service.TableInferService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Api(tags = "表格文字识别")
@RestController
@RequestMapping("/table")
public class TableController {
    private final Logger logger = LoggerFactory.getLogger(TableController.class);

    @Autowired
    private TableInferService tableInferService;


    @ApiOperation(value = "单表格文字识别-图片")
    @PostMapping("/tableInfoForImageFile")
    public ResultBean tableInfoForImageFile(@RequestParam(value = "imageFile") MultipartFile imageFile) {
        try (InputStream ignore = imageFile.getInputStream()) {
            String recognitionResult = tableInferService.getTableInfoByBaidu(imageFile.getBytes());
            JSONObject object = JSONObject.parseObject(recognitionResult);
            String excelUrl = object.getJSONObject("result").getString("result_data");
            Map<String, String> map = new ConcurrentHashMap<>();
            map.put("excelUrl", excelUrl);
            String base64Img = Base64.encodeBase64String(imageFile.getBytes());
            return ResultBean.success().add("result", map)
                    .add("base64Img", "data:imageName/jpeg;base64," + base64Img);

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }
}
