package ru.tinkoff.kora.techempower.loom.undertow.pool;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;

public final class VThreadNoopPool implements ByteBufferPool {
    private final boolean direct;
    private final ByteBufferPool arrayBackedPool;
    private final int size = 1024 * 16 - 20;
    private final ThreadLocal<ArrayDeque<ByteBuffer>> tl = ThreadLocal.withInitial(() -> new ArrayDeque<>(16));

    public VThreadNoopPool(boolean direct) {
        this.direct = direct;
        this.arrayBackedPool = direct ? new VThreadNoopPool(false) : this;
    }

    @Override
    public PooledByteBuffer allocate() {
        var t = Thread.currentThread();
        if (t.isVirtual()) {
            return new Unpooled(allocateBuf());
        }
        var deque = tl.get();
        var buf = deque.pollLast();
        if (buf == null) {
            buf = this.allocateBuf();
        }
        return new Pooled(this, buf.clear());
    }

    private ByteBuffer allocateBuf() {
        return direct
            ? ByteBuffer.allocateDirect(size)
            : ByteBuffer.allocate(size);
    }

    @Override
    public ByteBufferPool getArrayBackedPool() {
        return arrayBackedPool;
    }

    private void release(Pooled pooled) {
        if (Thread.currentThread().isVirtual()) {
            return;
        }
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
        private final VThreadNoopPool pool;
        private volatile boolean open = true;

        public Pooled(VThreadNoopPool pool, ByteBuffer buf) {
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

    private static final class Unpooled implements PooledByteBuffer {
        private final ByteBuffer buf;

        private Unpooled(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public ByteBuffer getBuffer() {
            return buf;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isOpen() {
            return true;
        }
    }
}
