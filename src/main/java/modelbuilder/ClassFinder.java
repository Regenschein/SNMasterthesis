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
        System.out.println("qwest complete");
    }

    public void setAlmostKeys(Model model, HashSet<HashSet<String>> almostKeys){
/*        HashSet<String> amks = transformSet(almostKeys);
        Set<Set<String>> prefixedSets = buildPrefixe(model, amks);
        for (RdfClass r : classes.values()){
            for (Set<String> singleSet : prefixedSets) {
                if(r.getAttributes().containsAll(singleSet)){
                    r.addAlmostKeys(singleSet);
                }
            }
        }*/

        HashSet<String> amks = transformSet(almostKeys);
        for (RdfClass r : classes.values()){
            for (Set<String> singleSet : almostKeys) {
                if(r.getAttributes().containsAll(singleSet)){
                    r.addAlmostKeys(singleSet);
                }
            }
        }
    }

    public void setConditionalKeys(Model model, HashSet<String> conditionalKeys){

        System.out.println("break");
    }

    private HashSet<String>  transformSet(HashSet<HashSet<String>> amk){
        HashSet<String> retVal = new HashSet<String>();
        for (HashSet<String> hash : amk){
            StringBuilder sb = new StringBuilder();
            for (String s : hash){
                sb.append(s + ", "); //OH, MY GOSH. OLD sb.append(s + ", ");
            }
            sb = sb.deleteCharAt(sb.length() -1);
            sb = sb.deleteCharAt(sb.length() -1);
            retVal.add(sb.toString());
        }
        return retVal;
    }

/*    public void setNonKeys(Set nonKeySet) {
        Set<Set<String>> prefixedSets = buildPrefixe(nonKeySet);
        for (RdfClass r : classes.values()){
            for (Set<String> singleSet : prefixedSets) {
                if(r.getAttributes().containsAll(singleSet)){
                    r.addNonKeys(singleSet);
                }
            }
        }
        System.out.println("TADA");
    }*/

    private Set<Set<String>> buildPrefixe(Model model, Set<String> nonKeySet){
        Set<Set<String>> retSet = new HashSet<>();
        for(String w : nonKeySet){
            Set<String> singleRetSet = new HashSet<String>();
            for (String s : w.split(", ")){
                if (s.equals("a")){
                    singleRetSet.add("a");
                } else {
                    String[] splitted = s.split(":");
                    try{
                        //singleRetSet.add(m.getPrefix(splitted[0]) + splitted[1] + ">");
                        //singleRetSet.add(model.getNsPrefixURI());
                    } catch(ArrayIndexOutOfBoundsException e){
                        singleRetSet.add(s + "");
                    }

                }
            }
            retSet.add(singleRetSet);
        }
        return retSet;
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
