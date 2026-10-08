package ru.project.analitic.model;

import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrimesDepartment {

    private Integer numberTO;
    private String nameDepartment;
    private Set<CrimeRequestDTO> allCrimes = new HashSet<>();
    private Set<CrimeRequestDTO> crimesPerson = new HashSet<>();
    private Set<CrimeRequestDTO> crimesPrevent = new HashSet<>();
    private Set<CrimeRequestDTO> crimesDoublePrevent = new HashSet<>();
}
