package ru.tinkoff.kora.jte.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface JteTemplate {
    String value();

    gg.jte.ContentType contentType() default gg.jte.ContentType.Html;
}
