package entity

import annotation.NoArgConstructor
import org.bson.Document
import utils.toDocument

@NoArgConstructor
data class Role(
  var id: Int,
  var name: String,
  var vip: Boolean,
  var pos: MutableList<Int>,
  var bag: Bag,
  var heros: MutableMap<Int, Hero>
) : IEntity<Role> {

  override fun toDoc(): Document {
    return Document(
      linkedMapOf<String, Any>(
        "id" to id,
        "name" to name,
        "vip" to vip,
        "bag" to bag.toDoc(),
        "pos" to pos,
        "heros" to heros.mapKeys { it.key.toString() }.toDocument()
      )
    )
  }

  override fun diff(other: Role, parent: String?): LinkedHashMap<String, Any> {
    val prefix = parent?.plus(".") ?: ""

    val roleUpdates = linkedMapOf<String, Any>()
    if (this.name != other.name) roleUpdates[prefix +"name"] = this.name
    if (this.vip != other.vip) roleUpdates[prefix + "vip"] = this.vip
    if (this.pos != other.pos) roleUpdates[prefix + "pos"] = this.pos

    this.bag.diff(other.bag, "bag").let { bagUpdates ->
      if (bagUpdates.isNotEmpty()) roleUpdates += bagUpdates
    }

    val heroPrefix = "heros"
    this.heros.forEach { k, v ->
      other.heros[k]?.let { ov ->
        v.diff(ov, "$heroPrefix.$k").let { heroUpdates ->
          if (heroUpdates.isNotEmpty()) roleUpdates += heroUpdates
        }
      }
    }
    return roleUpdates
  }

  override fun toCopy(): Role {
    return Role(
      id = this.id,
      name = this.name,
      vip = this.vip,
      pos = this.pos.toMutableList(),
      bag = this.bag.toCopy(),
      heros = this.heros.mapValues { it.value.toCopy() }.toMutableMap()
    )
  }

}

@NoArgConstructor
data class Bag(
  var capacity: Int,
  var count: Int,
  var items: MutableList<Item>
): IEntity<Bag> {
  override fun toDoc(): Document {
    return Document(
      linkedMapOf<String, Any>(
        "capacity" to capacity,
        "count" to count,
        "items" to items.map { it.toDoc() }
      )
    )
  }

  override fun diff(other: Bag, parent: String?): LinkedHashMap<String, Any> {
    val prefix = parent?.plus(".") ?: ""
    val bagUpdates = linkedMapOf<String, Any>()
    if (this.capacity != other.capacity) bagUpdates[prefix + "capacity"] = this.capacity
    if (this.count != other.count) bagUpdates[prefix + "count"] = this.count
    return bagUpdates
  }

  override fun toCopy(): Bag {
    return Bag(
      capacity = this.capacity,
      count = this.count,
      items = this.items.map { it.toCopy() }.toMutableList()
    )
  }
}

@NoArgConstructor
data class Hero(
  var id: Int,
  var name: String,
  var level: Int
): IEntity<Hero> {
  override fun toDoc(): Document {
    return Document(
      linkedMapOf<String, Any>(
        "id" to id,
        "name" to name,
        "level" to level
      )
    )
  }

  override fun diff(other: Hero, parent: String?): LinkedHashMap<String, Any> {
    val prefix = parent?.plus(".") ?: ""
    val heroUpdates = linkedMapOf<String, Any>()
    if (this.id != other.id) heroUpdates[prefix + "id"] = this.id
    if (this.name != other.name) heroUpdates[prefix + "name"] = this.name
    if (this.level != other.level) heroUpdates[prefix + "level"] = this.level
    return heroUpdates
  }

  override fun toCopy(): Hero {
    return Hero(
      id = this.id,
      name = this.name,
      level = this.level
    )
  }
}

@NoArgConstructor
data class Item(
  var id: Int,
  var name: String,
  var amount: Int
) : IEntity<Item> {
  override fun toDoc(): Document {
    return Document(
      linkedMapOf<String, Any>(
        "id" to id,
        "name" to name,
        "amount" to amount
      )
    )
  }

  override fun diff(other: Item, parent: String?): LinkedHashMap<String, Any> {
    val prefix = parent?.plus(".") ?: ""
    val itemUpdates = linkedMapOf<String, Any>()
    if (this.id != other.id) itemUpdates[prefix + "id"] = this.id
    if (this.name != other.name) itemUpdates[prefix + "name"] = this.name
    if (this.amount != other.amount) itemUpdates[prefix + "amount"] = this.amount
    return itemUpdates
  }

  override fun toCopy(): Item {
    return Item(
      id = this.id,
      name= this.name,
      amount = this.amount
    )
  }
}