package jmh;

import codec.JacksonCodec;
import entity.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(2)
@Threads(16)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)// 每个类为所有测试线程共享
public class CopyBenchmarkJava {

    @Param({"100","1000", "10000"})
    private int count;

    private Role lastEntity;
    private byte[] lastBytes;
    private ICodec codec;

    @Setup
    public void initParam() {
        this.codec = new JacksonCodec();
        this.lastEntity = randomRole();
        this.lastBytes = codec.doEncode(this.lastEntity);
    }

    @Benchmark
    public void diffRoleByCopy() {
        // 比较差异
        Role newRole = randomRole();
        this.lastEntity.diff(newRole, "");
        // 保存最新的数据对象
        this.lastEntity = newRole.toCopy();
    }

    @Benchmark
    public void diffRoleByCodec() {
        // 比较差异
        Role oldRole = codec.doDecode(lastBytes, Role.class);
        Role newRole = randomRole();
        oldRole.diff(newRole, "");
        // 保存最新的数据对象
        this.lastBytes = codec.doEncode(newRole);
    }

    // 模拟随机对象
    private Role randomRole() {
        Random random = new Random();
        int randomId = random.nextInt(count);
        ArrayList<Integer> posList = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            posList.add(i);
        }
        ArrayList<Item> itemList = new ArrayList<Item>();
        for (int i = 0; i < count; i++) {
            itemList.add(new Item(6000 + i, "道具" + i, 100 * i + 1));
        }
        HashMap<Integer, Hero> heros = new HashMap<Integer, Hero>();
        for (int i = 0; i < count; i++) {
            heros.put(1000 + i, new Hero(1000 + i, "英雄" + i, 100 * i + 1));
        }
        return new Role(10000 + randomId,
                "班纳$randomId",
                randomId % 2 == 0,
                posList,
                new Bag(2000 + randomId, 100 + randomId, itemList),
                heros
        );
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
//                .include(CopyBenchmarkJava.class.getSimpleName())
                .include(CopyBenchmarkKotlinKt.class.getSimpleName())
                .syncIterations(false)
                .result("E:\\Benchmark\\copy-benchmark-2.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}
