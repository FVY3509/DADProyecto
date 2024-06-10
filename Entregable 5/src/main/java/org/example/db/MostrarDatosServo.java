package org.example.db;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

public class MostrarDatosServo implements MyQuery {

	@Override
	public Future<JsonObject> start(SqlClient client) {
		Promise<JsonObject> promise = Promise.promise();
		
		client.query("SELECT * FROM datos_servo").execute().onComplete(ar -> {
			if (ar.succeeded()) {
				RowSet<Row> result = ar.result();
				JsonArray jsonArray = new JsonArray();
				
				for (Row row : result) {
					JsonObject jsonRow = new JsonObject();
					jsonRow.put("grados", row.getInteger("grados").intValue());
					jsonArray.add(jsonRow);
				}
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.put("Datos Servo", jsonArray);
				promise.complete(jsonObject);
				
			} else {
				promise.fail("Fallo: " + ar.cause().getMessage());
			}
		});
		
		return promise.future();
	}

}
