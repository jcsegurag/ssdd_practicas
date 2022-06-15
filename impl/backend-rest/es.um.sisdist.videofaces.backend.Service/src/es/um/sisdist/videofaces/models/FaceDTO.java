package es.um.sisdist.videofaces.models;


public class FaceDTO {
	private String fid;
	private String vid;
	private String faceData;
	
	
	public FaceDTO(String fid, String vid, String faceData) {
		super();
		this.fid = fid;
		this.vid = vid;
		this.faceData = faceData;
	}
	public String getFid() {
		return fid;
	}
	
	public String getVid() {
		return vid;
	}
	
	public String getFaceData() {
		return faceData;
	}
	
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	public void setVid(String vid) {
		this.vid = vid;
	}
	
	public void setFaceData(String faceData) {
		this.faceData = faceData;
	}

}
