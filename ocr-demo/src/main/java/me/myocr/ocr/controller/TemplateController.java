package me.myocr.ocr.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.myocr.ocr.configuration.FileProperties;
import me.myocr.ocr.model.LabelDTO;
import me.myocr.ocr.model.ResultBean;
import me.myocr.ocr.model.TemplateBean;
import me.myocr.ocr.service.TemplateService;
import me.myocr.ocr.utils.FileUtils;
import me.myocr.ocr.utils.UUIDUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Api(tags = "自定义模版文字识别")
@RestController
@Configuration
@RequestMapping("/template")
public class TemplateController {
    private final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    private static String excelTemplate;

    @Autowired
    private TemplateService ocrTemplateService;

    @Value("${server.baseUri}")
    private String baseUri;

    /**
     * 文件配置
     */
    @Autowired
    private FileProperties properties;

    @ApiOperation(value = "获取模版")
    @GetMapping(value = "/getTemplate", produces = "application/json;charset=utf-8")
    public ResultBean getTemplate(@RequestParam(value = "uid") String uid) {
        try {
            TemplateBean templateBean = ocrTemplateService.getTemplate(uid);
            return ResultBean.success().add("result", templateBean);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }

    @ApiOperation(value = "获取模版列表")
    @GetMapping(value = "/getTemplates", produces = "application/json;charset=utf-8")
    public ResultBean getTemplatesList() {
        try {
            return ResultBean.success().add("result", ocrTemplateService.getTemplateList());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }

    @ApiOperation(value = "更新模板信息")
    @PostMapping(value = "/updateTemplate", consumes = "application/json;charset=utf-8")
    public ResultBean updateTemplate(@RequestBody TemplateBean templateBean) {
        try {
            ocrTemplateService.updateTemplate(templateBean);
            return ResultBean.success();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }

    @ApiOperation(value = "删除模板")
    @PostMapping(value = "/removeTemplate", produces = "application/json;charset=utf-8")
    public ResultBean removeTemplate(@RequestParam(value = "uid") String uid) {
        try {
            ocrTemplateService.removeTemplate(uid);
            return ResultBean.success();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }

    @ApiOperation(value = "识别标注字段")
    @PostMapping(value = "/getLabelData", produces = "application/json;charset=utf-8")
    public ResultBean getLabelData(@RequestBody LabelDTO labelDTO) {
        try {
            String result = ocrTemplateService.getLabelData(labelDTO.getUid(), labelDTO.getLabelData());
            logger.info("LabelData: " + result);
            return ResultBean.success().add("result", result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }

    @ApiOperation(value = "创建模板")
    @PostMapping(value = "/addTemplate")
    public ResultBean addTemplate(@RequestParam(value = "name") String name, @RequestParam(value = "imageFile") MultipartFile imageFile) {
        try {
            // 要上传的目标文件存放路径
            FileProperties.ElPath path = properties.getPath();
            String imagePath = path.getPath().replace("\\", "/") + "images/";
            FileUtils.checkAndCreatePath(imagePath);
            String templatePath = path.getPath().replace("\\", "/") + "templates/";
            FileUtils.checkAndCreatePath(templatePath);

            TemplateBean templateBean = new TemplateBean();
            String uid = UUIDUtils.getUUID();
            templateBean.setUid(uid);
            String suffix = FileUtils.getSuffix(Objects.requireNonNull(imageFile.getOriginalFilename()));
            if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") && !suffix.equalsIgnoreCase(".png")) {
                return ResultBean.failure().add("errors", "Image format should be JPG(JPEG) or PNG!");
            }
            String imageName = FileUtils.getFileName(imageFile.getOriginalFilename());
            templateBean.setImageName(imageName);
            templateBean.setName(name);

            logger.info("Template name:" + name);

            if (FileUtils.upload(imageFile, imagePath, imageName)) {
                ocrTemplateService.addTemplate(templateBean);
                return ResultBean.success().add("result", templateBean);
            } else {
                return ResultBean.failure();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }

    @ApiOperation(value = "模版文字识别-图片")
    @PostMapping(value = "/infoForImageFile", produces = "application/json;charset=utf-8")
    public ResultBean infoForImageFile(@RequestParam(value = "uid") String uid, @RequestParam(value = "imageFile") MultipartFile imageFile) {
        if (FileUtils.getSuffix(Objects.requireNonNull(imageFile.getOriginalFilename())).equals(".xlsx")) {
            FileProperties.ElPath path = properties.getPath();
            String fileRelativePath = path.getPath().replace("\\", "/");
            excelTemplate = fileRelativePath + imageFile.getOriginalFilename();
            System.out.println(excelTemplate);
            return ResultBean.success().add("result", "waiting...");
        }
        try (InputStream ignore = imageFile.getInputStream()) {
            String base64Img = Base64.encodeBase64String(imageFile.getBytes());
            TemplateBean templateBean = ocrTemplateService.getTemplate(uid);

            String excelResult = ocrTemplateService.getImageInfo(templateBean, imageFile.getBytes(), excelTemplate);
            String excelUrl = baseUri + File.separator + excelResult;
            Map<String, String> map = new ConcurrentHashMap<>();
            map.put("excelUrl", excelUrl);

            return ResultBean.success().add("result", excelUrl)
                    .add("base64Img", "data:imageName/jpeg;base64," + base64Img);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("errors", e.getMessage());
        }
    }
}
