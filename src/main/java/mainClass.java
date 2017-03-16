import java.util.concurrent.ExecutionException;

/**
 * Created by gonzalo on 6/03/17.
 */
public class mainClass {

    public static void main (String[] args) throws ExecutionException {

        //TODO
        String token="235583922.e029fea.8f0b40ca9ab9430d8544a5b67aa0bc2d";
        String id="235583922";

        newMediaGetter get=new newMediaGetter(token,id);
        final likeInfo infLik= new likeInfo(token);
        final commentInfo infCom = new commentInfo(token);

        get.start();
        infLik.start();
        infCom.start();



    }
}
