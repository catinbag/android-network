package org.demo.network.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Reads a saved API answer from src/test/resources. */
public final class TestResources {
    private TestResources() {
    }

    public static String read(String name) {
        try (InputStream in = TestResources.class.getResourceAsStream("/" + name)) {
            if (in == null) throw new IllegalStateException("No test resource: " + name);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
