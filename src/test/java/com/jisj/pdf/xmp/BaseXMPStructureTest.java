package com.jisj.pdf.xmp;

import com.adobe.internal.xmp.XMPMetaFactory;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BaseXMPStructureTest {

    @Test
    void addCustomProperties() {
        BaseXMPStructure xmp = new BaseXMPStructure(XMPMetaFactory.create(), "http://ns.test.org/", "test1");
        Map<String, String> props = Map.of("P1", "Value1", "P2", "Value2","P3", "Value3");
        xmp.addCustomProperties(props);
        System.out.println(xmp.getMetadata().dumpObject());
        Map<String, String> propsNew = Map.of("P1", "Value1new", "P2", "Value2new","P4", "Value4");
        xmp.addCustomProperties(propsNew);
        System.out.println(xmp.getMetadata().dumpObject());
    }
}