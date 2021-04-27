package utils

import com.alibaba.fastjson.JSONObject
import java.io.IOException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper


object JacksonUtil {
  private val mapper = ObjectMapper()

  fun bean2Json(obj: Any): ByteArray {
    return try {
      mapper.writeValueAsBytes(obj)
    } catch (e: JsonProcessingException) {
      e.printStackTrace()
      throw RuntimeException("jackson bean2Json failed")
    }

  }

  fun <T> json2Bean(bytes: ByteArray, clazz: Class<T>): T {
    return try {
      mapper.readValue(bytes, clazz)
    } catch (e: IOException) {
      e.printStackTrace()
      throw RuntimeException("jackson json2Bean failed")
    }

  }
}

object FastJsonUtil {

  fun bean2Json(obj: Any): ByteArray {
    return JSONObject.toJSONBytes(obj)
  }

  fun <T> json2Bean(bytes: ByteArray, clazz: Class<T>): T {
    return JSONObject.parseObject(bytes, clazz)
  }

}