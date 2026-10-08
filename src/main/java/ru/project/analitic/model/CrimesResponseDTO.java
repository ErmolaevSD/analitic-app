package ru.project.analitic.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@HeadStyle(
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        wrapped = BooleanEnum.TRUE,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN
)
@HeadFontStyle(
        fontName = "Times New Roman",
        fontHeightInPoints = 14,
        bold = BooleanEnum.TRUE
)

@ContentStyle(
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        wrapped = BooleanEnum.TRUE,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN
)
@ContentFontStyle(
        fontName = "Times New Roman",
        fontHeightInPoints = 14,
        bold = BooleanEnum.FALSE
)
public class CrimesResponseDTO {

    @ExcelProperty("Код р-на")
    private Integer numberTO;
    @ExcelProperty("Наименование ТО")
    private String nameDepartment;
    @ExcelProperty("Всего")
    private Integer crimeAllCount;
    @ExcelProperty("С лицом")
    private Integer crimePersonCount;
    @ExcelProperty("Превентив")
    private Integer crimePreventCount;
    @ExcelProperty("Двойная")
    private Integer crimeDoublePreventCount;

}
