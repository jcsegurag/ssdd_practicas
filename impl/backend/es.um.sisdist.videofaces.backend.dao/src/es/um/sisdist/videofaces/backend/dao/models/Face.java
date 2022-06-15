package es.um.sisdist.videofaces.backend.dao.models;


public class Face {
    private String fid;
    private String vid; 
    private String faceData;
    
    public Face(String fid, String vid, String faceData) {
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
