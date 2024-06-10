package org.example.mqtt;

import org.example.db.*;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class MainVerticle extends AbstractVerticle {
	
	SqlClient client;
	MqttClient mqttClient;
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		//Opciones de la base de datos 
		MySQLConnectOptions connectOptions = new MySQLConnectOptions()
				.setPort(3306) 
				.setHost("localhost")
				.setDatabase("sistema") 
				.setUser("root") 
				.setPassword("180901"); 
		
		// Pool options
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		// Create the client pool
		this.client =  MySQLBuilder
				.client()
				.with(poolOptions)
				.connectingTo(connectOptions)
				.using(vertx)
				.build();

		//Opciones del broker 
		MqttClientOptions mqttOptions = new MqttClientOptions()
				.setClientId("vertx_client")
				.setAutoKeepAlive(true);

		mqttClient = MqttClient.create(vertx, mqttOptions);
		mqttClient.connect(1884, "localhost", s -> {
			if (s.succeeded()) {
				System.out.println("Connected to MQTT broker");
			} else {
				System.out.println("Failed to connect to MQTT broker");
				startPromise.fail(s.cause());
				return;
			}
		});

		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		router.get("/api/datosServo").handler(ctx -> {
			MyQuery query = new MostrarDatosServo();

			query.start(client).onComplete(ar -> {
				if (ar.succeeded()) {
					ctx.response().putHeader("content-type", "application/json").end(ar.result().encode());
				} else {
					ctx.response().setStatusCode(500).end(ar.cause().getMessage());
				}
			});
		});

		router.get("/api/datosSensor").handler(ctx -> {
			MyQuery query = new MostrarDatosSensor();

			query.start(client).onComplete(ar -> {
				if (ar.succeeded()) {
					ctx.response().putHeader("content-type", "application/json").end(ar.result().encode());
				} else {
					ctx.response().setStatusCode(500).end(ar.cause().getMessage());
				}
			});
		});

		router.post("/api/grados").handler(ctx -> {
			JsonObject jsonBody = ctx.body().asJsonObject();

			int grados = jsonBody.getInteger("grados").intValue();

			Registrar.entradaGrados(client, grados).onComplete(ar -> {
				if (ar.succeeded()) {
					mqttClient.publish("api/grados", Buffer.buffer(jsonBody.encode()), MqttQoS.AT_LEAST_ONCE, false,
							false, pubAck -> {
								if (pubAck.succeeded()) {
									System.out.println("Published to MQTT topic: api/grados");
								} else {
									System.out.println("Failed to publish to MQTT topic: api/grados");
								}
							});
					ctx.response().putHeader("content-type", "application/json").end(ar.result().encode());
				} else {
					ctx.response().setStatusCode(500).end(ar.cause().getMessage());
				}
			});
		});

		router.post("/api/distancia").handler(ctx -> {
			JsonObject jsonBody = ctx.body().asJsonObject();

			float distancia = jsonBody.getFloat("distancia");

			Registrar.entradaDistancia(client, distancia).onComplete(ar -> {
				if (ar.succeeded()) {
					mqttClient.publish("api/distancia", Buffer.buffer(jsonBody.encode()), MqttQoS.AT_LEAST_ONCE, false,
							false, pubAck -> {
								if (pubAck.succeeded()) {
									System.out.println("Published to MQTT topic: api/distancia");
								} else {
									System.out.println("Failed to publish to MQTT topic: api/distancia");
								}
							});
					ctx.response().putHeader("content-type", "application/json").end(ar.result().encode());
				} else {
					ctx.response().setStatusCode(500).end(ar.cause().getMessage());
				}
			});
		});

		router.get("/api/distanciaActual").handler(ctx -> {
			MyQuery query = new UltimaEntradaSensor();

			query.start(client).onComplete(ar -> {
				if (ar.succeeded()) {
					ctx.response().putHeader("content-type", "application/json").end(ar.result().encode());
				} else {
					ctx.response().setStatusCode(500).end(ar.cause().getMessage());
				}
			});
		});

		server.requestHandler(router).listen(8888, http -> {
			if (http.succeeded()) {
				startPromise.complete();
				System.out.println("Servidor HTTP iniciando en el puerto -> 8888");
			} else {
				startPromise.fail(http.cause());
			}
		});

	}

	@Override
	public void stop() throws Exception {
		client.close();
	    if (mqttClient != null && mqttClient.isConnected()) {
	        mqttClient.disconnect();
	    }
	}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
	    vertx.deployVerticle(new MainVerticle());
	}
}
