package com.sixsixsix516.seckill.lock;

import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @author SUN
 * @date 2022/5/4
 */
@Data
public class RedisSegmentLock implements Lock {

    public static final int NO_SEG = -1;

    /**
     * 总段数
     */
    int segmentCount;

    int segmentIndex;

    /**
     * 被锁住的分段
     */
    int segmentIndexLocked = NO_SEG;

    public static final int DEFAULT_TIMEOUT = 2000;

    InnerLock[] innerLocks;

    private Thread thread;


    public RedisSegmentLock(String lockKey, String requestId, int segmentCount) {
        this.segmentCount = segmentCount;
        innerLocks = new InnerLock[segmentCount];
        for (int i = 0; i < segmentCount; i++) {
            // 每个分段，加一个编号
            String innerLockKey = lockKey + ":" + i;
            innerLocks[i] = new InnerLock(innerLockKey, requestId);
        }
        // 随机选择一个段
        segmentIndex = Math.abs(ThreadLocalRandom.current().nextInt(segmentCount));
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long millToWait = unit == null ? DEFAULT_TIMEOUT : unit.toMillis(time);
        long startMills = System.currentTimeMillis();

        InnerLock innerLock = innerLocks[segmentIndex];

        // 是否抢锁成功
        boolean locked = false;

        while (!locked) {
            // 抢锁未成功一致循环

            locked = innerLock.lock();
            if (locked) {
                // 抢锁成功
                segmentIndexLocked = segmentIndex;
                thread = Thread.currentThread();
            } else {
                // 抢锁失败
                millToWait = millToWait - (System.currentTimeMillis() - startMills);
                startMills = System.currentTimeMillis();

                if (millToWait > 0L) {
                    // 还没有超时

                    // 暂停一百毫秒
                    LockSupport.parkNanos(100 * 1000 * 1000);

                    // 换个段抢
                    segmentIndex++;
                    if (segmentIndex >= this.segmentCount) {
                        segmentIndex = 0;
                    }

                    innerLock = innerLocks[segmentIndex];
                } else {
                    System.out.println("抢锁超时");
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public void unlock() {
        if (segmentIndexLocked == NO_SEG) {
            // 没有被锁，直接返回
            return;
        }

        this.innerLocks[segmentIndexLocked].unlock();
        segmentIndexLocked = NO_SEG;
        thread = null;
    }



    @Override
    public boolean tryLock() {
        throw new IllegalStateException("未实现");
    }


    @Override
    public void lock() {
        throw new IllegalStateException("未实现");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new IllegalStateException("未实现");

    }


    @Override
    public Condition newCondition() {
        throw new IllegalStateException("未实现");
    }
}
