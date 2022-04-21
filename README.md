## MyOCR

包含以下功能
1. 自定义模版
2. 基于模板识别
3. 通用文本识别
4. 表格识别

### 1. 前端部署

#### 直接运行：
```bash
cd ocr-ui
npm install
npm run dev
```

#### 构建dist安装包：
```bash
npm run build:prod
```

### 2. 后端部署

#### 构建jar包
可使用 IDEA 自带功能进行构建

#### 运行程序
```bash
java -jar iocr-demo.jar
```

#### 文件说明
```bash
# 模板索引文件路径 file/templates.json
# 模版配置文件路径 file/templates/202842e8155948b1b36baf516e030671.json
# 模板图片存储路径 file/images/3a37996f1c2f4beab972ca3e5f9b6a97.png
# Excel模版路径 file/template1.xlsx
# 测试图片路径 file/测试数据-1.png
# 识别结果路径 file/tables/result.xlsx
```

### 3. 功能测试

#### 自定义模版
红色为锚点框，蓝色为内容识别区

<img width="50%" alt="image" src="https://user-images.githubusercontent.com/79080003/164407768-21f7211f-6637-4db5-ab82-abc95650f86e.png">

#### 基于模版识别
选择框选好的模版，再上传文件，包括Excel模版和需要识别的图片

<img width="50%" alt="image" src="https://user-images.githubusercontent.com/79080003/164408042-40c11c71-25a4-4193-923f-96e6eb00116b.png">

Excel模版如下：

<img width="50%" alt="image" src="https://user-images.githubusercontent.com/79080003/164408343-496026d5-2b90-422b-b061-2455c3bdd844.png">

识别结果如下：

<img width="50%" alt="image" src="https://user-images.githubusercontent.com/79080003/164408595-73de905f-fb3a-40ff-b327-9e51186e3a64.png">

#### 通用文本识别
文本识别，直接返回识别结果

<img width="50%" alt="image" src="https://user-images.githubusercontent.com/79080003/164408831-c2f999e6-ce22-4955-86e3-d20ff692b694.png">

#### 表格识别
无需制作模版，但识别精度较低

<img width="60%" alt="image" src="https://user-images.githubusercontent.com/79080003/164408709-4d620884-2cbd-4538-9590-652235001dfc.png">

识别结果如下：

<img width="50%" alt="image" src="https://user-images.githubusercontent.com/79080003/164408731-863edce2-01d3-4817-bf2b-7b2c86c2b2da.png">

### 4. 注册百度开发者账号
在此次填入 API Key 和 Secret Key

文件名：AuthService.java

<img width="345" alt="image" src="https://user-images.githubusercontent.com/79080003/164411281-b90f8386-bd0f-45c9-b243-4e8724cafb70.png">







  



