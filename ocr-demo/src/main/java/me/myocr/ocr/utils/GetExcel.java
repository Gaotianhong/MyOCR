package me.myocr.ocr.utils;

import com.github.chimmhuang.excel.ExcelHelper;
import com.github.chimmhuang.excel.tablemodel.ExcelWorkbook;
import com.github.chimmhuang.excel.tablemodel.SheetTable;
import me.myocr.ocr.model.ExcelData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GetExcel {
    public static String testFillInTable(String fileName, List<String> data) throws IOException {

        // 获取 excel 二进制文件
        File file = new File(fileName);
        byte[] bytes = readFileToByteArray(file);

        // 创建 table 对象
        ExcelWorkbook excelWorkbook = ExcelHelper.createWorkbook(bytes);
        SheetTable table = excelWorkbook.getSheet(0);

        ExcelData tableData = new ExcelData();
        tableData.setData(data);

        // 设置 自定义 sheet 页名称
        table.setSheetName("result");

        // 将变量的值填充进表格
        ExcelHelper.fillInData(table, tableData);

        // 获取转换后的二进制（支持多 sheet 导出）
        byte[] bytes1 = ExcelHelper.convert2Byte(table, excelWorkbook.getSheet(0));

        String filePath = "file/tables/result.xlsx";
        File targetFile = new File(filePath);
        FileOutputStream fos = new FileOutputStream(targetFile);

        fos.write(bytes1);
        fos.close();
        return filePath;
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        byte[] bytes;
        try (FileInputStream fis = new FileInputStream(file)) {
            bytes = new byte[(int) file.length()];
            fis.read(bytes);
        } catch (IOException e) {
            throw e;
        }
        return bytes;
    }
}
