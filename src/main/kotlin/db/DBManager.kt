package db

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import mu.KotlinLogging
import org.litote.kmongo.KMongo

private val logger = KotlinLogging.logger { }

object DBManager {
    private var mongoClient: MongoClient
    var database: MongoDatabase

    init {
        logger.debug { "Iniciando conexión a MongoDB" }
        mongoClient = KMongo.createClient("mongodb://mongoadmin:mongopass@localhost/Practicar?authSource=admin")
        database = mongoClient.getDatabase("Practicar")
    }
}