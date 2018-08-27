package querybuilder;

import java.util.HashMap;
import java.util.Set;

public interface Querybuilder {

    void build(HashMap<String, String> prefixes, Set<String> amk, String name, int almostKey);

    void buildSpecificClasses(String[] classes);

    void buildClasses();
}
