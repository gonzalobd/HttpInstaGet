import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gonzalo on 6/03/17.
 */
public class mainClass {

    public static void main (String[] args) throws ExecutionException {

        //TODO
        String token="your-token-here";
        String id="your-id-here";
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        newMediaGetter get=new newMediaGetter(token,id,queue);
        likeInfo infLik= new likeInfo(token,queue);
        commentInfo infCom = new commentInfo(token,queue);

        get.start();
        infLik.start();
        infCom.start();



    }
}
