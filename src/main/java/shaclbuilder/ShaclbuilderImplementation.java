package shaclbuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.Resource;
//import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

public class ShaclbuilderImplementation implements Shaclbuilder {

    public void build(){

        // Load the main data model
        //org.apache.jena.rdf.model.Model dataModel = JenaUtil.createMemoryModel();
        //dataModel.read(ShaclbuilderImplementation.class.getResourceAsStream("./src/main/resources/data/class-001.test.ttl"), "urn:dummy", FileUtils.langTurtle);
        //dataModel = dataModel.read(ShaclbuilderImplementation.class.getResourceAsStream("/data/class-001.test.ttl"), "urn:dummy", FileUtils.langTurtle);

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        //Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);

        // Print violations
        //System.out.println(ModelPrinter.get().print(report.getModel()));

    }

}
