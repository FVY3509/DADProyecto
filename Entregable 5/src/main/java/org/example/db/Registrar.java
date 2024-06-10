package org.example.db;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public class Registrar {

	public static Future<JsonObject> entradaDistancia(SqlClient client, float distancia) {
		Promise<JsonObject> promise = Promise.promise();

		client.preparedQuery("INSERT INTO datos_sensor (distancia) VALUES (?)").execute(Tuple.of(distancia))
				.onComplete(ar -> {
					if (ar.succeeded()) {
						RowSet result = ar.result();
						JsonObject jsonObject = new JsonObject();
						if (distancia < 10) {
							jsonObject.put("mensaje", "debajo del limite");
						} else {
							jsonObject.put("mensaje", "limite alcanzado");
						}
						promise.complete(jsonObject);
					} else {
						promise.fail("Fallo: " + ar.cause().getMessage());
					}
				});

		return promise.future();
	}

	public static Future<JsonObject> entradaGrados(SqlClient client, int grados) {
		Promise<JsonObject> promise = Promise.promise();

		client.preparedQuery("INSERT INTO datos_servo (grados) VALUES (?)").execute(Tuple.of(grados)).onComplete(ar -> {
			if (ar.succeeded()) {
				RowSet result = ar.result();
				JsonObject jsonObject = new JsonObject();
				if (grados != 180) {
					jsonObject.put("mensaje", "llave abierta");
				} else {
					jsonObject.put("mensaje", "limite cerrada");
				}
				promise.complete(jsonObject);
			} else {
				promise.fail("Fallo: " + ar.cause().getMessage());
			}
		});

		return promise.future();
	}

}
