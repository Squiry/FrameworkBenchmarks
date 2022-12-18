package ru.tinkoff.kora.jte.common;

import gg.jte.TemplateOutput;

public interface JteTemplateWriter<T> {
    void write(T value, TemplateOutput templateOutput);
}
