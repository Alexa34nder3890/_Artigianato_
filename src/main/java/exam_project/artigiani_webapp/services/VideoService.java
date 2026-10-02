package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.VideoEntity;
import exam_project.artigiani_webapp.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<VideoEntity> findAll() {
        return videoRepository.findAllByOrderByIdDesc();
    }

    public VideoEntity save(VideoEntity video) {
        return videoRepository.save(video);
    }

    public void deleteById(Long id) {
        videoRepository.deleteById(id);
    }
}
