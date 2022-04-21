package me.myocr.ocr.inference;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.paddlepaddle.zoo.cv.objectdetection.PpWordDetectionTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public final class RecognitionModel {
    private ZooModel<Image, DetectedObjects> detectionModel;
    private ZooModel<Image, String> recognitionModel;
    private Predictor<Image, String> recognizer;
    private Predictor<Image, DetectedObjects> detector;

    public void init(String detUri, String recUri) throws MalformedModelException, ModelNotFoundException, IOException {
        this.detectionModel = ModelZoo.loadModel(detectCriteria(detUri));
        this.recognitionModel = ModelZoo.loadModel(recognizeCriteria(recUri));
        this.recognizer = recognitionModel.newPredictor();
        this.detector = detectionModel.newPredictor();
    }

    public void close() {
        this.detectionModel.close();
        this.recognitionModel.close();
        this.recognizer.close();
        this.detector.close();
    }

    public String predictSingleLineText(Image image)
            throws TranslateException {
        String text = recognizer.predict(image);
        return text;
    }

    private Criteria<Image, DetectedObjects> detectCriteria(String detUri) {
        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                        .optEngine("PaddlePaddle")
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelUrls(detUri)
                        // .optDevice(Device.cpu())
                        .optTranslator(new PpWordDetectionTranslator(new ConcurrentHashMap<String, String>()))
                        .optProgress(new ProgressBar())
                        .build();

        return criteria;
    }

    private Criteria<Image, String> recognizeCriteria(String recUri) {
        Criteria<Image, String> criteria =
                Criteria.builder()
                        .optEngine("PaddlePaddle")
                        .setTypes(Image.class, String.class)
                        .optModelUrls(recUri)
                        // .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .optTranslator(new PpWordRecognitionTranslator())
                        .build();

        return criteria;
    }
}
