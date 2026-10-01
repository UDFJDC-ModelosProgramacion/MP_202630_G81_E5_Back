package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.VideoDTO;
import co.edu.udistrital.mdp.pets.entities.VideoEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.VideoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shelters")
public class VideoController {

    private final VideoService videoService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "/{shelterId}/videos")
    @ResponseStatus(code = HttpStatus.CREATED)
    public VideoDTO create(@PathVariable Long shelterId, @RequestBody VideoDTO videoDTO)
            throws EntityNotFoundException, IllegalOperationException {
        VideoEntity video = videoService.createVideo(shelterId, modelMapper.map(videoDTO, VideoEntity.class));
        return modelMapper.map(video, VideoDTO.class);
    }

    @GetMapping(value = "/{shelterId}/videos")
    @ResponseStatus(code = HttpStatus.OK)
    public List<VideoDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        List<VideoEntity> videos = videoService.getVideos(shelterId);
        return modelMapper.map(videos, new TypeToken<List<VideoDTO>>() {
        }.getType());
    }

    @GetMapping(value = "/{shelterId}/videos/{videoId}")
    @ResponseStatus(code = HttpStatus.OK)
    public VideoDTO findOne(@PathVariable Long shelterId, @PathVariable Long videoId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(videoService.getVideo(shelterId, videoId), VideoDTO.class);
    }

    @PutMapping(value = "/{shelterId}/videos/{videoId}")
    @ResponseStatus(code = HttpStatus.OK)
    public VideoDTO update(@PathVariable Long shelterId, @PathVariable Long videoId,
            @RequestBody VideoDTO videoDTO) throws EntityNotFoundException, IllegalOperationException {
        VideoEntity video = videoService.updateVideo(shelterId, videoId,
                modelMapper.map(videoDTO, VideoEntity.class));
        return modelMapper.map(video, VideoDTO.class);
    }

    @DeleteMapping(value = "/{shelterId}/videos/{videoId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long shelterId, @PathVariable Long videoId)
            throws EntityNotFoundException, IllegalOperationException {
        videoService.deleteVideo(shelterId, videoId);
    }
}