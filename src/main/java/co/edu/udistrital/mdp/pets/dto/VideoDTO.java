package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class VideoDTO {
    private Long id;
    private String url;
    private ShelterDTO shelter;
}