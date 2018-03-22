package modelbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Modelbuilder{

    public static final String DATA_DIR = "./src/main/resources/data/";
    public static final String ONTOLOGIES_DIR = "./src/main/resources/ontologies/";

    public static final String WORLD_SCHEMA = "http://data.masterthesis.com/dataset/world/schema/";
    public static final String WORLD_DATA = "http://data.masterthesis.com/dataset/world";

    public static final String WORLD_SCHEMA_FILE = ONTOLOGIES_DIR + "world.ttl";
    public static final String WORLD_DATA_FILE = DATA_DIR + "world-0.1.ttl";

    public void build();
}
