package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import lombok.Data;

@Data
public class VaccinationRecordDTO {

    private Long id;
    private String vaccinesApplied;
    private Date nextDueDate;

}