package es.um.sisdist.videofaces.backend.grpc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.Thread;
import com.google.protobuf.Empty;

import es.um.sisdist.videofaces.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.videofaces.backend.grpc.VideoAvailability;
import es.um.sisdist.videofaces.backend.grpc.PetitionAccepted;
import es.um.sisdist.videofaces.backend.grpc.VideoAvailabilityOrBuilder;
import es.um.sisdist.videofaces.backend.grpc.VideoSpec;
import es.um.sisdist.videofaces.backend.dao.DAOFactoryImpl;
import es.um.sisdist.videofaces.backend.dao.IDAOFactory;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
import es.um.sisdist.videofaces.backend.facedetect.VideoFaces;
import io.grpc.stub.StreamObserver;

class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase 
{
	private Logger logger;
    IDAOFactory daoFactory;
    IVideoDAO daoVideo;
    public GrpcServiceImpl(Logger logger) 
    {
		super();
		this.logger = logger;
        daoFactory = new DAOFactoryImpl();
        daoVideo = daoFactory.createSQLVideoDAO();
	}

   
    @Override
	public StreamObserver<VideoSpec> processVideo(StreamObserver<PetitionAccepted> responseObserver)
	{
		// Llamar a la funcion de procesar un video
    	
    	responseObserver.onNext(PetitionAccepted.newBuilder().setAccepted(true).build());
    	return new StreamObserver<VideoSpec>() {
			@Override
			public void onCompleted() {
				// Terminar la respuesta.
				responseObserver.onCompleted();
			}
			@Override
			public void onError(Throwable arg0) {
			}
			@Override
			public void onNext(VideoSpec videoSpec) 
			{
				
				logger.info("-----------Add video------- " + videoSpec.getId());
		    	VideoFaces videofaces = new VideoFaces(videoSpec.getId(), videoSpec.getUid());
		    	videofaces.start();
			}
		};
	}
}