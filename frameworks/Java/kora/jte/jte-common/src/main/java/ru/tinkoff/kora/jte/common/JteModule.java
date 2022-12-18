package ru.tinkoff.kora.jte.common;

public interface JteModule {
    @Jte
    default <T> JteTemplateHttpServerResponseMapper<T> jteTemplateHttpServerResponseMapper(JteTemplateWriter<T> templateWriter) {
        return new JteTemplateHttpServerResponseMapper<>(templateWriter);
    }
}
