import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;



/**
 * Created by gonzalobd on 7/03/17.
 */
public class commentInfo extends Thread{
    private static String access_token;
    private ArrayList<Map<String,Object>> commentsReceived = new ArrayList<Map<String,Object>>();
    private ArrayList<Map<String,Object>> commentsSent = new ArrayList<Map<String,Object>>();
    private static final AtomicBoolean closed = new AtomicBoolean(false);



    public commentInfo(String acces_token) throws ExecutionException {
        this.access_token=acces_token;
    }

    private Properties getProducerConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "serializers.JsonSerializer");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        return properties;
    }
    private Properties getConsumerConfig() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group2");
        return properties;
    }

    private KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConsumerConfig());
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
        consumer.subscribe(Collections.singletonList("newMedia"));
        ArrayList<String> mediaList = new ArrayList<String>();


        while (!isInterrupted()) {
            ConsumerRecords<String,String> records2 = consumer.poll(100);
            for (ConsumerRecord<String,String> record : records2) {

                if (!mediaList.contains(record.value())){
                    mediaList.add(record.value());
                    //System.out.println("topic recevied" + record.value());
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
                    Thread.sleep(35000);//hay que dosificar las peticiones, instagram nos permite 5000/hora

                }
                catch (IOException e){
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
