package org.example.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DB {

	private static final List<Sensor> sensorList = new ArrayList<>();
	private static final List<Servo> servoList = new ArrayList<>();

	public static void agregarMedidaServo(int grados) {
		DB.servoList.add(new Servo(grados));
	}

	public static JSONArray getServoList() {
		JSONArray list = new JSONArray();

		for (Servo servo : DB.servoList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("grados", servo.getGrados());
			list.put(jsonObject);
		}

		return list;
	}

	public static void agregarMedidaSensor(float distancia) {
		DB.sensorList.add(new Sensor(distancia));
	}

	public static JSONArray getSensorList() {
		JSONArray list = new JSONArray();

		for (Sensor sensor : DB.sensorList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("distancia", sensor.getDistancia());
			list.put(jsonObject);
		}

		return list;
	}

}
