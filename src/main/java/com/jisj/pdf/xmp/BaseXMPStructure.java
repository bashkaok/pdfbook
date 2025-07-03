package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.*;
import com.adobe.internal.xmp.options.IteratorOptions;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.jisj.pdf.Utils;

import java.util.*;
import java.util.function.Function;

/**
 * Base class for XMP schema
 */
public class BaseXMPStructure {
    private final XMPMeta metadata;
    private final String nameSpace;
    private final String prefix;

    /**
     * Schema constructor
     *
     * @param metadata  metadata object
     * @param nameSpace name space URI
     * @param prefix    NS prefix
     */
    public BaseXMPStructure(XMPMeta metadata, String nameSpace, String prefix) {
        if (metadata == null)
            throw new IllegalArgumentException("Unexpected metadata value=null");
        this.metadata = metadata;
        this.nameSpace = nameSpace;
        this.prefix = prefix;
        registerNS();
    }

    public XMPMeta getMetadata() {
        return metadata;
    }

    public String getNS() {
        return nameSpace;
    }

    public String getPrefix() {
        return prefix;
    }

    private void registerNS() {
        try {
            XMPMetaFactory.getSchemaRegistry().registerNamespace(getNS(), getPrefix());
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the string property value with current name space
     *
     * @param name  property name
     * @param value property value
     */
    public void setProperty(String name, String value) {
        try {
            metadata.setProperty(getNS(), name, value);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives the string property value from current name space
     *
     * @param name property name
     * @return {@code String} value | empty {@code String}
     */
    public String getStringPropertyValue(String name) {
        XMPProperty property;
        try {
            property = metadata.getProperty(getNS(), name);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
        return property == null ? "" : property.getValue();
    }

    /**
     * Sets the GUID property value with current name space
     *
     * @param name  property name
     * @param value property value
     */
    public void setProperty(String name, UUID value) {
        setProperty(name, value.toString());
    }

    /**
     * Gives the GUID property value from current name space
     *
     * @param name property name
     * @return {@code UUID} value | {@code null}
     */
    public UUID getGUIDPropertyValue(String name) {
        String value = getStringPropertyValue(name);
        return value.isEmpty() ? null : UUID.fromString(value);
    }

    /**
     * Gives the property with specified name
     *
     * @param name property name
     * @return {@code LocalDate} value if exist
     */
    public Optional<XMPProperty> getProperty(String fieldNS, String name) {
        try {
            return Optional.ofNullable(metadata.getProperty(fieldNS, name));
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets structure field to XMP
     *
     * @param parentNS   parent node name space
     * @param structName structure [name | path] in parent node
     * @param structNS   structure name space
     * @param fieldName  field name
     * @param value      field value
     */
    public void setStructField(String parentNS, String structName, String structNS,
                               String fieldName, String value) {
        try {
            getMetadata().setStructField(parentNS, structName, structNS, fieldName, value);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives the structure field from XMP
     *
     * @param parentNS   parent node name space
     * @param structName structure name in parent node
     * @param structNS   structure name space
     * @param fieldName  field name
     * @return field value | {@code null}
     */
    public Optional<XMPProperty> getStructField(String parentNS, String structName, String structNS,
                                                String fieldName) {
        try {
            return Optional.ofNullable(getMetadata().getStructField(parentNS, structName, structNS, fieldName));
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives a list of structure element of array
     *
     * @param nameSpace array name space
     * @param arrayName array name
     * @param element   element creator function: {@code <T> creator(String elementPath)}
     * @param <T>       structure object
     * @return list of elements of {@code <T>}
     */
    public <T> List<T> getArrayStruct(String nameSpace, String arrayName, Function<String, T> element) {
        List<T> result = new ArrayList<>();
        try {
            int lastItemIndex = getMetadata().countArrayItems(nameSpace, arrayName);
            for (int i = 1; i <= lastItemIndex; i++) {
                String structPath = XMPPathFactory.composeArrayItemPath(arrayName, i);
                result.add(element.apply(structPath));
            }
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
        return result;

    }

    /**
     * Gives the string array list
     *
     * @param nameSpace array name space
     * @param arrayName array name
     * @return list with elements of array
     */
    public List<String> getArray(String nameSpace, String arrayName) {
        List<String> result = new ArrayList<>();
        try {
            XMPIterator i = getMetadata().iterator(nameSpace, arrayName, new IteratorOptions().setJustChildren(true));
            while (i.hasNext()) {
                result.add(((XMPProperty) i.next()).getValue());
            }
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
        return result;

    }

    /**
     * Appends the structure element to end of array
     *
     * @param arrayNS   array namespace
     * @param arrayName array name
     * @return path to the struct element in array
     */
    public String appendArrayStructItem(String arrayNS, String arrayName) {
        try {
            getMetadata().appendArrayItem(arrayNS, arrayName, Utils.newOptions(PropertyOptions.ARRAY),
                    null, Utils.newOptions(PropertyOptions.STRUCT));
            // Assuming the structure was appended as the last item in the array
            int lastItemIndex = getMetadata().countArrayItems(arrayNS, arrayName);
            return XMPPathFactory.composeArrayItemPath(arrayName, lastItemIndex);

        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Appends the value to specified array
     *
     * @param arrayNS       array namespace
     * @param arrayPathName name or a general path expression of array
     * @param value         item value
     * @return the general path expression to item was appended
     */
    public String appendArrayItem(String arrayNS, String arrayPathName, String value) {
        try {
            getMetadata().appendArrayItem(arrayNS, arrayPathName, value);
            int lastItemIndex = getMetadata().countArrayItems(arrayNS, arrayPathName);
            return XMPPathFactory.composeArrayItemPath(arrayPathName, lastItemIndex);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "BaseXMPStructure{" +
                "nameSpace='" + nameSpace + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
