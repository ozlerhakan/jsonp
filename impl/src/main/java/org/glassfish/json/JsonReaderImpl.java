package org.glassfish.json;

import javax.json.*;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

class JsonReaderImpl implements JsonReader {
    private final JsonParser parser;
    private boolean readDone;
    private final Map<String, ?> config;

    /**
     * Creates a JSON reader from a character stream
     *
     * @param reader a reader from which JSON is to be read
     */
    public JsonReaderImpl(Reader reader) {
        this(reader, Collections.<String, Object>emptyMap());
    }

    /**
     * Creates a JSON reader from a character stream. The created
     * JSON reader is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param reader a character stream from which JSON is to be read
     * @param config a map of provider specific properties to configure the
     *               JSON reader; may be empty or null
     */
    public JsonReaderImpl(Reader reader, Map<String, ?> config) {
        JsonParserFactory factory = Json.createParserFactory(config);
        parser = factory.createParser(reader);
        this.config = factory.getConfigInUse();
    }

    /**
     * Creates a JSON reader from a byte stream. The character encoding of
     * the stream is determined as per the
     * <a href="http://tools.ietf.org/rfc/rfc4627.txt">RFC</a>.
     *
     * @param in a byte stream from which JSON is to be read
     */
    public JsonReaderImpl(InputStream in) {
        this(in, Collections.<String, Object>emptyMap());
    }

    /**
     * Creates a JSON reader from a byte stream. The character encoding of
     * the stream is determined as per the
     * <a href="http://tools.ietf.org/rfc/rfc4627.txt">RFC</a>. The created
     * JSON reader is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param in a byte stream from which JSON is to be read
     * @param config a map of provider specific properties to configure the
     *               JSON reader; may be empty or null
     */
    public JsonReaderImpl(InputStream in, Map<String, ?> config) {
        JsonParserFactory factory = Json.createParserFactory(config);
        parser = factory.createParser(in);
        this.config = factory.getConfigInUse();
    }

    /**
     * Creates a JSON reader from a byte stream. The bytes of the stream
     * are decoded to characters using the specified charset.
     *
     * @param in a byte stream from which JSON is to be read
     * @param charset a charset
     */
    public JsonReaderImpl(InputStream in, Charset charset) {
        this(in, charset, Collections.<String, Object>emptyMap());
    }

    /**
     * Creates a JSON reader from a byte stream. The bytes of the stream
     * are decoded to characters using the specified charset. The created
     * JSON reader is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param in a byte stream from which JSON is to be read
     * @param charset a charset
     * @param config a map of provider specific properties to configure the
     *               JSON reader; may be empty or null
     */
    public JsonReaderImpl(InputStream in, Charset charset, Map<String, ?> config) {
        JsonParserFactory factory = Json.createParserFactory(config);
        parser = factory.createParser(in, charset);
        this.config = factory.getConfigInUse();
    }

    /**
     * Returns a JSON array or object that is represented in
     * the input source. This method needs to be called
     * only once for a reader instance.
     *
     * @return a Json object or array
     * @throws JsonException if a JSON object or array cannot
     *     be created due to i/o error (IOException would be
     * cause of JsonException)
     * @throws javax.json.stream.JsonParsingException if a JSON object or array
     *     cannot be created due to incorrect representation
     * @throws IllegalStateException if this method, readObject, readArray or
     *     close method is already called
     */
    public JsonStructure read() {
        if (readDone) {
            throw new IllegalStateException("read/readObject/readArray/close method is already called.");
        }
        readDone = true;
        if (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            if (e == JsonParser.Event.START_ARRAY) {
                return readArray(new JsonArrayBuilderImpl());
            } else if (e == JsonParser.Event.START_OBJECT) {
                return readObject(new JsonObjectBuilderImpl());
            } else {
                throw new JsonException("Cannot read JSON, parsing error. Parsing Event="+e);
            }
        }
        throw new JsonException("Cannot read JSON, possibly empty stream");
    }

    /**
     * Returns a JSON object that is represented in
     * the input source. This method needs to be called
     * only once for a reader instance.
     *
     * @return a Json object
     * @throws JsonException if a JSON object cannot
     *     be created due to i/o error (IOException would be
     *     cause of JsonException)
     * @throws javax.json.stream.JsonParsingException if a JSON object cannot
     *     be created due to incorrect representation
     * @throws IllegalStateException if this method, readObject, readArray or
     *     close method is already called
     */
    public JsonObject readObject() {
        if (readDone) {
            throw new IllegalStateException("read/readObject/readArray/close method is already called.");
        }
        readDone = true;
        if (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            if (e == JsonParser.Event.START_OBJECT) {
                return readObject(new JsonObjectBuilderImpl());
            } else if (e == JsonParser.Event.START_ARRAY) {
                throw new JsonException("Cannot read JSON object, found JSON array");
            } else {
                throw new JsonException("Cannot read JSON object, parsing error. Parsing Event="+e);
            }
        }
        throw new JsonException("Cannot read JSON object, possibly empty stream");
    }

    /**
     * Returns a JSON array that is represented in
     * the input source. This method needs to be called
     * only once for a reader instance.
     *
     * @return a Json array
     * @throws JsonException if a JSON array cannot
     *     be created due to i/o error (IOException would be
     *     cause of JsonException)
     * @throws javax.json.stream.JsonParsingException if a JSON array cannot
     *     be created due to incorrect representation
     * @throws IllegalStateException if this method, readObject, readArray or
     *     close method is already called
     */
    public JsonArray readArray() {
        if (readDone) {
            throw new IllegalStateException("read/readObject/readArray/close method is already called.");
        }
        readDone = true;
        if (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            if (e == JsonParser.Event.START_ARRAY) {
                return readArray(new JsonArrayBuilderImpl());
            } else if (e == JsonParser.Event.START_OBJECT) {
                throw new JsonException("Cannot read JSON array, found JSON object");
            } else {
                throw new JsonException("Cannot read JSON array, parsing error. Parsing Event="+e);
            }
        }
        throw new JsonException("Cannot read JSON array, possibly empty stream");
    }

    /**
     * Closes this reader and frees any resources associated with the
     * reader. This closes the underlying input source.
     *
     * @throws JsonException if an i/o error occurs (IOException would be
     * cause of JsonException)
     */
    @Override
    public void close() {
        readDone = true;
        parser.close();
    }

    private JsonArray readArray(JsonArrayBuilder builder) {
        while(parser.hasNext()) {
            JsonParser.Event e = parser.next();
            switch (e) {
                case START_ARRAY:
                    JsonArray array = readArray(new JsonArrayBuilderImpl());
                    builder.add(array);
                    break;
                case START_OBJECT:
                    JsonObject object = readObject(new JsonObjectBuilderImpl());
                    builder.add(object);
                    break;
                case VALUE_STRING:
                    String  string = parser.getString();
                    builder.add(string);
                    break;
                case VALUE_NUMBER:
                    BigDecimal bd = new BigDecimal(parser.getString());
                    builder.add(bd);
                    break;
                case VALUE_TRUE:
                    builder.add(true);
                    break;
                case VALUE_FALSE:
                    builder.add(false);
                    break;
                case VALUE_NULL:
                    builder.addNull();
                    break;
                case END_ARRAY:
                    return builder.build();
                default:
                    throw new JsonException("Internal Error");
            }
        }
        throw new JsonException("Internal Error");
    }

    private JsonObject readObject(JsonObjectBuilder builder) {
        String key = null;
        while(parser.hasNext()) {
            JsonParser.Event e = parser .next();
            switch (e) {
                case START_ARRAY:
                    JsonArray array = readArray(new JsonArrayBuilderImpl());
                    builder.add(key, array);
                    break;
                case START_OBJECT:
                    JsonObject object = readObject(new JsonObjectBuilderImpl());
                    builder.add(key, object);
                    break;
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    String  string = parser.getString();
                    builder.add(key, string);
                    break;
                case VALUE_NUMBER:
                    BigDecimal bd = new BigDecimal(parser.getString());
                    builder.add(key, bd);
                    break;
                case VALUE_TRUE:
                    builder.add(key, true);
                    break;
                case VALUE_FALSE:
                    builder.add(key, false);
                    break;
                case VALUE_NULL:
                    builder.addNull(key);
                    break;
                case END_OBJECT:
                    return builder.build();
                default:
                    throw new JsonException("Internal Error");
            }
        }
        throw new JsonException("Internal Error");
    }

    /**
     * Returns read-only map of supported provider specific configuration
     * properties that are used to configure this JSON reader. If there are
     * any specified configuration properties that are not supported by
     * the provider, they won't be part of the returned map.
     *
     * @return a map of supported provider specific properties that are used
     * to configure this JSON reader; may be empty but not null.
     */
    public Map<String, ?> getConfigInUse() {
        return config;
    }
}
