package modelbuilder;

import controller.InstanceBuilderController;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassFinder {

    private Map<String, RdfClass> classes = new HashMap<String, RdfClass>();
    private static ClassFinder classfinder;


    public ClassFinder(){
        this.classfinder = this;
    }

    public static synchronized ClassFinder getInstance() {
        if (ClassFinder.classfinder == null) {
            ClassFinder.classfinder = new ClassFinder();
        }
        return ClassFinder.classfinder;
    }

    public void addInstanceToClass(String classname, String instancename ){
        classes.get(classname).addInstance(instancename);
    }

    public void build(Model model) {
        StmtIterator i = model.listStatements();
        while (i.hasNext()) {
            Statement fact = i.next();
            if(fact.getPredicate().getURI().contains("rdf-syntax-ns#type")){
                if (!classes.containsKey(Util.transformObject(model,fact))){
                    classes.put(Util.transformObject(model,fact), new RdfClass(Util.transformObject(model,fact)));
                }
                RdfClass rdfTempClass = classes.get(Util.transformObject(model,fact));
                rdfTempClass.addInstance(Util.transformSubject(model,fact));
                classes.put(Util.transformObject(model,fact), rdfTempClass);
            }
        }
        i = model.listStatements();
        while (i.hasNext()) {
            Statement fact = i.next();
            for (RdfClass r : classes.values()){
                if (r.getInstances().contains(Util.transformSubject(model,fact))) {
                    r.addAttribute(Util.transformPredicate(model,fact, r.getName())); //                    r.addAttribute(Util.transformPredicate(model,fact));
                }
            }
        }
    }

    public void setAlmostKeys(Model model, HashSet<HashSet<String>> almostKeys){
        for (RdfClass r : classes.values()){
            for (Set<String> singleSet : almostKeys) {
                if(r.getAttributes().containsAll(singleSet)){
                    r.addAlmostKeys(singleSet);
                    if (singleSet.contains("ub:University%&%ub:name")){
                        System.out.println("checki-brecki");
                    }
                }
            }
        }
    }

    public void setConditionalKeys(Model model, HashSet<String> conditionalKeys){
        for (String singleKey : conditionalKeys){
            System.out.println(singleKey);
            String[] splittedCK = singleKey.split("\t");
            HashSet<String> keyAttributes = new HashSet<String>();
            for (String singleKeyAttribute : splittedCK[0].split(" ")){
                keyAttributes.add(singleKeyAttribute);
            }
            HashMap<String, String> conditions = new HashMap<String, String>();
            String[] singleConditions = splittedCK[1].split(" ");
            for (int i = 0; i < singleConditions.length; i++){
                if(!singleConditions[i].contains("=")){
                    singleConditions[i-1] = singleConditions[i-1] + " " + singleConditions[i];
                }
            }
            for (String singleCondition : singleConditions){
                if(singleCondition.contains("=")){
                    String[] splittedSingleCondition = singleCondition.split("=");
                    conditions.put(splittedSingleCondition[0], splittedSingleCondition[1]);
                }
            }
            ConditionalKey conditionalKey = new ConditionalKey(keyAttributes, conditions, Float.parseFloat(splittedCK[2]), Float.parseFloat(splittedCK[3]));
            System.out.println("bp");

            for (RdfClass r : classes.values()){
                if(r.getAttributes().containsAll(keyAttributes)){
                    r.addConditionalKey(conditionalKey);
                }
            }
        }
    }

    private HashSet<String>  transformSet(HashSet<HashSet<String>> amk){
        HashSet<String> retVal = new HashSet<String>();
        for (HashSet<String> hash : amk){
            StringBuilder sb = new StringBuilder();
            for (String s : hash){
                sb.append(s + ", ");
            }
            sb = sb.deleteCharAt(sb.length() -1);
            sb = sb.deleteCharAt(sb.length() -1);
            retVal.add(sb.toString());
        }
        return retVal;
    }


    public String retrieveClass(String instance){
        for (RdfClass r : classes.values()) {
            if (r.getInstances().contains(instance)) {
                return r.getName();
            }
        }
        return null;
    }

    public Map<String, RdfClass> getClasses() {
        return classes;
    }

}
