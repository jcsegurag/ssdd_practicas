package es.um.sisdist.videofaces.backend.facedetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.InputStream;
import java.util.logging.Logger;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplay.EndAction;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.VideoPositionListener;
import org.openimaj.video.xuggle.XuggleVideo;
import es.um.sisdist.videofaces.backend.dao.DAOFactoryImpl;
import es.um.sisdist.videofaces.backend.dao.IDAOFactory;
//import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
/**
 * OpenIMAJ Hello world!
 *
 */
public class VideoFaces extends Thread
{
    private static final Logger logger = Logger.getLogger(VideoFaces.class.getName());
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private String uid;
	private String id;
	private final AtomicBoolean running = new AtomicBoolean(false);
    IVideoDAO daoVideo;
    IDAOFactory daoFactory;
	public VideoFaces(String id, String uid) {
		this.id = id;
		this.uid = uid;
		daoFactory = new DAOFactoryImpl();
		daoVideo = daoFactory.createSQLVideoDAO();
		
	}
	
	 private static void copyInputStreamToFile(InputStream inputStream, String videoPath)
	            throws IOException {

	        // append = false
	        try (FileOutputStream outputStream = new FileOutputStream(videoPath, false)) {
	            int read;
	            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
	            while ((read = inputStream.read(bytes)) != -1) {
	                outputStream.write(bytes, 0, read);
	            }
	        } catch (Exception e) {
				// TODO: handle exception
			}

	    }
	 
	 private void saveImages(final File folder, String vid) throws FileNotFoundException {
		    for (final File imagen : folder.listFiles()) {
		    	InputStream datosImagen = new FileInputStream(imagen);
		    		daoVideo.saveImage(vid, datosImagen);
		            //System.out.println(fileEntry.getName());
		        }
	}
	 
	   /* public void stop() {
	        running.set(false);
	    }*/
	public void run() {
		// VideoCapture vc = new VideoCapture( 320, 240 );
        // VideoDisplay<MBFImage> video = VideoDisplay.createVideoDisplay( vc );
		//Optional<Blob> videoData = daoVideo.getVideoById(id);
		//Blob videoD = daoVideo.getVideoById(id);
		logger.info("-----------------------------------------------------------------ESTAMOS EN EL HILO -----------------------------------------------------------------");

    	String videoDir = "/tmp/"+uid+"/videos/";
		File videos = new File(videoDir);
		if(!videos.exists()) {
			videos.mkdirs();
		}
    	String facesDir = "/tmp/"+uid+"/faces/";
		File faces = new File(facesDir);
		if(!faces.exists()) {
			faces.mkdirs();
		}
    			
    	//String videoPath = "/tmp/"+uid+"/"+id+".mp4";
		String videoPath = videoDir+id+".mp4";
		InputStream videoStream = daoVideo.getStreamForVideo(id);
		try {
			copyInputStreamToFile(videoStream, videoPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//XuggleVideo xuggleVideo = new XuggleVideo(videoStream);
		 Video<MBFImage> video = new XuggleVideo(videoPath);
		//Video<MBFImage> videoImagen = xuggleVideo;
		VideoDisplay<MBFImage> vd = VideoDisplay.createOffscreenVideoDisplay(video);
        // El Thread de procesamiento de vídeo se termina al terminar el vídeo.
        vd.setEndAction(EndAction.CLOSE_AT_END);
        vd.addVideoListener(new VideoDisplayListener<MBFImage>() {
            // Número de imagen
            int imgn = 0;

		@Override
        public void beforeUpdate(MBFImage frame)
        {
            FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(40);
            List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));

            for (DetectedFace face : faces)
            {
                frame.drawShape(face.getBounds(), RGBColour.RED);
                try
                {
                    // También permite enviar la imagen a un OutputStream
                    ImageUtilities.write(frame.extractROI(face.getBounds()),
                            new File(String.format(facesDir+"img%05d.jpg", imgn++)));
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("!");
            }
        }

        @Override
        public void afterUpdate(VideoDisplay<MBFImage> display)
        {
        }
    });

    vd.addVideoPositionListener(new VideoPositionListener() {
        @Override
        public void videoAtStart(VideoDisplay<? extends Image<?, ?>> vd)
        {
        }

        @Override
        public void videoAtEnd(VideoDisplay<? extends Image<?, ?>> vd)
        {
            System.out.println("End of video");
            try {
				saveImages(faces, id);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            File videoRemove = new File(videoPath);
            videoRemove.delete();
            File imagenes = new File(facesDir);
            imagenes.delete();
        }
    });

    System.out.println("Fin.");
    }
}

