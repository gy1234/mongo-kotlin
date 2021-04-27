package utils

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import entity.IEntity
import org.bson.Document
import org.bson.conversions.Bson

// Map转Document
fun Map<String, Any>.toDocument(): Document {
  return Document().also { doc ->
    this.forEach { k, v ->
      if (v is IEntity<*>) {
        doc.append(k, v.toDoc())
      } else {
        doc.append(k, v)
      }
    }
  }
}

// Map转Filters.add
fun Map<String, *>.toFilters(): Bson {
  return Filters.and(this.map { (k, v) ->
    if (v is Map<*,*>) {
      v.mapKeys { it.key.toString() }.toMatchFilters(k)
    } else Filters.eq(k, v)
  })
}

// Map转Filters.elemMatch
fun Map<String, *>.toMatchFilters(key: String): Bson {
  return Filters.elemMatch(key, this.toFilters())
}

// Map转Updates.combine
fun Map<String, *>.toUpdates(): Bson {
  return Updates.combine(this.map { (k, v) -> Updates.set(k, v) })
}
