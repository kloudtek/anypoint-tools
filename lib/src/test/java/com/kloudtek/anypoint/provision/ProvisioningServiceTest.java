package com.kloudtek.anypoint.provision;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvisioningServiceTest {
    @Test
    public void testExpressionLanguageSimple() {
        ProvisioningServiceImpl s = new ProvisioningServiceImpl();
        HashMap<String, String> p = new HashMap<>();
        p.put("world", "Glarg");
        p.put("foo", "bar");
        assertEquals("helloGlarg", s.parseEL("hello${world}", p));
        assertEquals("helloGlargXbar", s.parseEL("hello${world}X${foo}", p));
        assertEquals("hello${world}", s.parseEL("hello$${world}", p));
        assertEquals("hello$world}", s.parseEL("hello$world}", p));
    }
}