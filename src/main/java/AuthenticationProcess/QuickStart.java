package AuthenticationProcess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;

public class QuickStart {
    Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static void main( String[] args ) {
        // Replace the placeholder with your MongoDB deployment's connection string
        String uri = "mongodb+srv://admin:admin@user.gzuhqty.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("sample_analytics");
            if (database != null) {
                // แสดงชื่อฐานข้อมูล
                System.out.println("Database Name: " + database.getName());
                // แสดงรายชื่อของ Collections ทั้งหมดในฐานข้อมูล
                MongoIterable<String> collectionNames = database.listCollectionNames();
                System.out.println("Collections: ");
                for (String collectionName : collectionNames) {
                    System.out.println(collectionName);
                }
            } else {
                System.out.println("No matching database found.");
            }

            MongoCollection<Document> collection = database.getCollection("accounts");
            FindIterable<Document> documents = collection.find();
//            // วนลูปผลลัพธ์ทั้งหมดและแสดงข้อมูล
//            for (Document document : documents) {
//                System.out.println(document.toJson());
//            }

            Bson filter = eq("account_id", 371138);
            Document doc = collection.find(filter).first();
            if (doc != null) {
                System.out.println("ข้อมูลที่พบ" + doc.toJson());
            } else {
                System.out.println("No matching documents found.");
            }
        }
    }
}
