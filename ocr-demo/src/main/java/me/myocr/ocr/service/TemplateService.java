package me.myocr.ocr.service;

import ai.djl.translate.TranslateException;
import me.myocr.ocr.model.LabelBean;
import me.myocr.ocr.model.TemplateBean;

import java.io.IOException;
import java.util.List;


public interface TemplateService {
    String getImageInfo(TemplateBean templateBean, byte[] imgData, String excelFile) throws Exception;

    List<TemplateBean> getTemplateList();

    TemplateBean getTemplate(String uid) throws IOException;

    void addTemplate(TemplateBean templateBean) throws IOException;

    void updateTemplate(TemplateBean templateBean) throws IOException;

    void removeTemplate(String uid) throws IOException;

    String getLabelData(String uid, LabelBean labelData) throws IOException, TranslateException;

    String getTemplateInfoByBaidu(byte[] imgData);
}
