package org.glassfish.json;

import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

class JsonWriterFactoryImpl implements JsonWriterFactory {
    private final Map<String, ?> config;

    JsonWriterFactoryImpl(Map<String, ?> config) {
        this.config = config;
    }

    public JsonWriterFactoryImpl() {
        this.config = Collections.emptyMap();
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return new JsonWriterImpl(writer);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return new JsonWriterImpl(out);
    }

    @Override
    public JsonWriter createWriter(OutputStream out, Charset charset) {
        return new JsonWriterImpl(out, charset);
    }

    @Override
    public Map<String, ?> getConfigInUse() {
        return config;
    }
}
