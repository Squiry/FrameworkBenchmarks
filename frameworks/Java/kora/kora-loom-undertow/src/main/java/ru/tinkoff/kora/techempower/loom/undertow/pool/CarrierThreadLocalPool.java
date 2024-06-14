package ru.tinkoff.kora.techempower.loom.undertow.pool;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;

public final class CarrierThreadLocalPool implements ByteBufferPool {
    private final boolean direct;
    private final ByteBufferPool arrayBackedPool;
    private final int size = 1024 * 16 - 20;
    private final ThreadLocal<ArrayDeque<ByteBuffer>> tl = new jdk.internal.misc.CarrierThreadLocal() {
        @Override
        protected Object initialValue() {
            return new ArrayDeque<>(16);
        }
    };

    public CarrierThreadLocalPool(boolean direct) {
        this.direct = direct;
        this.arrayBackedPool = direct ? new CarrierThreadLocalPool(false) : this;
    }

    @Override
    public PooledByteBuffer allocate() {
        var deque = tl.get();
        var buf = deque.pollLast();
        if (buf != null) {
            buf = direct
                ? ByteBuffer.allocateDirect(size)
                : ByteBuffer.allocate(size);
        }
        return new Pooled(this, buf);
    }

    @Override
    public ByteBufferPool getArrayBackedPool() {
        return arrayBackedPool;
    }

    private void release(Pooled pooled) {
        var dequeue = tl.get();
        if (dequeue.size() < 4) {
            dequeue.offer(pooled.buf);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public int getBufferSize() {
        return size;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    private static final class Pooled implements PooledByteBuffer {
        private final ByteBuffer buf;
        private final CarrierThreadLocalPool pool;
        private volatile boolean open = true;

        public Pooled(CarrierThreadLocalPool pool, ByteBuffer buf) {
            this.buf = buf;
            this.pool = pool;
        }

        @Override
        public ByteBuffer getBuffer() {
            return buf;
        }

        @Override
        public void close() {
            open = false;
            pool.release(this);
        }

        @Override
        public boolean isOpen() {
            return open;
        }
    }

}
