package es.um.sisdist.videofaces.backend.dao.video;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.LinkedList;
import java.sql.Blob;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.backend.dao.models.Face;
public interface IVideoDAO
{
    Optional<Video> getVideoById(String id);
    LinkedList<Video> getVideosByUid(String uid);
    
    LinkedList<Face> getFacesByVid(String vid);
    // Get stream of video data
    InputStream getStreamForVideo(String id);

    Video.PROCESS_STATUS getVideoStatus(String id);

	Optional<Video> saveVideo(String userid, LocalDateTime localDateTime, String filename, InputStream inputStream);
	void saveImage(String vid, InputStream datosImagen);
}
