package ru.tinkoff.kora.techempower.blocking;

import ru.tinkoff.kora.jte.common.JteTemplate;
import ru.tinkoff.kora.techempower.common.Fortune;

import java.util.List;

@JteTemplate("fortunes")
public record FortunesTemplate(List<Fortune> fortunes) {
}
