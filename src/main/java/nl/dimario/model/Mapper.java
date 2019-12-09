package nl.dimario.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class Mapper {

    private static ObjectMapper objectMapper;

    public static ObjectMapper getMapper() {
        if (objectMapper == null) {

            YAMLFactory yamlFactory = new YAMLFactory();
            yamlFactory.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
            yamlFactory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
            objectMapper = new ObjectMapper(yamlFactory);
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        return objectMapper;
    }
}
