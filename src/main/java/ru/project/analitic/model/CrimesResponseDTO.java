package ru.project.analitic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrimesResponseDTO {

    private Integer numberTO;
    private String nameDepartment;
    private Integer crimeAllCount;
    private Integer crimePersonCount;
    private Integer crimePreventCount;
    private Integer crimeDoublePreventCount;

}
