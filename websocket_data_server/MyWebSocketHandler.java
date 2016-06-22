package websocket_data_server;

import java.io.IOException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.*;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

//import FileWatcher;
import java.util.*;
import java.io.*;

@WebSocket
public class MyWebSocketHandler {
    private Session session;
    private KafkaConsumer<String, String> consumer, massConsumer;

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws InterruptedException  {
        this.session = session;
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
//		FileWatcher accelWatcher = new FileWatcher(
//			new File("/users/erikrisinger/development/accel_flink"), 10);
//		FileWatcher hrtWatcher = new FileWatcher(
//					new File("/users/erikrisinger/development/hrt_flink"), 10);
//		ArrayList<String> recentLines = new ArrayList<>();
//		String[] split;
//		accelWatcher.startWatching();
//		long latestTime = 0;

        //kafka consumer code
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test" + System.currentTimeMillis());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        massConsumer = new KafkaConsumer<>(props);
        massConsumer.subscribe(Arrays.asList("sensor-message"));

        this.streamData();
    }

    @OnWebSocketMessage
    public void onMessage(String message) {

        System.out.println(message);
        Properties props = new Properties();

        String[] split = message.split(",");
        if (split.length == 2 && "ID".equals(split[0])){

            //kafka consumer code
            props.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9093");
            props.put("group.id", "test" + System.currentTimeMillis());
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList("subject_" + Integer.parseInt(split[1])));

            this.streamData();
        }
//        System.out.println("Message: " + message);
    }

    public void streamData() {
        String split[];
        long t;
        long latestTime = System.currentTimeMillis();
        long timeOfLastStep = latestTime;
        double x, y, z;

        while (true){

            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                try {
                    this.session.getRemote().sendString(record.value());
//                    System.out.println(record.value());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        //file monitoring/reading code
//        try {
////            session.getRemote().sendString("Hello Webbrowser");
//
//            long t=0;
//			 double x, y, z;
//            x = y = z = 0;
//            while(true){
//				if (accelWatcher == null) {
//					System.out.println("NO WATCHER");
//				}
//				if (accelWatcher.hasRecentLines()) {
//					recentLines = accelWatcher.getRecentLines();
//					for (String s : recentLines) {
//
//						split = s.split(",");
//
//						if (split.length == 4) {
////							System.out.println("good split: " + split);
//							t = Long.parseLong(split[0]);
//
//							if (t > latestTime) {
//								latestTime = t;
//								x = Double.parseDouble(split[1]);
//								y = Double.parseDouble(split[2]);
//								z = Double.parseDouble(split[3]);
////								y = 0.75*Math.sin(x/10.0) + 0.1*(Math.random()-0.5) ;
//
//								String outString = ("{"
//									+ "\"t\":" + t
//									+ ", \"x\":" + x
//									+ ", \"y\":" + y
//									+ ", \"z\":" + z
//									+ "}" );
////								System.out.println(outString);
//								session.getRemote().sendString(outString);
//
//							}
//						}
//						else {
////							System.out.println("bad string: " + s);
//						}
//					}
//				}
//
//				//hrt
//				if (hrtWatcher.hasRecentLines()) {
//					recentLines = hrtWatcher.getRecentLines();
//					for (String s : recentLines) {
//
//						if (!s.equals("")) {
//							x = Double.parseDouble(s);
//							String outString = ("{"
//								+ "\"h\":" + x
//								+ "}" );
//							session.getRemote().sendString(outString);
//						}
//					}
//				}
//				Thread.sleep(10);
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}