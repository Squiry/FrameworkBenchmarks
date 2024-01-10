package ru.tinkoff.kora.techempower.common;

import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public record JsonResponse(String message) {
}
