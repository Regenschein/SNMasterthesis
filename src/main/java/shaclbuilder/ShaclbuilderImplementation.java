package shaclbuilder;

import controller.Controller;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;

public class ShaclbuilderImplementation implements Shaclbuilder {

    public void build(){

        Model shapesmodel = JenaUtil.createDefaultModel();
        shapesmodel.read("./src/main/resources/eval/sparql-test-shape.ttl");
        //shapesmodel.read("./src/main/resources/data/hotelratings.shapes.ttl");

        Model datamodel = JenaUtil.createDefaultModel();
        datamodel.read("./src/main/resources/eval/sparql-test-data.ttl");

        Resource report = ValidationUtil.validateModel(datamodel, shapesmodel, false);
        //Resource report = ValidationUtil.validateModel(shapesmodel, shapesmodel, true);

        System.out.println(ModelPrinter.get().print(report.getModel()));

        Controller.getInstance().tA_main.setText(ModelPrinter.get().print(report.getModel()));
    }

    //ValidationExample

    private void buildNodeShape(String shapeName, String targetClass, String[] properties) {
        StringBuilder sb = new StringBuilder();
        sb.append(shapeName + "\n");
        sb.append("\t a sh:NodeShape ;\n");
        sb.append("\t sh:targetClass " + targetClass + " ;\n");
        sb.append("\t sh:property [\n");
        for (String s : properties) {
            sb.append("\t\t sh:path " + s + ";\n");
            sb.append("\t\t sh:maxCount 1 ;\n");
            sb.append("\t ] ;");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(".");
        System.out.println(sb.toString());

    }

    private void buildPropertyShape() {

    }

    private void buildKeyNode(){
        buildNodeShape("schema:PersonShape", "schema:Person", new String[]{"schema:\"given name\""});
    }

}
