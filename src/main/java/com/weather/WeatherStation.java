package com.weather;
import org.apache.kafka.clients.producer.*;
import java.util.*;

public class WeatherStation {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        // Connect to Kafka (use env variable if available, else localhost)
        String kafkaUrl = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        if (kafkaUrl == null) kafkaUrl = "localhost:9092";

        props.put("bootstrap.servers", kafkaUrl);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        long stationId = 1;
        if (System.getenv("STATION_ID") != null) {
            stationId = Long.parseLong(System.getenv("STATION_ID"));
        }

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            Random rand = new Random();
            long sNo = 1;
            System.out.println("Weather Station " + stationId + " Started...");
            while (true) {
                // Random Battery (30% low, 40% med, 30% high)
                String battery = "medium";
                double b = rand.nextDouble();
                if (b < 0.3) battery = "low";
                else if (b > 0.7) battery = "high";

                // Weather Data
                int humidity = rand.nextInt(100);
                int temp = rand.nextInt(100);
                int wind = rand.nextInt(50);
                long ts = System.currentTimeMillis() / 1000;

                String json = String.format(
                    "{\"station_id\":%d,\"s_no\":%d,\"battery_status\":\"%s\",\"status_timestamp\":%d,\"weather\":{\"humidity\":%d,\"temperature\":%d,\"wind_speed\":%d}}",
                    stationId, sNo, battery, ts, humidity, temp, wind
                );

                // Drop 10% of messages
                if (rand.nextDouble() > 0.1) {
                    producer.send(new ProducerRecord<>("weather-data", json));
                    System.out.println("Sent: " + json);
                } else {
                    System.out.println("Dropped message #" + sNo);
                }
                sNo++;
                Thread.sleep(1000);
            }
        }
    }
}
