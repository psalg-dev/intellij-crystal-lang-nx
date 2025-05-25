package org.intellij.crystal;

import com.intellij.execution.ExternalizablePath;
import org.jdom.Element;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JdomUtils {
    public static void addNestedValue(Element element, String tag, Object value) {
        if (value == null) return;
        Element child = new Element(tag);
        child.setText(value.toString());
        element.addContent(child);
    }

    public static void addNestedPath(Element element, String tag, Path path) {
        if (path == null) return;
        addNestedValue(element, tag, ExternalizablePath.urlValue(path.toString()));
    }

    public static String getNestedString(Element element, String tag) {
        Element child = element.getChild(tag);
        return child != null ? child.getText() : null;
    }

    public static boolean getNestedBoolean(Element element, String tag) {
        String value = getNestedString(element, tag);
        return Boolean.parseBoolean(value);
    }

    public static Path getNestedPath(Element element, String tag) {
        String value = getNestedString(element, tag);
        return value != null ? Paths.get(ExternalizablePath.localPathValue(value)) : null;
    }
}