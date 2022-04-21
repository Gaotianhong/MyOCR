package me.myocr.ocr.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.myocr.ocr.utils.baidu.Base64Util;
import me.myocr.ocr.utils.baidu.HttpUtil;
import me.myocr.ocr.utils.baidu.AuthService;
import me.myocr.ocr.service.InferService;
import org.springframework.stereotype.Service;


import java.net.URLEncoder;


@Service
public class InferServiceImpl implements InferService {
    public String getGeneralInfoByBaidu(byte[] imgData) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate";
        try {
            // 本地文件路径
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 获取access_token
            String accessToken = AuthService.getAuth();

            String result = HttpUtil.post(url, accessToken, param);
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(result, Object.class);

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
