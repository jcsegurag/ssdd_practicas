package es.um.sisdist.videofaces.backend.dao.video;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import es.um.sisdist.videofaces.backend.dao.models.Video;

public interface IVideoDAO
{
    public Optional<Video> getVideoById(String id);

    // Get stream of video data
    public InputStream getStreamForVideo(String id);

    public Video.PROCESS_STATUS getVideoStatus(String id);

	Optional<Video> saveVideo(String userid, LocalDateTime localDateTime, String filename, InputStream inputStream);
}
