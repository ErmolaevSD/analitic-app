package ru.project.analitic.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrimeRequestDTO {

    @ExcelProperty("Код р-на")
    private Integer numberTO;
    @ExcelProperty("Район")
    private String nameDepartment;
    @ExcelProperty("Статья")
    private Integer numberSt;
    @ExcelProperty("Фамилия подозр.(обв.) ф.1.1")
    private String namePerson;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        CrimeRequestDTO that = (CrimeRequestDTO) object;
        return Objects.equals(numberTO, that.numberTO) && Objects.equals(nameDepartment, that.nameDepartment) && Objects.equals(numberSt, that.numberSt) && Objects.equals(namePerson, that.namePerson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberTO, nameDepartment, numberSt, namePerson);
    }
}
