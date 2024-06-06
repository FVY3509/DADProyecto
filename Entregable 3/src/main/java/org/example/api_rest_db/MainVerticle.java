package org.example.api_rest_db;

import org.example.api_rest_db.MainVerticle;
import org.example.api_rest_db.db.*;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class MainVerticle extends AbstractVerticle {
	
	private SqlClient client;
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		MySQLConnectOptions connectOptions = new MySQLConnectOptions()
				.setPort(3306) //puerto de conexion al servicio de la base de datos 
				.setHost("localhost")
				.setDatabase("sistema") //base de datos 
				.setUser("root") //usuario de la base 
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
		
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());
		
		router.get("/api/datosServo").handler(ctx -> { 
			
			MyQuery query = new MostrarDatosServo(); //obtener el historial de datos del componente servo motor 
			
			query.start(client).onComplete(ar -> {
				if (ar.succeeded()) {
                    ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(ar.result().encode());
                } else {
                    ctx.response()
                        .setStatusCode(500)
                        .end(ar.cause().getMessage());
                }
			});
		});

		router.get("/api/datosSensor").handler(ctx -> { 
			
			MyQuery query = new MostrarDatosSensor(); //obtener el historial de datos del componente sensor de proximidad
			
			query.start(client).onComplete(ar -> {
				if (ar.succeeded()) {
                    ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(ar.result().encode());
                } else {
                    ctx.response()
                        .setStatusCode(500)
                        .end(ar.cause().getMessage());
                }
			});
		});

		router.post("/api/grados").handler(ctx -> { 
			
			JsonObject jsonBody = ctx //cuerpo de la peticion
					.body()
					.asJsonObject();
			
			int grados = jsonBody.getInteger("grados").intValue();
			
			Registrar.entradaGrados(client, grados).onComplete(ar -> { //insertar nueva entrada de grados a la base de datos 
				if (ar.succeeded()) {
                    ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(ar.result().encode());
                } else {
                    ctx.response()
                        .setStatusCode(500)
                        .end(ar.cause().getMessage());
                }
			}); 
		});
		
		router.post("/api/distancia").handler(ctx -> { 
			
			JsonObject jsonBody = ctx //cuerpo de la peticion 
					.body()
					.asJsonObject();
			
			float distancia = jsonBody.getFloat("distancia");
			
			Registrar.entradaDistancia(client, distancia).onComplete(ar -> {	//insertar nueva entrada del sensor a la base de datos 
				if (ar.succeeded()) {
                    ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(ar.result().encode());
                } else {
                    ctx.response()
                        .setStatusCode(500)
                        .end(ar.cause().getMessage());
                }
			}); 
		});
		
		router.get("/api/distanciaActual").handler(ctx -> { 
			
			MyQuery query = new UltimaEntradaSensor(); //ultimo registro del sensor de proximidad
			
			query.start(client).onComplete(ar -> {
				if (ar.succeeded()) {
                    ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(ar.result().encode());
                } else {
                    ctx.response()
                        .setStatusCode(500)
                        .end(ar.cause().getMessage());
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
	public void stop() {
		client.close();
	}
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}
}
