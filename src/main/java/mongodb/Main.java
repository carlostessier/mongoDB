package mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class Main {

	public static void createDocuments(MongoCollection<Document> collection) {
		Document articulo = new Document();
		articulo.append("articulo", "ABC1").append("detalles", 1d)
				.append("scores",
						new Document("type", "exam")
						.append("modelo", "14Q3")
						.append("proveedor", "XYZ Company"))
				.append("categoria", "ropa")
				.append("stock", asList(new Document("type", "stock")
											.append("talla", "S").append("cantidad", 25),
										new Document("type", "stock")
											.append("talla", "M").append("cantidad", 50)));

		collection.insertOne(articulo);

		List<Document> articulos = new ArrayList<>();

		articulos.add(Document.parse("{" 
				+ "     articulo: 'ABC2'," 
				+ "     detalles: {" 
				+ "        modelo: '14Q3',"
				+ "        proveedor: 'M1 Corporation'" 
				+ "     },"
				+ "     stock: [ { talla: 'S', cantidad: 5 }, { talla: 'L', cantidad: 1 } ]," 
				+ "     categoria: 'ropa'" 
				+ "   }"
				+ "}"));

		articulos.add(Document.parse("{" 
				+ "     articulo: 'MNO2'," 
				+ "     detalles: {" 
				+ "        modelo: '14Q3',"
				+ "        proveedor: 'ABC Company'" 
				+ "     },"
				+ "     stock: [ { talla: 'S', cantidad: 25 }, { talla: 'M', cantidad: 50 } ]," 
				+ "     categoria: 'comida'"
				+ "   }" 
				+ "}"));

		articulos.add(Document.parse("{" + "     articulo: 'IJK2'," 
				+ "     detalles: {" 
				+ "        modelo: '14Q3',"
				+ "        proveedor: 'M5 Corporation'" 
				+ "     },"
				+ "     stock: [ { talla: 'S', cantidad: 5 }, { talla: 'L', cantidad: 1 } ]," 
				+ "     categoria: 'hogar'" 
				+ "   }"
				+ "}"));

		collection.insertMany(articulos);

	}

	public static void main(String[] args) {
		Utilidades utilidades = new Utilidades("mongodb://localhost:27017");
		
		utilidades.getConnection("prueba");
		
		MongoCollection<Document> coleccionInventario = utilidades.getCollection("inventario");

		createDocuments(coleccionInventario);

		System.out.println("Muestra todos los documentos");

		coleccionInventario.find().forEach((Consumer<Document>) doc -> System.out.println(doc.toJson()));
				
		System.out.println("Muestra los documentos cuya categoría sea ropa");
		BasicDBObject query = new BasicDBObject("categoria", "ropa");

		printQuery(coleccionInventario, query);

		System.out.println("Muestra los documentos cuya categoría sea ropa y modelo '14Q3'");

		query = new BasicDBObject();
		query.put("categoria", "ropa");
		query.put("detalles.modelo", "14Q3");

		printQuery(coleccionInventario, query);

		System.out.println("Muestra los documentos cuya categoría sea hogar o comida");
		
		/*	
		BasicDBList or = new BasicDBList();
		or.add(new BasicDBObject("categoria", "hogar"));
		or.add(new BasicDBObject("categoria", "comida"));
		query = new BasicDBObject("$or", or);*/
		
		List<String> list = new ArrayList<String>();
	    list.add("hogar");
	    list.add("comida");
	    query = new BasicDBObject();
	 
	    query.put("categoria", new BasicDBObject("$in", list));
		
		printQuery(coleccionInventario, query);
		
		System.out.println("Muestra los documentos cuya cantidad en stock sea mayor que 10 ó menor o igual que 4");
		
		BasicDBList or = new BasicDBList();
		or.add(new BasicDBObject("stock.cantidad", new BasicDBObject("$gt", 10)));
		or.add(new BasicDBObject("stock.cantidad", new BasicDBObject("$lte", 4)));
		query = new BasicDBObject("$or", or);
		
		printQuery(coleccionInventario, query);

		System.out.println("Muestra los documentos cuyo proveedor sea M1.Coorporation");
		query = new BasicDBObject();
		query.put("detalles.proveedor", "M1 Corporation");
		printQuery(coleccionInventario, query);
		
		System.out.println("Borra los documentos de la categoría hogar");
	
		coleccionInventario.deleteMany(Filters.eq("categoria", "hogar"));		
		query = new BasicDBObject();
		query.put("categoria", "hogar");

		printQuery(coleccionInventario, query);
			
	}
	    
	private static void printQuery(MongoCollection<Document> collection, BasicDBObject whereQuery) {
		collection.find(whereQuery).forEach((Consumer<Document>) doc -> System.out.println(doc.toJson()));
	}
}
