package ru.tinkoff.kora.techempower.loom.undertow.pool;

import java.util.function.Supplier;

public class CarrierThreadLocalFix {
    public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new jdk.internal.misc.CarrierThreadLocal<S>() {
            @Override
            protected S initialValue() {
                return supplier.get();
            }
        };
    }

}
