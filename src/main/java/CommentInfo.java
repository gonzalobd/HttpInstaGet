import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;



/**
 * Created by gonzalobd on 7/03/17.
 */
public class CommentInfo extends Thread{
    private static String access_token;
    private ArrayList<Map<String,Object>> commentsReceived = new ArrayList<Map<String,Object>>();
    private ArrayList<Map<String,Object>> commentsSent = new ArrayList<Map<String,Object>>();
    private static final AtomicBoolean closed = new AtomicBoolean(false);
    LinkedBlockingQueue<String> queue;




    public CommentInfo(String acces_token, LinkedBlockingQueue<String> queue) throws ExecutionException {
        this.access_token=acces_token;
        this.queue=queue;
    }

    private Properties getProducerConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "serializers.JsonSerializer");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        return properties;
    }


    private Producer<String, Map<String,Object>> producer = new KafkaProducer<>(getProducerConfig());

    @Override
    public  void run() {

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println("Shutting down");
                closed.set(true);
            }
        });
        ArrayList<String> mediaList = new ArrayList<String>();


        while (!isInterrupted()) {


            Object[] pics=queue.toArray();
            //System.out.println("images received for comment analytics:   "+pics );

            if (pics.length<=20) {

                for (int i = 0; i < pics.length; i++) {
                    if (!mediaList.contains(pics[i])) {
                        mediaList.add(pics[i].toString());
                    }
                }
            }

            //Queremos informacion solo de las 20 ultimas fotos
            //Si ya van mas de 20 fotos hay que eliminar las mas antiguas del registro

            if (pics.length>20){
                for (int i = 0; i < pics.length; i++) {
                    if (!mediaList.contains(pics[i])) {
                        mediaList.add(0,pics[i].toString());
                    }
                }

                while (mediaList.size()>20) {
                    mediaList.remove(mediaList.size()-1);
                }

            }

            for (int i=0;i<mediaList.size();i++){

                String stringUrl ="https://api.instagram.com/v1/media/"+mediaList.get(i)+"/comments?access_token="+access_token;

                try{

                    URL url = new URL(stringUrl);
                    URLConnection uc = url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader((uc.getInputStream())));
                    ObjectMapper mapper = new ObjectMapper();
                    String json = br.readLine();
                    Map<String, Object> map = new HashMap<String, Object>();
                    // convert JSON string to Map
                    map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
                    commentsReceived=(ArrayList<Map<String,Object>>) map.get("data");



                    for (Map<String,Object> comment:commentsReceived){

                        Map<String,Object> oneComment =new HashMap<String,Object>();

                        oneComment.put("username",((Map)comment.get("from")).get("username"));
                        oneComment.put("media",mediaList.get(i));
                        oneComment.put("text",comment.get("text"));
                        oneComment.put("created_time",comment.get("created_time"));

                        if (!commentsSent.contains(oneComment)){

                            producer.send(new ProducerRecord<>("comment","Comment", oneComment));
                            commentsSent.add(oneComment);

                        }

                    }

                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(35000);//hay que dosificar las peticiones, instagram nos permite 5000/hora
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
