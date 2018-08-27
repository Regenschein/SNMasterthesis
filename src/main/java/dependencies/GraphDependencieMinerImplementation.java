package dependencies;

import modelbuilder.ClassFinder;
import modelbuilder.RdfClass;
import java.util.Map;


public class GraphDependencieMinerImplementation implements GraphDependencieMiner{


    public GraphDependencieMinerImplementation() {

        ClassFinder classFinder = ClassFinder.getInstance();
        for (Map.Entry<String, RdfClass> entry : classFinder.getClasses().entrySet()) {
            for (String attribute1 : entry.getValue().getAttributes()) {
                for (String attribute2 : entry.getValue().getAttributes()){
                    ruleImplication();
                }
            }
        }
    }

    private void ruleImplication() {

    }

    private void ruleMath(){

    }

    private void ruleSizeRatio(){

    }

}
