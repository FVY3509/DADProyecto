package org.example.api_rest_db.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlClient;

public interface MyQuery {
	
	Future<JsonObject> start(SqlClient client);

}
