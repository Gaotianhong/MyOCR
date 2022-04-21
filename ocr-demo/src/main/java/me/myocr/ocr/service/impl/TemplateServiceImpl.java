package me.myocr.ocr.service.impl;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.translate.TranslateException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.myocr.ocr.configuration.FileProperties;
import me.myocr.ocr.inference.RecognitionModel;
import me.myocr.ocr.model.LabelBean;
import me.myocr.ocr.model.TemplateBean;
import me.myocr.ocr.service.TemplateService;
import me.myocr.ocr.utils.FileUtils;
import me.myocr.ocr.utils.GetExcel;
import me.myocr.ocr.utils.PointUtils;
import me.myocr.ocr.utils.baidu.AuthService;
import me.myocr.ocr.utils.baidu.Base64Util;
import me.myocr.ocr.utils.baidu.HttpUtil;
import me.myocr.ocr.utils.CalculateIOU;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


@Service
public class TemplateServiceImpl implements TemplateService {
    private static final String TEMPLATE_LIST_FILE = "templates.json";
    private final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    /**
     * file configuration
     */
    @Autowired
    private FileProperties properties;

    /**
     * ocr recognition model
     */
    @Autowired
    private RecognitionModel recognitionModel;


    public String getImageInfo(TemplateBean templateBean, byte[] imgData, String excelFile) throws Exception {
//        List<LabelBean> anchorLabels = getLabelDataByType(templateBean.getLabelData(), "anchor");
        List<LabelBean> contentLabels = getLabelDataByType(templateBean.getLabelData(), "rectangle");

        String recognitionResult = getTemplateInfoByBaidu(imgData);
        JSONObject object = JSONObject.parseObject(recognitionResult);
        JSONArray jsonArray = object.getJSONArray("words_result");

        HashMap<Integer, String> mp = new HashMap<>();
        // 锚点标注框，已在excel模版中预先填写完毕
//        for (LabelBean anchorLabel : anchorLabels) {
//            mp.put(anchorLabel.getIndex(), anchorLabel.getValue());
//        }

        for (LabelBean contentLabel : contentLabels) {
            int[] rect1 = PointUtils.rectTOPLEFTWH(contentLabel.getPoints());
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject tempRect = jsonArray.getJSONObject(j).getJSONObject("location");
                int[] rect2 = {tempRect.getInteger("top"), tempRect.getInteger("left"),
                        tempRect.getInteger("width"), tempRect.getInteger("height")};
                float iou = CalculateIOU.getIOU(rect1, rect2); // 以左上角为坐标原点
                if (iou > 0.1f) {
                    mp.put(contentLabel.getIndex(), jsonArray.getJSONObject(j).getString("words"));
                }
            }
        }

        List<String> Data = new ArrayList<>(Collections.nCopies(object.getInteger("words_result_num") + 1, ""));

        for (Integer i : mp.keySet()) {
            Data.set(i, mp.get(i));
        }

        String filePath = GetExcel.testFillInTable(excelFile, Data);
        return filePath;
    }

    public List<TemplateBean> getTemplateList() {
        List<TemplateBean> templateList = null;
        FileProperties.ElPath path = properties.getPath();
        String fileRelativePath = path.getPath().replace("\\", "/");
        String json = null;
        try {
            json = FileUtils.readFile(fileRelativePath, TEMPLATE_LIST_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isBlank(json)) {
            templateList = new Gson().fromJson(json, new TypeToken<List<TemplateBean>>() {
            }.getType());
        } else {
            templateList = new ArrayList<>();
        }
        return templateList;
    }

    /**
     * 获取模板
     *
     * @param uid
     */
    public TemplateBean getTemplate(String uid) throws IOException {
        TemplateBean template = null;
        FileProperties.ElPath path = properties.getPath();
        String fileRelativePath = path.getPath().replace("\\", "/") + "templates/";
        String json = FileUtils.readFile(fileRelativePath, uid + ".json");
        if (!StringUtils.isBlank(json)) {
            template = new Gson().fromJson(json, new TypeToken<TemplateBean>() {
            }.getType());
        }
        return template;
    }

    /**
     * 新增模板
     *
     * @param templateBean
     */
    public synchronized void addTemplate(TemplateBean templateBean) throws IOException {
        List<TemplateBean> templateList = getTemplateList();
        templateBean.setLabelData(null);
        templateList.add(templateBean);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(templateList);
        FileProperties.ElPath path = properties.getPath();
        // 保存模版列表数据
        String fileRelativePath = path.getPath().replace("\\", "/");
        FileUtils.saveFile(fileRelativePath, TEMPLATE_LIST_FILE, json);
        // 保存模版数据
        json = gson.toJson(templateBean);
        logger.info(json);
        FileUtils.saveFile(fileRelativePath + "templates/", templateBean.getUid() + ".json", json);
    }

    /**
     * 更新模板
     *
     * @param templateBean
     */
    public synchronized void updateTemplate(TemplateBean templateBean) throws IOException {
        List<TemplateBean> templateList = getTemplateList();
        for (TemplateBean item : templateList) {
            if (item.getUid().equals(templateBean.getUid())) {
                BeanUtils.copyProperties(templateBean, item);
                item.setLabelData(null);
                break;
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(templateList);
        FileProperties.ElPath path = properties.getPath();
        // 保存模版列表数据
        String fileRelativePath = path.getPath().replace("\\", "/");
        FileUtils.saveFile(fileRelativePath, TEMPLATE_LIST_FILE, json);
        // 保存模版数据
        json = gson.toJson(templateBean);
        FileUtils.saveFile(fileRelativePath + "templates/", templateBean.getUid() + ".json", json);
    }

    /**
     * 删除模板
     *
     * @param uid
     */
    public synchronized void removeTemplate(String uid) throws IOException {
        List<TemplateBean> templateList = getTemplateList();
        for (int i = 0; i < templateList.size(); i++) {
            if (templateList.get(i).getUid().equals(uid)) {
                templateList.remove(i);
                break;
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(templateList);
        FileProperties.ElPath path = properties.getPath();
        // 保存模版列表数据
        String fileRelativePath = path.getPath().replace("\\", "/");
        FileUtils.saveFile(fileRelativePath, TEMPLATE_LIST_FILE, json);
        // 删除模版数据
        FileUtils.removeFile(fileRelativePath + "templates/", uid + ".json");
    }


    public String getLabelData(String uid, LabelBean labelData) throws IOException, TranslateException {
        TemplateBean template = getTemplate(uid);
        FileProperties.ElPath path = properties.getPath();
        String fileRelativePath = path.getPath().replace("\\", "/");
        BufferedImage img = ImageIO.read(new File(fileRelativePath + "images/" + template.getImageName()));
        Image image = ImageFactory.getInstance().fromImage(img);
        int[] rect = PointUtils.rectXYWH(labelData.getPoints());
        Image subImage = image.getSubImage(rect[0], rect[1], rect[2], rect[3]);
        String result = recognitionModel.predictSingleLineText(subImage);
        return result;
    }

    private List<LabelBean> getLabelDataByType(List<LabelBean> labelData, String type) {
        List<LabelBean> labels = new ArrayList<>();
        for (int i = 0; i < labelData.size(); i++) {
            if (labelData.get(i).getType().equals(type)) {
                labels.add(labelData.get(i));
            }
        }
        return labels;
    }

    public String getTemplateInfoByBaidu(byte[] imgData) {
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate";
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 获取access_token
            String accessToken = AuthService.getAuth();
            return HttpUtil.post(url, accessToken, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
