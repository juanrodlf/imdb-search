package co.empathy.academy.search.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.file.Files;

public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static String loadAsString(final String path) {
        try {
            final File resource = new ClassPathResource(path).getFile();

            return new String(Files.readAllBytes(resource.toPath()));
        } catch(final Exception ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

}
