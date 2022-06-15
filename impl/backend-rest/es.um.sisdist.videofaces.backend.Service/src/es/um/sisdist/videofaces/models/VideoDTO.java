package es.um.sisdist.videofaces.models;

import es.um.sisdist.videofaces.backend.dao.models.Video.PROCESS_STATUS;
import jakarta.xml.bind.annotation.XmlRootElement;
public class VideoDTO {
	
	private String vid;
	private String uid;
	private String filename;
	private String date;
	private PROCESS_STATUS pstatus;
	
	/**
	 * @return the vid
	 */
	public String getVid()
	{
		return vid;
	}
	
	public String getUid() {
		return uid;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	public String getDate() {
		return date;
	}
	
	public PROCESS_STATUS getPstatus() {
		return pstatus;
	}
	
	public VideoDTO(String vid, String uid, String filename, String date, PROCESS_STATUS pstatus)
	{
		super();
		this.vid = vid;
		this.uid = uid;
		this.filename = filename;
		this.date = date;
		this.pstatus = pstatus;

	}
}
