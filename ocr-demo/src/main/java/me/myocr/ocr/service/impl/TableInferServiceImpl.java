package me.myocr.ocr.service.impl;

import me.myocr.ocr.service.TableInferService;
import me.myocr.ocr.utils.baidu.Base64Util;
import me.myocr.ocr.utils.baidu.HttpUtil;
import me.myocr.ocr.utils.baidu.AuthService;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;


@Service
public class TableInferServiceImpl implements TableInferService {
    public String getTableInfoByBaidu(byte[] imgData) {
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/form_ocr/request";
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam + "&is_sync=true&request_type=excel";

            // 获取access_token
            String accessToken = AuthService.getAuth();

            String result = HttpUtil.post(url, accessToken, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
