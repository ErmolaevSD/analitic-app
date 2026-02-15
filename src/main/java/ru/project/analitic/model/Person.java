package ru.project.analitic.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @ExcelProperty("Имя")
    private String firstName;
    @ExcelProperty("Фамилия")
    private String surName;
    @ExcelProperty("Отчество")
    private String lastName;
    @ExcelProperty("Дата рождения")
    private LocalDate birthdate;
}