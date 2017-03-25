/**
 * Created by gonzalobd on 6/03/17.
 */

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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

public class newMediaGetter extends Thread {
    private ArrayList<Map<String,Object>> mediaFullList;
    private static String access_token;
    private static String id;
    LinkedBlockingQueue<String> queue;
    private ArrayList<String> mediaToSend =new ArrayList<String>();
    private static final AtomicBoolean closed = new AtomicBoolean(false);

    private Properties getProducerConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        return properties;
    }

    Producer<String, String> producer = new KafkaProducer<>(getProducerConfig());

    public newMediaGetter(String acces_token, String id, LinkedBlockingQueue<String> queue) throws  ExecutionException{
        this.access_token=acces_token;
        this.id=id;
        this.queue=queue;

    }

    @Override
    public  void run() {

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {

                System.out.println("Shutting down");
                closed.set(true);
            }
        });
        String stringUrl = "https://api.instagram.com/v1/users/"+id+"/media/recent/?access_token="+access_token;

        while (!isInterrupted()) {
            mediaFullList=null;

            try {
                URL url = new URL(stringUrl);
                URLConnection uc = url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader((uc.getInputStream())));
                ObjectMapper mapper = new ObjectMapper();
                String json = br.readLine();
                Map<String, Object> map = new HashMap<String, Object>();
                // convert JSON string to Map
                map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

                mediaFullList=(ArrayList<Map<String,Object>>) map.get("data");

                for(int i=0;i<mediaFullList.size();i++){

                    //System.out.println((mediaFullList.get(i)).get("id"));
                    if (!mediaToSend.contains((mediaFullList.get(i)).get("id").toString())){
                    mediaToSend.add((mediaFullList.get(i)).get("id").toString());
                    producer.send(new ProducerRecord<String, String>("newMedia","media",(mediaFullList.get(i)).get("id").toString()));
                    queue.add((mediaFullList.get(i)).get("id").toString());
                    }
                }

                Thread.sleep(60000); //hay que dosificar las peticiones, instagram nos permite 5000/hora

            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
