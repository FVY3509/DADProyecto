package com.example.api_rest;

import com.example.api_rest.model.DB;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());
		
		router.get("/api/datosServo").handler(ctx -> { //mostrar historial de los datos del servo motor
			
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.put("Datos Servo", DB.getServoList());
			
			ctx.response()
				.putHeader("content-type", "application/json")
				.end(jsonResponse.encode());
		});

		router.get("/api/datosSensor").handler(ctx -> { //mostrar historial de los datos del sensor de proximidad
			
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.put("Datos Sensor", DB.getSensorList());
			
			ctx.response()
				.putHeader("content-type", "application/json")
				.end(jsonResponse.encode());
		});

		router.post("/api/grados").handler(ctx -> { //insertar nueva entrada de grados 
			
			JsonObject jsonBody = ctx
					.body()
					.asJsonObject();
			
			int grados = jsonBody.getInteger("grados").intValue();
			
			DB.agregarMedidaServo(grados);
			
			String responseMessage;
			
			responseMessage = (grados != 180) 
					? "{\"mensaje\": \"llave abierta\"}" 
					: "{\"mensaje\": \"llave cerrada\"}";
			
			ctx.response()
				.putHeader("content-type", "application/json")
				.end(responseMessage);
		});
		
		router.post("/api/distancia").handler(ctx -> { //insertar nueva entrada del sensor 
			
			JsonObject jsonBody = ctx
					.body()
					.asJsonObject();
			
			float distancia = jsonBody.getFloat("distancia");
			
			DB.agregarMedidaSensor(distancia);
			
			String responseMessage;
			
			 if (distancia < 10)
				 responseMessage = "{\"mensaje\": \"debajo del limite\"}";
			 else
				 responseMessage = "{\"mensaje\": \"ha alcanzado el limite\"}";
			
			ctx.response()
				.putHeader("content-type", "application/json")
				.end(responseMessage);
		});
		
		router.get("/api/distanciaActual").handler(ctx -> { //ultima entrada obtenida por el sensor de proximidad
			
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.put("distancia", DB.ultimaEntradaSensor());
			
			ctx.response()
				.putHeader("content-type", "application/json")
				.end(jsonResponse.encode());
		});
		
		server.requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
            } else {
                startPromise.fail(http.cause());
            }
        });
	}
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}
}
