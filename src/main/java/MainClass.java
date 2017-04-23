import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gonzalo on 6/03/17.
 */
public class MainClass {

    public static void main (String[] args) throws ExecutionException {

        //TODO
        String token=args[1];
        String id=args[0];
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        NewMediaGetter get=new NewMediaGetter(token,id,queue);
        LikeInfo infLik= new LikeInfo(token,queue);
        CommentInfo infCom = new CommentInfo(token,queue);

        get.start();
        infLik.start();
        infCom.start();



    }
}
