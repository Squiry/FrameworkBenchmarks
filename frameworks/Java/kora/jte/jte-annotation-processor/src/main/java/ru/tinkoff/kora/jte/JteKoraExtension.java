package ru.tinkoff.kora.jte;

import com.squareup.javapoet.ClassName;
import ru.tinkoff.kora.annotation.processor.common.AnnotationUtils;
import ru.tinkoff.kora.annotation.processor.common.CommonUtils;
import ru.tinkoff.kora.kora.app.annotation.processor.extension.ExtensionResult;
import ru.tinkoff.kora.kora.app.annotation.processor.extension.KoraExtension;

import javax.annotation.Nullable;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class JteKoraExtension implements KoraExtension {
    private final TypeMirror jteTemplateWriterErasure;
    private final Types types;
    private final Elements elements;

    public JteKoraExtension(Types types, Elements elements, TypeElement writerElement) {
        this.types = types;
        this.elements = elements;
        this.jteTemplateWriterErasure = types.erasure(writerElement.asType());
    }

    @Nullable
    @Override
    public KoraExtensionDependencyGenerator getDependencyGenerator(RoundEnvironment roundEnvironment, TypeMirror typeMirror) {
        if (this.jteTemplateWriterErasure == null) {
            return null;
        }
        if (!this.types.isAssignable(typeMirror, this.jteTemplateWriterErasure)) {
            return null;
        }
        var typeParameter = ((DeclaredType) typeMirror).getTypeArguments().get(0);
        var typeElement = (TypeElement) this.types.asElement(typeParameter);
        var jteTemplate = AnnotationUtils.findAnnotation(typeElement, ClassName.get("ru.tinkoff.kora.jte.common", "JteTemplate"));
        if (jteTemplate == null) {
            return null;
        }
        return () -> {
            var writerClassName = CommonUtils.getOuterClassesAsPrefix(typeElement) + typeElement.getSimpleName().toString() + "_JteWriter";
            var writerPackage = elements.getPackageOf(typeElement).getQualifiedName().toString();
            var jteWriterImpl = this.elements.getTypeElement(writerPackage + "." + writerClassName);
            if (jteWriterImpl == null) {
                return ExtensionResult.nextRound();
            }
            var constructor = CommonUtils.findConstructors(jteWriterImpl, m -> m.contains(Modifier.PUBLIC))
                .stream()
                .findFirst()
                .get();
            return ExtensionResult.fromExecutable(constructor);
        };
    }
}
