package ru.tinkoff.kora.jte;

import com.squareup.javapoet.*;
import ru.tinkoff.kora.annotation.processor.common.AbstractKoraProcessor;
import ru.tinkoff.kora.annotation.processor.common.AnnotationUtils;
import ru.tinkoff.kora.annotation.processor.common.CommonUtils;
import ru.tinkoff.kora.annotation.processor.common.NameUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class JteAnnotationProcessor extends AbstractKoraProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of("ru.tinkoff.kora.jte.common.JteTemplate");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var annotation : annotations) {
            for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                assert element.getKind() == ElementKind.RECORD;
                var typeElement = (TypeElement) element;
                var annotationMirror = AnnotationUtils.findAnnotation(typeElement, ClassName.get("ru.tinkoff.kora.jte.common", "JteTemplate"));
                var templateName = Objects.requireNonNull(AnnotationUtils.<String>parseAnnotationValueWithoutDefault(annotationMirror, "value"));
                if (templateName.endsWith(".jte")) {
                    templateName= templateName.substring(0, templateName.length() - 4);
                }
                var contentType = Optional.ofNullable(AnnotationUtils.<VariableElement>parseAnnotationValueWithoutDefault(annotationMirror, "contentType"))
                    .map(VariableElement::getSimpleName)
                    .map(Objects::toString)
                    .orElse("Html");
                var writerClassName = NameUtils.generatedType(typeElement, "JteWriter");
                var typeName = TypeName.get(typeElement.asType());
                var b = TypeSpec.classBuilder(writerClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get("ru.tinkoff.kora.jte.common", "JteTemplateWriter"), typeName));
                var m = MethodSpec.methodBuilder("write")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .addParameter(typeName, "value")
                    .addParameter(ClassName.get("gg.jte", "TemplateOutput"), "output");
                if (contentType.equals("Html")) {
                    m.addStatement("$T _output", ClassName.get("gg.jte.html", "HtmlTemplateOutput"));
                    m.beginControlFlow("if (output instanceof $T _o)", ClassName.get("gg.jte.html", "HtmlTemplateOutput"))
                        .addStatement("_output = _o")
                        .nextControlFlow("else")
                        .addStatement("_output = new $T(output)", ClassName.get("gg.jte.html", "OwaspHtmlTemplateOutput"))
                        .endControlFlow();
                } else {
                    m.addStatement("var _output = _output;");
                }
                var template = ClassName.get("gg.jte.generated.precompiled", "Jte" + templateName + "Generated");
                m.addCode("$T.render(_output, null", template);
                for (var recordComponent : ((TypeElement) element).getRecordComponents()) {
                    m.addCode(", value.$N()", recordComponent.getSimpleName());
                }
                m.addCode(");\n");
                b.addMethod(m.build());

                var packageName = this.elements.getPackageOf(element).getQualifiedName().toString();
                var javaFile = JavaFile.builder(packageName, b.build()).build();
                try {
                    javaFile.writeTo(this.processingEnv.getFiler());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return false;
    }
}
