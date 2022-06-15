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

    
	/*@Override
	public StreamObserver<VideoAndChunkData> processVideo(StreamObserver<Empty> responseObserver)
	{
		// TODO Auto-generated method stub
		return super.processVideo(responseObserver);
	}*/
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
				
				logger.info("------------------------------Add video----------------------------------------------  " + videoSpec.getId());
		    	VideoFaces videofaces = new VideoFaces(videoSpec.getId(), videoSpec.getUid());
		    	videofaces.start();
		    	//Thread thread = new Thread(videofaces);
		    	//thread.start();
			}
		};
	}
	/*@Override
	public void isVideoReady(VideoSpec request, StreamObserver<VideoAvailability> responseObserver)
	{
		// Acceder a la bd a través del dao y comprobar si el video esta procesado
		responseObserver.onNext(VideoAvailability.newBuilder().setAvailable(true).build());
		responseObserver.onCompleted();
	}*/


	/*@Override
	public void storeImage(VideoSpec request, StreamObserver<PetitionAccepted> responseObserver)
    {
		logger.info("Add video " + request.getId());
    	videoMap.put(request.getId(), request);
    	responseObserver.onNext(PetitionAccepted.newBuilder().build());
    	responseObserver.onCompleted();
	}*/

	/*@Override
	public StreamObserver<VideoSpec> storeImages(StreamObserver<PetitionAccepted> responseObserver) 
	{
		// La respuesta, sólo un objeto Empty
		responseObserver.onNext(Empty.newBuilder().build());

		// Se retorna un objeto que, al ser llamado en onNext() con cada
		// elemento enviado por el cliente, reacciona correctamente
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
				logger.info("Add video  " + videoSpec.getId());
		    	int videoId = request.getId();	
			}
		};
	}*/

	/*@Override
	public void obtainImage(VideoSpec request, StreamObserver<PetitionAccepted> responseObserver) {
		// TODO Auto-generated method stub
		super.obtainImage(request, responseObserver);
	}

	@Override
	public StreamObserver<VideoSpec> obtainCollage(StreamObserver<PetitionAccepted> responseObserver) {
		// TODO Auto-generated method stub
		return super.obtainCollage(responseObserver);
	}*/
	
}