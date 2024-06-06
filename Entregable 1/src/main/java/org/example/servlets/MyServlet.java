package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.example.model.DB;
import org.json.JSONObject;

public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MyServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject jsonResponse = new JSONObject();
		String nameList = request.getParameter("list");

		if (nameList.equalsIgnoreCase("servo")) {
			jsonResponse.put("Datos Servo", DB.getServoList());

		} else if (nameList.equalsIgnoreCase("sensor")) {
			jsonResponse.put("Datos Sensor", DB.getSensorList());
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.print(jsonResponse.toString());
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// buffer peticion
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		String jsonString = sb.toString();
		JSONObject jsonObject = new JSONObject(jsonString);

		String nameItem = request.getParameter("item");
		JSONObject jsonObjectResponse = new JSONObject();

		if (nameItem.equalsIgnoreCase("servo")) {
			int grados = jsonObject.getInt("grados");

			DB.agregarMedidaServo(grados);

			if (grados != 180)
				jsonObjectResponse.put("message", "llave abierta");
			else
				jsonObjectResponse.put("message", "llave cerrada");

		} else if (nameItem.equalsIgnoreCase("sensor")) {
			float distancia = jsonObject.getFloat("distancia");
			
			DB.agregarMedidaSensor(distancia);

			if (distancia != 10)
				jsonObjectResponse.put("message", "debajo del limite");
			else
				jsonObjectResponse.put("message", "limite alcanzado");
		}

		// formato de la respuesta
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// enviar respuesta
		PrintWriter out = response.getWriter();
		out.print(jsonObjectResponse.toString());
		out.flush();
	}

}
