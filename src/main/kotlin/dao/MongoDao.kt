package dao

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import utils.toFilters
import utils.toUpdates

fun getClient(connectionStr: String = "mongodb://192.168.98.27:27017"): MongoClient {
  val clientSettings = MongoClientSettings.builder()
    .applyConnectionString(ConnectionString(connectionStr)).build()
  return MongoClients.create(clientSettings)
}

fun getDb(client: MongoClient, dbName: String): MongoDatabase {
  return client.getDatabase(dbName)
}

fun getColl(db: MongoDatabase, collName: String): MongoCollection<Document> {
  return db.getCollection(collName)
}


fun insertOne(coll: MongoCollection<Document>, data: Document): Boolean {
  return coll.insertOne(data).insertedId?.isNull?.not() ?: false
}

fun findOne(coll: MongoCollection<Document>, filter: Map<String, Any>): Document? {
  return coll.find(filter.toFilters()).first()
}

fun findAll(coll: MongoCollection<Document>, filter: Map<String, Any>?=null): List<Document> {
  return coll.find(filter?.toFilters()?:Document()).toList()
}

fun updateOne(
  coll: MongoCollection<Document>,
  filter: Map<String, Any>,
  updates: Map<String, Any>
): Boolean {
  return 0 < coll.updateOne(filter.toFilters(), updates.toUpdates()).modifiedCount
}

fun deleteOne(
  coll: MongoCollection<Document>,
  filter: Map<String, Any>
): Boolean {
  return 0 < coll.deleteOne(filter.toFilters()).deletedCount
}
