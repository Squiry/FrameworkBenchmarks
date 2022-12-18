package ru.tinkoff.kora.jte;

import ru.tinkoff.kora.kora.app.annotation.processor.extension.ExtensionFactory;
import ru.tinkoff.kora.kora.app.annotation.processor.extension.KoraExtension;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Optional;

public class JteKoraExtensionFactory implements ExtensionFactory {
    @Override
    public Optional<KoraExtension> create(ProcessingEnvironment processingEnvironment) {
        var writerElement = processingEnvironment.getElementUtils().getTypeElement("ru.tinkoff.kora.jte.common.JteTemplateWriter");
        if (writerElement == null) {
            return Optional.empty();
        }
        return Optional.of(new JteKoraExtension(processingEnvironment.getTypeUtils(), processingEnvironment.getElementUtils(), writerElement));
    }
}
