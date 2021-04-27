package jmh


import codec.JacksonCodec
import entity.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.nextInt


@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
@Threads(8)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)// 每个类为所有测试线程共享
open class CopyBenchmarkKotlin {

  lateinit var lastEntity: Role
  lateinit var lastBytes: ByteArray
  lateinit var codec: ICodec

  @Setup
  fun initParm() {
    this.codec = JacksonCodec()
    this.lastEntity = randomRole()
    this.lastBytes = codec.doEncode(this.lastEntity)
  }

  @Benchmark
  fun diffRoleByCopy() {
    // 比较差异
    val newRole = randomRole()
    this.lastEntity.diff(newRole)
    // 保存最新的数据对象
    this.lastEntity = newRole.toCopy()
  }

  @Benchmark
  fun diffRoleByCodec() {
    // 比较差异
    val oldRole = codec.doDecode(lastBytes, Role::class.java)
    val newRole = randomRole()
    oldRole.diff(newRole)
    // 保存最新的数据对象
    this.lastBytes = codec.doEncode(newRole)
  }

  // 模拟随机对象
  private fun randomRole(): Role {
    val randomObject = Random(System.currentTimeMillis())
    val randomId = randomObject.nextInt(1..9999)
    val posList = (1..randomId).filter { it % 2 == 0 }.toMutableList()
    val itemList = (1..randomObject.nextInt(1..999))
      .map { index -> Item(6000 + index, "道具$index", 100 * index + 1) }
      .toMutableList()
    val heroMaps = (1..randomObject.nextInt(1..99))
      .associate { index -> index to Hero(1000 + index, "英雄$index", 10 * index + 1) }
      .toMutableMap()
    return Role(
      id = 10000 + randomId,
      name = "班纳$randomId",
      vip = randomId % 2 == 0,
      pos = posList,
      bag = Bag(2000 + randomId, 100 + randomId, itemList),
      heros = heroMaps
    )
  }


}

fun main() {
  val options = OptionsBuilder()
    .include(CopyBenchmarkKotlin::class.simpleName)
    .output("E:\\Benchmark\\mongo-benchmark.log")
    .build()
  Runner(options).run()
}