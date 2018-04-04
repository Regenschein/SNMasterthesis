package shaclbuilder;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.FileUtils;
//import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
//import org.topbraid.spin.util.JenaUtil;

import java.io.InputStream;

public class ValidationExample {

    /**
     * Loads an example SHACL file and validates all focus nodes against all shapes.
     */
    public static void main(String[] args) throws Exception {

        // Load the main data model
        Model dataModel = ModelFactory.createDefaultModel();
        FileManager.get().readModel( dataModel, "./src/main/resources/data/class-001.test.ttl" );
        //InputStream is = ValidationExample.class.getResourceAsStream("src/main/resources/data/class-001.test.ttl");
        //dataModel.read(ValidationExample.class.getResourceAsStream("src/main/resources/data/class-001.test.ttl"), "urn:dummy", FileUtils.langTurtle);

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);

        // Print violations
        System.out.println(ModelPrinter.get().print(report.getModel()));
    }
}