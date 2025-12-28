-- Battery Status Query
SELECT battery_status, COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() as percentage 
FROM weather_readings GROUP BY battery_status;

-- Dropped Messages Query
SELECT station_id, MAX(s_no) - COUNT(*) as dropped_count 
FROM weather_readings GROUP BY station_id;
