package ru.tinkoff.kora.techempower.async;

import ru.tinkoff.kora.jte.common.JteTemplate;
import ru.tinkoff.kora.techempower.common.Fortune;

import java.util.List;

@JteTemplate("fortunes")
public record FortunesTemplate(List<Fortune> fortunes) {
}
