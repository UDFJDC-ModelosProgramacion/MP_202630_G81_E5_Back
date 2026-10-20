package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class VaccinationRecordDTO {

    private Long id;
    private String vaccinesApplied;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date nextDueDate;

}