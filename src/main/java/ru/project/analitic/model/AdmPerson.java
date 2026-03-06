package ru.project.analitic.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdmPerson {

    @ExcelProperty("Дата совершения правонарушения")
    private LocalDate dateFact;
    @ExcelProperty("Дата составления протокола")
    private LocalDate dateProtocol;
    @ExcelProperty("Номер протокола")
    private String numberProtocol;
    @ExcelProperty("КУСП")
    private String numberKusp;
    @ExcelProperty("Подразделение составителя")
    private String nameOtdel;
    @ExcelProperty("Статья")
    private String numberStat;
    @ExcelProperty("Территориальный орган")
    private String nameDepartment;
    @ExcelProperty("Статус дела")
    private String status;
    @ExcelProperty("Фамилия нарушителя")
    private String surName;
    @ExcelProperty("Имя нарушителя")
    private String firstName;
    @ExcelProperty("Отчество нарушителя")
    private String lastName;
    @ExcelProperty("Дата рождения нарушителя")
    private LocalDate birthdate;
    @ExcelProperty("Дата вынесения постановления (о назначении адм. наказания)")
    private LocalDate dateEnd;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AdmPerson admPerson = (AdmPerson) o;
        return Objects.equals(surName, admPerson.surName) && Objects.equals(firstName, admPerson.firstName) && Objects.equals(lastName, admPerson.lastName) && Objects.equals(birthdate, admPerson.birthdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surName, firstName, lastName, birthdate);
    }
}