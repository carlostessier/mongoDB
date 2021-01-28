package mongodb;


import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Utilidades {

	private  MongoClient clienteMongo;
	private  MongoDatabase database;

	public Utilidades() {
		this("mongodb://localhost:27017");

	}

	public Utilidades(String clienteURI) {
		clienteMongo = new MongoClient(new MongoClientURI(clienteURI));

	}

	public MongoDatabase getConnection(String databaseName) {
		
		database = this.clienteMongo.getDatabase(databaseName);
		return database;
	}

	public MongoCollection<Document> getCollection(String colection) {
		// Seleccionar una Base de datos
		try {
			database.createCollection(colection);
		} catch (MongoCommandException e) {
			System.out.println("Collection "+ colection+" already Exists");
			database.drop();
			database.createCollection(colection);

		}

		return database.getCollection(colection);

	}
	
	public  void closeMongoCliente() {
		System.out.println("Releasing all open resources ...");
		
			if (clienteMongo != null) {
				clienteMongo.close();
				clienteMongo = null;
			}
		
	}
}
