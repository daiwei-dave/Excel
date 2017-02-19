package com.dw.excel;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.util.List;

/**
 * Created by daiwei on 2017/1/14.
 */
public class CreteTemplate {

    public static void main(String[] args) {
        //获取解析xml文件路径
        ClassLoader classLoader = CreteTemplate.class.getClassLoader();
        URL resource = classLoader.getResource("student.xml");
        String path = resource.getPath();
        System.out.println(path);
        File file = new File(path);
        SAXBuilder builder = new SAXBuilder();

        try {
            //解析xml文件
            Document parse = builder.build(file);
            //创建Excel
            HSSFWorkbook wb = new HSSFWorkbook();
            //创建sheet
            HSSFSheet sheet = wb.createSheet("Sheet0");
            //获取xml文件跟节点
            Element root = parse.getRootElement();
            //获取模板名称,即excel的文件名
            String templateName = root.getAttribute("name").getValue();
            int rownum = 0;
            int column = 0;
            //设置列宽
            Element colgroup = root.getChild("colgroup");
            setColumnWidth(sheet, colgroup);

            //设置标题
            Element title = root.getChild("title");
            List<Element> trs = title.getChildren("tr");
            for (int i = 0; i < trs.size(); i++) {//遍历tr
                Element tr = trs.get(i);
                List<Element> tds = tr.getChildren("td");

                //设置样式为居中
                HSSFCellStyle cellStyle = wb.createCellStyle();
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

                HSSFRow row = sheet.createRow(rownum);
                for (column = 0; column < tds.size(); column++) {//遍历td
                    Element td = tds.get(column);
                    //创建列单元格
                    HSSFCell cell = row.createCell(column);
                    //获取td内的属性
                    Attribute rowSpan = td.getAttribute("rowspan");
                    Attribute colSpan = td.getAttribute("colspan");
                    Attribute value = td.getAttribute("value");
                    if (value != null) {
                        String val = value.getValue();
                        //设置单元格的值
                        cell.setCellValue(val);
                        //默认从0开始
                        int rspan = rowSpan.getIntValue() - 1;
                        int cspan = colSpan.getIntValue() - 1;
                        //设置字体
                        HSSFFont font = wb.createFont();
                        font.setFontName("仿宋_GB2312");
                        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//字体加粗
                        font.setFontHeightInPoints((short) 12);
                        cellStyle.setFont(font);
                        cell.setCellStyle(cellStyle);

                        //合并单元格居中
                        sheet.addMergedRegion(new CellRangeAddress(rspan, rspan, 0, cspan));
                    }
                }
                //该行单元填充完成，行号+1
                rownum++;
            }


            //设置表头
            Element thead = root.getChild("thead");
            trs = thead.getChildren("tr");
            for (int i = 0; i < trs.size(); i++) {
                Element tr = trs.get(i);
                //新创建一行
                HSSFRow row = sheet.createRow(rownum);
                List<Element> ths = tr.getChildren("th");
                for (column = 0; column < ths.size(); column++) {
                    //获取th标签
                    Element th = ths.get(column);
                    Attribute valueAttr = th.getAttribute("value");

                    HSSFCell cell = row.createCell(column);
                    if (valueAttr != null) {
                        String value = valueAttr.getValue();
                        cell.setCellValue(value);
                    }
                }
                //该行单元填充完成，行号+1
                rownum++;
            }


            //设置数据区域样式
            Element tbody = root.getChild("tbody");
            Element tr = tbody.getChild("tr");
            //获取创建行数
            int repeat = tr.getAttribute("repeat").getIntValue();
            List<Element> tds = tr.getChildren("td");
            for (int i = 0; i < repeat; i++) {
                HSSFRow row = sheet.createRow(rownum);
                for (column = 0; column < tds.size(); column++) {
                    //设置列
                    Element td = tds.get(column);
                    HSSFCell cell = row.createCell(column);
                    setType(wb, cell, td);
                }
                rownum++;
            }

            //生成Excel导入模板，即生成目标地址
            File tempFile = new File("e:/" + templateName + ".xls");
            //删除该路径下的文件
            tempFile.delete();
            //创建该路径下的文件
            tempFile.createNewFile();
            //获得输出流，默认使用的是overwrite
            FileOutputStream stream = FileUtils.openOutputStream(tempFile);
            wb.write(stream);
            stream.close();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 单元格样式
     * @param wb   工作簿
     * @param cell 单元格
     * @param td   所属列
     * @author David
     */
    private static void setType(HSSFWorkbook wb, HSSFCell cell, Element td) {
        Attribute typeAttr = td.getAttribute("type");
        String type = typeAttr.getValue();
        //设置格式
        HSSFDataFormat format = wb.createDataFormat();
        HSSFCellStyle cellStyle = wb.createCellStyle();
        if ("NUMERIC".equalsIgnoreCase(type)) {//将此 String 与另一个 String 比较，不考虑大小写。
            //设为数字类型
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            Attribute formatAttr = td.getAttribute("format");
            String formatValue = formatAttr.getValue();
            //设置数字格式
            formatValue = StringUtils.isNotBlank(formatValue) ? formatValue : "#,##0.00";
            cellStyle.setDataFormat(format.getFormat(formatValue));
        } else if ("STRING".equalsIgnoreCase(type)) {
            cell.setCellValue("");
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cellStyle.setDataFormat(format.getFormat("@"));
        } else if ("DATE".equalsIgnoreCase(type)) {
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cellStyle.setDataFormat(format.getFormat("yyyy-m-d"));
        } else if ("ENUM".equalsIgnoreCase(type)) {
            //所占空间位置
            CellRangeAddressList regions =
                    new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(),
                            cell.getColumnIndex(), cell.getColumnIndex());
            Attribute enumAttr = td.getAttribute("format");
            String enumValue = enumAttr.getValue();
            //加载下拉列表内容
            DVConstraint constraint =
                    DVConstraint.createExplicitListConstraint(enumValue.split(","));
            //数据有效性对象
            HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
            wb.getSheetAt(0).addValidationData(dataValidation);
        }
        cell.setCellStyle(cellStyle);
    }


    /**
     * 设置列宽
     * @param sheet
     * @param colgroup
     */
    private static void setColumnWidth(HSSFSheet sheet, Element colgroup) {
        List<Element> cols = colgroup.getChildren("col");
        for (int i = 0; i < cols.size(); i++) {
            Element col = cols.get(i);
            Attribute width = col.getAttribute("width");
            //获得width的单位，用空格替换数字
            String unit = width.getValue().replaceAll("[0-9,\\.]", "");
            //获得width的数值，用空格替换单位
            String value = width.getValue().replaceAll(unit, "");
            int v = 0;
            //设置成excel识别的数值
            if (StringUtils.isBlank(unit) || "px".endsWith(unit)) {
                v = Math.round(Float.parseFloat(value) * 37F);
            } else if ("em".endsWith(unit)) {
                v = Math.round(Float.parseFloat(value) * 267.5F);
            }
            sheet.setColumnWidth(i, v);
        }
    }

}
