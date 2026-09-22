package ru.project.analitic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrimesDepartment {

    private Integer numberTO;
    private String nameDepartment;
    private CrimeRequestDTO allCrimes;
    private CrimeRequestDTO crimesPerson;
    private CrimeRequestDTO crimesPrevent;
    private CrimeRequestDTO crimesDoublePrevent;
}
