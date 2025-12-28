package com.weather;
import org.apache.kafka.clients.consumer.*;
import org.json.JSONObject;
import java.sql.*;
import java.util.*;
import java.time.Duration;

public class CentralStation {
    public static void main(String[] args) {
        Properties props = new Properties();
        String kafkaUrl = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        if (kafkaUrl == null) kafkaUrl = "localhost:9092";

        props.put("bootstrap.servers", kafkaUrl);
        props.put("group.id", "central-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null) dbUrl = "jdbc:postgresql://localhost:5432/weather_db";

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
             Connection conn = DriverManager.getConnection(dbUrl, "postgres", "weather")) {

            consumer.subscribe(Arrays.asList("weather-data"));
            System.out.println("Central Station listening...");

            String sql = "INSERT INTO weather_readings (station_id, s_no, battery_status, status_timestamp, humidity, temperature, wind_speed) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        JSONObject json = new JSONObject(record.value());
                        pstmt.setLong(1, json.getLong("station_id"));
                        pstmt.setLong(2, json.getLong("s_no"));
                        pstmt.setString(3, json.getString("battery_status"));
                        pstmt.setLong(4, json.getLong("status_timestamp"));

                        JSONObject w = json.getJSONObject("weather");
                        pstmt.setInt(5, w.getInt("humidity"));
                        pstmt.setInt(6, w.getInt("temperature"));
                        pstmt.setInt(7, w.getInt("wind_speed"));

                        pstmt.executeUpdate();
                        System.out.println("Saved message from Station " + json.getLong("station_id"));
                    } catch (Exception e) {
                        System.err.println("Error parsing: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
