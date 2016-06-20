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

    KafkaConsumer<String, String> consumer;

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
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
//		FileWatcher accelWatcher = new FileWatcher(
//			new File("/users/erikrisinger/development/accel_flink"), 10);
//		FileWatcher hrtWatcher = new FileWatcher(
//					new File("/users/erikrisinger/development/hrt_flink"), 10);
//
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
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("sensor-message"));

        this.streamData(session);
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
    }

    public void streamData(Session session) {
        String split[];
        long t;
        long latestTime = System.currentTimeMillis();
        long timeOfLastStep = latestTime;
        double x, y, z;

        while (true){

            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {

                try {
                    session.getRemote().sendString(record.value());
//                    System.out.println(record.value());
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
//                JSONObject jsonOut = new JSONObject();
//                split = record.value().split(",");
//
//                if (split.length == 4) {
////                    System.out.println("good split: " + split);
//                    t = Long.parseLong(split[0]);
////
//                    if (t > latestTime) {
//                        latestTime = t;
//                        x = Double.parseDouble(split[1]);
//                        y = Double.parseDouble(split[2]);
//                        z = Double.parseDouble(split[3]);
//////								y = 0.75*Math.sin(x/10.0) + 0.1*(Math.random()-0.5) ;
//
////                        int step = Math.random() > 0.96 ? 0 : -1;
////                        double step = Math.abs(Math.max(Math.max(x, y), z));
////                        long now = System.currentTimeMillis();
//
////                        if (step >= 15 && now - timeOfLastStep > 500){
////                            timeOfLastStep = now;
////                            outString = ("{" + "\"t\":" + t
////                                    + ", \"s\":" + (x + y + z) / 3
//////                                    + ", \"s\":" + 0
////                                    + "}"
////                            );
////
////                        }
////                        else {
//                        jsonOut.put("user_id", 0);
//                        jsonOut.put("device_type", "MS_BAND_1");
//                        jsonOut.put("t", t);
//                        jsonOut.put("x", x);
//                        jsonOut.put("y", y);
//                        jsonOut.put("z", z);
//
//
////                        String outString;
////                        outString = ("{"
////                                + "\"t\":" + t
////                                + ", \"x\":" + x
////                                + ", \"y\":" + y
////                                + ", \"z\":" + z
////                                + "}" );
////                        }
//
////                        System.out.println("SENDING");
//                        try {
//                            session.getRemote().sendString(jsonOut.toJSONString());
////                            session.getRemote().sendString("length: 4");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
////
////
////
//                    }
//                }
//                else if (split.length == 2){
////                    System.out.println("STEP RECEIVED: " + split.toString());
//                    String outString = "";
////                        int step = Math.random() > 0.96 ? 0 : -1;
//                    long now = System.currentTimeMillis();
//
//                    if (now - timeOfLastStep > 500){
//                        timeOfLastStep = now;
//                        outString = ("{" + "\"t\":" + Long.parseLong(split[0])
//                                + ", \"s\":" + Double.parseDouble(split[1]) / 3
//                                + "}"
//                        );
//
//                    }
//                    try {
//                        session.getRemote().sendString(outString);
////                        session.getRemote().sendString("length: 2");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
//
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