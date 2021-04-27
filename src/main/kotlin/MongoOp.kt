import codec.JacksonCodec
import com.mongodb.client.MongoCollection
import dao.*
import entity.*
import org.bson.Document
import utils.toDocument
import kotlin.random.Random
import kotlin.random.nextInt


/**
 * 2021-4-23
 * MongoDao基本操作测试
 */
fun main() {
  val client = getClient()
  val mongoDb = getDb(client, "got")
  val roleColl = getColl(mongoDb, "player")
  val random = Random(System.currentTimeMillis()).nextInt(1..9999)
  val role = Role(
    id = 10000 + random,
    name = "班纳$random",
    vip = random % 2 == 0,
    pos = arrayListOf(10, 30, 40),
    bag = Bag(
      capacity = 2000 + random,
      count = 100 + random,
      items = arrayListOf(
        Item(6001, "武器", 3 * random),
        Item(6002, "衣服", 5 * random),
        Item(6003, "鞋子", 10 * random)
      )
    ),
    heros = hashMapOf(
      1001 to Hero(1001, "刘备", 5 * random),
      1002 to Hero(1002, "关羽", 15 * random),
      1003 to Hero(1003, "张飞", 25 * random)
    )
  )
  insertOne(roleColl, role.toDoc()).let { println("\ninsert result:" + if (it) "success" else "failed") }

  val idFilters = linkedMapOf<String, Any>()
  filterRoleId(role.id).append2Map(idFilters)
  findOne(roleColl, idFilters)?.let { println("find result:" + it.toJson()) } ?: return

  pojo4json(roleColl, random, role, JacksonCodec())// 使用json序列化

//  pojo4copy(roleColl, random, role)// 使用对象深copy
//
//  daoOp(roleColl, random, role)// 使用原生的document

  deleteOne(roleColl, idFilters).let { println("\ndelete result:" + if (it) "success" else "failed") }
}

fun pojo4json(
  roleColl: MongoCollection<Document>,
  random: Int,
  role: Role,
  codec: ICodec
) {
  val idFilters = linkedMapOf<String, Any>()
  filterRoleId(role.id).append2Map(idFilters)

  val old = codec.doEncode(role)
  println("json char size:" + old.size)
  role.name = "爱丽丝$random"
  role.pos.add(50)
  role.pos.remove(10)
  role.bag.capacity = 8888
  role.heros[1003]?.name = "吕布"
  val last = codec.doDecode(old, Role::class.java)
  role.diff(other = last).let { updates ->
    updateAndFind(roleColl, idFilters, updates)
  }

}

fun pojo4copy(
  roleColl: MongoCollection<Document>,
  random: Int,
  role: Role
) {
  val idFilters = linkedMapOf<String, Any>()
  filterRoleId(role.id).append2Map(idFilters)
  val last = role.toCopy()
  role.name = "梦奇$random"
  role.pos.add(20)
  role.pos.remove(40)
  role.bag.capacity = 9999
  role.heros[1001]?.name = "赵云"
  role.diff(other = last).let { updates ->
    updateAndFind(roleColl, idFilters, updates)
  }
}

fun daoOp(
  roleColl: MongoCollection<Document>,
  random: Int,
  role: Role
) {
  val idFilters = linkedMapOf<String, Any>()
  filterRoleId(role.id).append2Map(idFilters)
  var updates = linkedMapOf<String, Any>()
  // 更新一级对象的基本属性
  updateRoleName("恰米$random").append2Map(updates)
  updateAndFind(roleColl, idFilters, updates)

  // 更新一级对象的所有数组属性
  updates.clear()
  val newPos = role.pos.toMutableList()
  newPos.add(50)
  updateRolePos(newPos.toList()).append2Map(updates)
  updateAndFind(roleColl, idFilters, updates)

  // 更新二级对象的基本属性
  updates.clear()
  updateBagCapacity(9999).append2Map(updates)
  updateAndFind(roleColl, idFilters, updates)

  // 更新二级Map对象的基本属性
  updates.clear()
  updateHeroNameById(1001, "赵云").append2Map(updates)
  updateAndFind(roleColl, idFilters, updates)

  // 更新一级对象的单个数组属性
  val posFilters = linkedMapOf<String, Any>()
  filterRoleId(role.id).append2Map(posFilters)
  filterRolePos(10).append2Map(posFilters)
  updates.clear()
  updateRolePos(20).append2Map(updates)
  updateAndFind(roleColl, posFilters, updates, idFilters)

  // 更新二级单个数组对象的基本属性
  val itemFilters = linkedMapOf<String, Any>()
  filterRoleId(role.id).append2Map(itemFilters)
  filterBagItemId(6001).append2Map(itemFilters)
  updates.clear()
  updateBagItemNameById("项链").append2Map(updates)
  updateAndFind(roleColl, itemFilters, updates, idFilters)
}

fun filterRoleId(roleId: Int): Pair<String, Any> {
  return "id" to roleId
}

fun updateRoleName(name: String): Pair<String, Any> {
  return "name" to name
}

fun updateRolePos(newPos: List<Int>): Pair<String, Any> {
  return "pos" to newPos
}

fun updateBagCapacity(capacity: Int): Pair<String, Any> {
  return "bag.capacity" to capacity
}

fun updateHeroNameById(heroId: Int, heroName: String): Pair<String, Any> {
  return "heros.$heroId.name" to heroName
}

fun filterBagItemId(itemId: Int): Pair<String, Any> {
  return "bag.items.id" to itemId
}

fun updateBagItemNameById(itemName: String): Pair<String, Any> {
  return "bag.items.$.name" to itemName
}

fun filterRolePos(pos: Int): Pair<String, Any> {
  return "pos" to pos
}

fun updateRolePos(newPos: Int): Pair<String, Any> {
  return "pos.$" to newPos
}


fun Pair<String, Any>.append2Map(map: MutableMap<String, Any>) {
  map[this.first] = this.second
}

fun updateAndFind(
  coll: MongoCollection<Document>,
  filters: Map<String, Any>,
  updates: Map<String, Any>,
  findFilters: Map<String, Any> = filters
) {
  println("\nupdate filters: ${filters.toDocument().toJson()}, updates:${updates.toDocument().toJson()}")
  updateOne(coll, filters, updates).let { println("update result:" + if (it) "success" else "failed") }
  println("find filters: ${findFilters.toDocument().toJson()}")
  findOne(coll, findFilters)?.let { println("find result:" + it.toJson()) }
}