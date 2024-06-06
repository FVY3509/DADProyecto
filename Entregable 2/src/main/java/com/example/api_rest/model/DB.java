package com.example.api_rest.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DB {

	private static final List<Sensor> sensorList = new ArrayList<>();
	private static final List<Servo> servoList = new ArrayList<>();

	public static void agregarMedidaServo(int grados) {
		DB.servoList.add(new Servo(grados));
	}

	public static JsonArray getServoList() {
		JsonArray list = new JsonArray();

		for (Servo servo : DB.servoList) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.put("grados", servo.getGrados());
			list.add(jsonObject);
		}

		return list;
	}
	
	public static void agregarMedidaSensor(float distancia) {
		DB.sensorList.add(new Sensor(distancia));
	}

	public static JsonArray getSensorList() {
		JsonArray list = new JsonArray();

		for (Sensor sensor : DB.sensorList) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.put("distancia", sensor.getDistancia());
			list.add(jsonObject);
		}

		return list;
	}
	
	public static float ultimaEntradaSensor() {
		int index = DB.sensorList.size() - 1;
		if (!DB.sensorList.isEmpty())
			return DB.sensorList.get(index).getDistancia();
		else
			return 0;
	}

}
