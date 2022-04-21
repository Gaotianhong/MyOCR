package me.myocr.ocr.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.myocr.ocr.model.ResultBean;
import me.myocr.ocr.service.InferService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Api(tags = "通用文字识别")
@RestController
@RequestMapping("/inference")
public class InferController {
    private final Logger logger = LoggerFactory.getLogger(InferController.class);

    @Autowired
    private InferService inferService;


    @ApiOperation(value = "通用文字识别-图片")
    @PostMapping(value = "/generalInfoForImageFile", produces = "application/json;charset=utf-8")
    public ResultBean generalInfoForImageFile(@RequestParam(value = "imageFile") MultipartFile imageFile) {
        try (InputStream ignore = imageFile.getInputStream()) {
            String recognitionResult = inferService.getGeneralInfoByBaidu(imageFile.getBytes());
            JSONObject object = JSONObject.parseObject(recognitionResult);
            JSONArray jsonArray = object.getJSONArray("words_result");
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < jsonArray.size(); i++) {
                res.append(jsonArray.getJSONObject(i).getString("words"));
            }

            String base64Img = Base64.encodeBase64String(imageFile.getBytes());
            return ResultBean.success().add("result", res.toString()).add("base64Img", "data:imageName/jpeg;base64," + base64Img);

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }
}
