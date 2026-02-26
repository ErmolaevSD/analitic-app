package ru.project.analitic.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @ExcelProperty("Фамилия")
    private String surName;
    @ExcelProperty("Имя")
    private String firstName;
    @ExcelProperty("Отчество")
    private String lastName;
    @ExcelProperty("Дата рождения")
    private String birthdate;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Person person = (Person) object;
        return Objects.equals(surName, person.surName) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(birthdate, person.birthdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surName, firstName, lastName, birthdate);
    }
}