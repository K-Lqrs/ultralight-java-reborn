package net.janrupf.ujr.nap.gen;

import net.janrupf.ujr.nap.util.NativeTypeMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper to generate C++ classes for native access.
 */
public class CPPNativeAccessClassBuilder {
    private final ProcessingEnvironment environment;
    private final NativeTypeMapper typeMapper;
    private final String namespace;
    private final String className;
    private final StringBuilder content;

    /**
     * Creates a new C++ native access class builder.
     *
     * @param environment the environment to use
     * @param typeMapper  the type mapper to use
     * @param namespace   the namespace the class should be in
     * @param className   the name of the class
     */
    public CPPNativeAccessClassBuilder(
            ProcessingEnvironment environment,
            NativeTypeMapper typeMapper,
            String namespace,
            String className
    ) {
        this.environment = environment;
        this.typeMapper = typeMapper;
        this.namespace = namespace;
        this.className = className;
        this.content = new StringBuilder();

        this.fileHeader();
    }

    private void fileHeader() {
        // Add the disclaimer
        content.append("// This file is auto-generated by the ujr native access processor\n");
        content.append("// Do not edit this file manually\n");
        content.append("\n");

        // Add the include guard
        content.append("#pragma once\n");
        content.append("\n");

        // Add required includes
        content.append("#include <jni.h>\n");
        content.append("#include \"ujr/util/JniClass.hpp\"\n");
        content.append("#include \"ujr/util/JniField.hpp\"\n");
        content.append("#include \"ujr/util/JniMethod.hpp\"\n");
        content.append("\n");

        // Add the namespace (if any)
        if (namespace != null && !namespace.isEmpty()) {
            content.append("namespace ").append(namespace).append(" {\n");
        }

        // Add the class
        content.append("class ").append(className).append(" {\n");

        // Everything is public
        content.append("public:\n");

        // Delete the constructor, these class can't be instantiated
        content.append("    ").append(className).append("() = delete;\n");
        content.append("\n");
    }

    /**
     * Adds the class accessor to the generated class.
     *
     * @param clazz the class to add
     */
    public void addClass(TypeElement clazz) {
        // Add the class
        content.append("    static inline ")
                .append(typeMapper.toJniClassType(clazz.asType()))
                .append(" CLAZZ")
                .append("{};\n");
    }

    /**
     * Adds a field accessor to the generated class.
     *
     * @param field           the field to add
     * @param declarationName the name of the variable to declare
     */
    public void addField(VariableElement field, String declarationName) {
        TypeMirror fieldType = field.asType();
        TypeElement fieldClazz = (TypeElement) environment.getTypeUtils().asElement(fieldType);

        String jniType;
        if (fieldClazz != null || fieldType.getKind() == TypeKind.ARRAY) {
            jniType = typeMapper.toJniType(fieldType);
        } else {
            // Primitive types don't have an associated element
            jniType = typeMapper.toPrimitiveJniType(fieldType);
        }

        String cppClass;
        if (field.getModifiers().contains(Modifier.STATIC)) {
            cppClass = "JniStaticField";
        } else {
            cppClass = "JniInstanceField";
        }

        // Add the field
        content.append("    static inline ::ujr::")
                .append(cppClass)
                .append("<")
                .append("decltype(CLAZZ), \"")
                .append(field.getSimpleName())
                .append("\", ")
                .append(jniType)
                .append("> ")
                .append(declarationName)
                .append("{CLAZZ};\n");
    }

    /**
     * Adds a method accessor to the generated class.
     *
     * @param method          the method to add
     * @param declarationName the name of the variable to declare
     */
    public void addMethod(ExecutableElement method, String declarationName) {
        TypeMirror returnType = method.getReturnType();

        String jniReturnType = typeMapper.toJniType(returnType);

        List<String> jniParameterTypes = new ArrayList<>();
        for (VariableElement parameter : method.getParameters()) {
            TypeMirror parameterType = parameter.asType();

            String jniType = typeMapper.toJniType(parameterType);

            jniParameterTypes.add(jniType);
        }


        boolean isConstructor = method.getSimpleName().contentEquals("<init>");

        String cppClass;
        if (isConstructor) {
            cppClass = "JniConstructor";
        } else if (method.getModifiers().contains(Modifier.STATIC)) {
            cppClass = "JniStaticMethod";
        } else {
            cppClass = "JniInstanceMethod";
        }

        // Add the method
        content.append("    static inline ::ujr::")
                .append(cppClass)
                .append("<")
                .append("decltype(CLAZZ), ");

        if (!isConstructor) {
            content.append("\"").append(method.getSimpleName()).append("\", ").append(jniReturnType);

            if (!jniParameterTypes.isEmpty()) {
                content.append(", ");
            }
        }

        if (!jniParameterTypes.isEmpty()) {
            content.append(String.join(", ", jniParameterTypes));
        }

        content.append("> ")
                .append(declarationName)
                .append("{CLAZZ};\n");
    }

    /**
     * Assembles the generated class.
     *
     * @return the generated class
     */
    public String build() {
        // Close the class
        content.append("};\n");
        content.append("\n");

        // Close the namespace (if any)
        if (namespace != null && !namespace.isEmpty()) {
            content.append("}\n");
        }

        return content.toString();
    }
}
