package entity

import org.bson.Document

interface IEntity<T> {

  fun toDoc(): Document

  fun diff(other: T, parent: String? = null): LinkedHashMap<String, Any>

  fun toCopy(): T

}

interface ICodec {

  fun <T: Any> doEncode(obj : T): ByteArray

  fun <T : Any> doDecode(bytes: ByteArray, clazz: Class<T>): T

}

