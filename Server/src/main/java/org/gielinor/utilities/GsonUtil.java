package org.gielinor.utilities;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author <a href="https://Gielinor.org">Gielinor Logan G.</a>
 */
public class GsonUtil {

    private static final Gson INSTANCE;

    static {
        INSTANCE = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().setDateFormat(DateFormat.LONG)
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).setPrettyPrinting().setVersion(1.0).create();
    }

    public static Object load(String dir, Type type) {
        try (Reader reader = Files.newBufferedReader(Paths.get(dir))) {
            return INSTANCE.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(Object src, String dir, Type type) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(dir))) {
            writer.write(INSTANCE.toJson(src, type));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
