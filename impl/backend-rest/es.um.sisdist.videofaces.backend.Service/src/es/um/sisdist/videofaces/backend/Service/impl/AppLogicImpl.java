/**
 *
 */
package es.um.sisdist.videofaces.backend.Service.impl;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;

import es.um.sisdist.collage.CollageClient;
import es.um.sisdist.collage.ImageData;
import es.um.sisdist.videofaces.backend.dao.DAOFactoryImpl;
import es.um.sisdist.videofaces.backend.dao.IDAOFactory;
import es.um.sisdist.videofaces.backend.dao.models.User;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.backend.dao.user.IUserDAO;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
import es.um.sisdist.videofaces.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.videofaces.backend.grpc.VideoAvailability;
import es.um.sisdist.videofaces.backend.grpc.VideoSpec;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/**
 * @author dsevilla
 *
 */
public class AppLogicImpl
{
    IDAOFactory daoFactory;
    IUserDAO daoUser;
    IVideoDAO daoVideo;

    private static final Logger logger = Logger.getLogger(AppLogicImpl.class.getName());

    private final ManagedChannel channel;
    private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;
    private final GrpcServiceGrpc.GrpcServiceStub asyncStub;

    static AppLogicImpl instance = new AppLogicImpl();

    private AppLogicImpl()
    {
        daoFactory = new DAOFactoryImpl();
        daoUser = daoFactory.createSQLUserDAO();
        daoVideo = daoFactory.createSQLVideoDAO();

        Optional<String> grpcServerName = Optional.ofNullable(System.getenv("GRPC_SERVER"));
        Optional<String> grpcServerPort = Optional.ofNullable(System.getenv("GRPC_SERVER_PORT"));

        channel = ManagedChannelBuilder
                .forAddress(grpcServerName.orElse("localhost"), Integer.parseInt(grpcServerPort.orElse("50051")))
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build();
        blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
        asyncStub = GrpcServiceGrpc.newStub(channel);
    }

    public static AppLogicImpl getInstance()
    {
        return instance;
    }

    public Optional<User> getUserByEmail(String userId)
    {
        Optional<User> u = daoUser.getUserByEmail(userId);
        return u;
    }

    public Optional<User> getUserById(String userId)
    {
        return daoUser.getUserById(userId);
    }

    public boolean isVideoReady(String videoId)
    {
        // Test de grpc, puede hacerse con la BD
        VideoAvailability available = blockingStub.isVideoReady(VideoSpec.newBuilder().setId(videoId).build());
        return available.getAvailable();
    }

    // El frontend, a través del formulario de login,
    // envía el usuario y pass, que se convierte a un DTO. De ahí
    // obtenemos la consulta a la base de datos, que nos retornará,
    // si procede,
    public Optional<User> checkLogin(String email, String pass)
    {
        Optional<User> u = daoUser.getUserByEmail(email);

        if (u.isPresent())
        {
            String hashed_pass = User.md5pass(pass);
            if (0 == hashed_pass.compareTo(u.get().getPassword_hash()))
                return u;
        }

        return Optional.empty();
    }
    
 // Registro de usuario
    public Optional<User> registerUser(String email, String username, String password) {
    	return daoUser.createUser(email, username, password);
    }
 
    public Optional<User> checkRegister(String email)
    {
    	Optional<User> u = daoUser.getUserByEmail(email);
    	if(u.isPresent())
    	{
			return u;
    	}
    	
    	else return Optional.empty();

    }
    /** Send images. */
    public void sendVideoGrpc(Optional<Video> video)
    {
  	  // Imágenes para enviar
  	  VideoSpec video = VideoSpec.newBuilder().setid(video.getId()).setuid(video.getUserid()).build();
  	  
  	  try {
  		  blockingStub.processVideo(video);		  
  	  } catch (StatusRuntimeException e) {
  		  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
  		  return;
  	  }

  	  // Stream
  	  try {
  		  final CountDownLatch finishLatch = new CountDownLatch(1);
  		  
  		  StreamObserver<PetitionAccepted> soPetitionAccepted= new StreamObserver<PetitionAccepted>() {

  			  @Override
  			  public void onNext(Empty value) {
  			  }

  			  @Override
  			  public void onError(Throwable t) {
  				  finishLatch.countDown();
  			  }

  			  @Override
  			  public void onCompleted() {
  				  finishLatch.countDown();
  			  }
  		  };
  		  
  		  StreamObserver<VideoSpec> so = asyncStub.storeImages(soPetitionAccepted);
  		  so.onNext(video);
  		  so.onCompleted();
  		  
  		  // Esperar la respuesta
  		  if (finishLatch.await(1, TimeUnit.SECONDS))
  			  logger.info("Received response.");
  		  else
  			  logger.info("Not received response!");
  		  
  	  } catch (StatusRuntimeException e) {
  		  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
  		  return;
  	  } catch (InterruptedException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}

    }
    public Optional<Video> uploadVideo(String filename, String uid, InputStream fileInputStream){
    	Optional<Video> video = daoVideo.saveVideo(uid, LocalDateTime.now(), filename, fileInputStream);
    	
    	return daoVideo.saveVideo(uid, LocalDateTime.now(), filename, fileInputStream);
    }
    
}
