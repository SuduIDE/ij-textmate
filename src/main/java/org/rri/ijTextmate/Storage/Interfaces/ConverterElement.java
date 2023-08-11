package org.rri.ijTextmate.Storage.Interfaces;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public interface ConverterElement {
    boolean fromElement(final @NotNull Element root);
    Element toElement();
}
