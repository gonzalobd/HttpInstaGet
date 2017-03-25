import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gonzalo on 6/03/17.
 */
public class mainClass {

    public static void main (String[] args) throws ExecutionException {

        //TODO
        String token="235583922.e029fea.8f0b40ca9ab9430d8544a5b67aa0bc2d";
        String id="235583922";
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        newMediaGetter get=new newMediaGetter(token,id,queue);
        likeInfo infLik= new likeInfo(token,queue);
        commentInfo infCom = new commentInfo(token,queue);

        get.start();
        infLik.start();
        infCom.start();



    }
}
