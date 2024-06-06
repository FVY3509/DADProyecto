#include <Servo.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <NewPing.h>

// Configuración de WiFi
const char* ssid = "";
const char* password = "";

// URLs de los endpoints
const char* gradosEndpoint = "http://localhost:8888/api/grados";
const char* distanciaEndpoint = "http://localhost:8888/api/distancia";

// Pines
#define TRIGGER_PIN 12 // Pin TRIGGER del sensor de proximidad
#define ECHO_PIN 13    // Pin ECHO del sensor de proximidad
#define SERVO_PIN 9    // Pin del servomotor

// Rango máximo para el sensor de proximidad
#define MAX_DISTANCE 200

// Inicialización del sensor de proximidad
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);

// Inicialización del servomotor
Servo myservo;

void setup() {
  Serial.begin(115200);
  myservo.attach(SERVO_PIN);
  
  // Conexión a WiFi
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println(" Connected!");
}

void loop() {
  // Lectura del sensor de proximidad
  float distance = sonar.ping_cm();
  Serial.print("Distance: ");
  Serial.print(distance);
  Serial.println(" cm");
  
  // Enviar datos de distancia al endpoint
  if (distance > 0) {
    sendDistance(distance);
  }
  
  // Control del servomotor
  int grados = map(distance, 0, MAX_DISTANCE, 0, 180);
  myservo.write(grados);
  sendGrados(grados);
  
  delay(5000); // Esperar 5 segundos antes de la siguiente lectura
}

void sendGrados(int grados) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(gradosEndpoint);
    http.addHeader("Content-Type", "application/json");

    String jsonPayload = "{\"grados\":" + String(grados) + "}";
    int httpResponseCode = http.POST(jsonPayload);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println(httpResponseCode);
      Serial.println(response);
    } else {
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  } else {
    Serial.println("Error in WiFi connection");
  }
}

void sendDistance(float distancia) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(distanciaEndpoint);
    http.addHeader("Content-Type", "application/json");

    String jsonPayload = "{\"distancia\":" + String(distancia) + "}";
    int httpResponseCode = http.POST(jsonPayload);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println(httpResponseCode);
      Serial.println(response);
    } else {
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  } else {
    Serial.println("Error in WiFi connection");
  }
}
