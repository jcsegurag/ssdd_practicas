package es.um.sisdist.videofaces.models;
import es.um.sisdist.videofaces.backend.dao.models.Video;

public class VideoDTOUtils {
	
	public static Video fromDTO(VideoDTO vdto)
	{
		return new Video(vdto.getVid(),
				vdto.getUid(),
				vdto.getPstatus(),
				vdto.getFilename(),
				vdto.getDate()
				);
	}
	public static VideoDTO toDTO(Video v)
	{
		return new VideoDTO(v.getId(),
				v.getUserid(),
				v.getFilename(),
				v.getDate(),
				v.getPstatus()
				);
	}
}
