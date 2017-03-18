import java.util.concurrent.ExecutionException;

/**
 * Created by gonzalo on 6/03/17.
 */
public class mainClass {

    public static void main (String[] args) throws ExecutionException {

        //TODO
        String token="your-token-here";
        String id="your-id-here";

        newMediaGetter get=new newMediaGetter(token,id);
        final likeInfo infLik= new likeInfo(token);
        final commentInfo infCom = new commentInfo(token);

        get.start();
        infLik.start();
        infCom.start();



    }
}
