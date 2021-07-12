package nl.sander;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Cleaner {
    private final String yamlFile;
    private final ObjectMapper objectMapper;

    public Cleaner(String filename) {
        this.yamlFile = filename;
        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: nl.sander.Cleaner <file>");
            System.exit(-1);
        }

        String filename = args[0];
        new Cleaner(filename).run();
    }

    private void run() {
        try {
            Object resource = objectMapper.readValue(new File(yamlFile), Object.class);
            Map<String, Object> cleaned = clean(YamlObject.of(resource));
            objectMapper.writeValue(new File(yamlFile + "_cleaned"), cleaned);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Map<String, Object> clean(YamlObject resource) {
        resource.get("items")
                .forEach(item -> {
                    YamlObject metadata = item.get("metadata");
                    metadata.remove("managedFields");
                    metadata.clear("annotations");
                    metadata.remove("creationTimestamp");
                    metadata.get("labels").removeIf("_key!=app");
                    metadata.remove("selfLink");
                    metadata.remove("namespace");
                    metadata.remove("resourceVersion");
                    metadata.remove("uid");
                    YamlObject spec = item.get("spec");
                    spec.clear("template/metadata/annotations");
                    spec.remove("template/spec/containers/0/image");
                    spec.remove("template/metadata/creationTimestamp");
                    spec.remove("triggers/1/imageChangeParams/lastTriggeredImage");
                    spec.removeIf("triggers/1/imageChangeParams/from/namespace != openshift");
                    item.remove("status");
                });

        return resource.asMap();
    }
}
