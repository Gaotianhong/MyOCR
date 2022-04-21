package me.myocr.ocr.configuration;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import me.myocr.ocr.inference.RecognitionModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class ModelConfiguration {
    @Value("${model.type}")
    private String type;

    @Value("${model.table.layout}")
    private String tableLayout;
    @Value("${model.table.table-en}")
    private String table;
    // mobile model
    @Value("${model.mobile.det}")
    private String mobileDet;
    @Value("${model.mobile.rec}")
    private String mobileRec;
    // light model
    @Value("${model.light.det}")
    private String lightDet;
    @Value("${model.light.rec}")
    private String lightRec;
    // server model
    @Value("${model.server.det}")
    private String serverDet;
    @Value("${model.server.rec}")
    private String serverRec;

    @Bean
    public RecognitionModel recognitionModel() throws IOException, ModelNotFoundException, MalformedModelException {
        RecognitionModel recognitionModel = new RecognitionModel();
        if (StringUtils.isEmpty(type) || type.toLowerCase().equals("mobile")) {
            recognitionModel.init(mobileDet, mobileRec);
        } else if (type.toLowerCase().equals("light")) {
            recognitionModel.init(lightDet, lightRec);
        } else if (type.toLowerCase().equals("server")) {
            recognitionModel.init(serverDet, serverRec);
        } else {
            recognitionModel.init(mobileDet, mobileRec);
        }
        return recognitionModel;
    }
}