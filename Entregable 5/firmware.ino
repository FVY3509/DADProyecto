#include <Servo.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <NewPing.h>

// Configuración de WiFi
const char* ssid = "";
const char* password = "";

// Configuración de MQTT
const char* mqtt_server = "192.168.1.100";
const int mqtt_port = 1884;
const char* grados_topic = "api/grados";
const char* distancia_topic = "api/distancia";

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

// Inicialización de WiFi y MQTT
WiFiClient espClient;
PubSubClient client(espClient);

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

  // Configuración del cliente MQTT
  client.setServer(mqtt_server, mqtt_port);
  reconnect();
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // Lectura del sensor de proximidad
  float distance = sonar.ping_cm();
  Serial.print("Distance: ");
  Serial.print(distance);
  Serial.println(" cm");
  
  // Enviar datos de distancia al broker MQTT
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
  if (client.connected()) {
    String payload = "{\"grados\":" + String(grados) + "}";
    client.publish(grados_topic, payload.c_str());
    Serial.print("Published grados: ");
    Serial.println(payload);
  } else {
    Serial.println("MQTT not connected");
  }
}

void sendDistance(float distancia) {
  if (client.connected()) {
    String payload = "{\"distancia\":" + String(distancia) + "}";
    client.publish(distancia_topic, payload.c_str());
    Serial.print("Published distancia: ");
    Serial.println(payload);
  } else {
    Serial.println("MQTT not connected");
  }
}

void reconnect() {
  // Intentar conectar al broker MQTT
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Intentar conectar
    if (client.connect("ArduinoClient")) {
      Serial.println("connected");
      // Suscribirse a los tópicos si es necesario
      // client.subscribe("your_topic");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Esperar 5 segundos antes de reintentar
      delay(5000);
    }
  }
}
