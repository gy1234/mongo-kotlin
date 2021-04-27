package codec

import entity.ICodec
import utils.JacksonUtil

class JacksonCodec : ICodec {

  override fun <T: Any> doEncode(obj: T): ByteArray {
    return JacksonUtil.bean2Json(obj)
  }

  override fun <T : Any> doDecode(bytes: ByteArray, clazz: Class<T>): T {
    return JacksonUtil.json2Bean(bytes, clazz)
  }
}