CREATE SCHEMA IF NOT EXISTS WeatherData;
USE WeatherData;

CREATE TABLE WeatherDataAS01 (
    record_id INT PRIMARY KEY,
    city VARCHAR(100),
    country VARCHAR(100),
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    date DATE,
    temperature_celsius INT,
    humidity_percent INT,
    precipitation_mm DECIMAL(10, 2),
    wind_speed_kmh INT,
    weather_condition VARCHAR(100),
    forecast VARCHAR(255),
    updated DATE
);

/*INSERTANDO DATOS PRUEBA*/
GRANT ALL PRIVILEGES ON `WeatherData`.* TO 'root'@'localhost';

INSERT INTO WeatherDataAS01 (
    record_id, city, country, latitude, longitude, date,
    temperature_celsius, humidity_percent, precipitation_mm,
    wind_speed_kmh, weather_condition, forecast, updated
) VALUES 
(401, 'Madrid', 'Spain', 40.4168, -3.7038, '2023-11-10', 15, 65, 2.5, 20, 'Cloudy', 'Light rain expected in the afternoon', '2023-11-10'),
(402, 'Valencia', 'Spain', 39.4699, -0.3763, '2023-11-10', 18, 72, 0.0, 15, 'Partly cloudy', 'Clear skies expected throughout the day', '2023-11-10'),
(403, 'Barcelona', 'Spain', 41.3784, 2.1925, '2023-11-10', 17, 78, 0.1, 10, 'Light rain', 'Scattered showers throughout the day', '2023-11-10'),
(404, 'Alicante', 'Spain', 38.3452, -0.4810, '2023-11-10', 21, 58, 0.0, 25, 'Sunny', 'Clear skies with no precipitation', '2023-11-10'),
(405, 'Castellón de la Plana', 'Spain', 39.9860, -0.0376, '2023-11-10', 14, 85, 5.2, 18, 'Heavy rain', 'Rain expected to continue until evening', '2023-11-10');

SELECT * FROM WeatherDataAS01
/*
DROP TABLE IF EXISTS WeatherDataAS01;
*/
/*
DELETE FROM WeatherDataAS01;
*/
