package es.um.sisdist.videofaces.models;

import es.um.sisdist.videofaces.backend.dao.models.Face;

public class FaceDTOUtils {
	public static Face fromDTO(FaceDTO fdto)
	{
		return new Face(fdto.getFid(),
				fdto.getVid(),
				fdto.getFaceData()
				);
	}
	public static FaceDTO toDTO(Face f)
	{
		return new FaceDTO(f.getFid(),
				f.getVid(),
				f.getFaceData()
				);
	}
}
