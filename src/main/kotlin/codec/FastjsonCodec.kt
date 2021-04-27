package codec

import entity.ICodec
import utils.FastJsonUtil

class FastjsonCodec : ICodec {

  override fun <T: Any> doEncode(obj: T): ByteArray {
    return FastJsonUtil.bean2Json(obj)
  }

  override fun <T : Any> doDecode(bytes: ByteArray, clazz: Class<T>): T {
    return FastJsonUtil.json2Bean(bytes, clazz)
  }
}