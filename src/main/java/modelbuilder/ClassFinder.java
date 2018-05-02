package modelbuilder;

import model.Model;
import model.Triple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassFinder {

    private Map<String, RdfClass> classes = new HashMap<String, RdfClass>();

    public ClassFinder() {
    }

    public void build() {
        Model model = Model.getInstance();
        try {
            for (Triple triple : model.getTriples()) {
                if (triple.getPredicate().equals("a")){
                    if(!classes.containsKey(triple.getObject()))
                        classes.put(triple.getObject(), new RdfClass(triple.getObject()));
                    RdfClass rdfTempClass = classes.get(triple.getObject());
                    rdfTempClass.addInstance(triple.getSubject());
                    classes.put(triple.getObject(), rdfTempClass);
                }
            }
            for (Triple triple : model.getTriples()) {
                for (RdfClass r : classes.values()){
                    if (r.getInstances().contains(triple.getSubject())) {
                        r.addAttribute(triple.getPredicate());
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlmostKeys(HashSet<HashSet<String>> almostKeys){
        HashSet<String> amks = transformSet(almostKeys);
        Set<Set<String>> prefixedSets = buildPrefixe(amks);
        for (RdfClass r : classes.values()){
            for (Set<String> singleSet : prefixedSets) {
                if(r.getAttributes().containsAll(singleSet)){
                    r.addAlmostKeys(singleSet);
                }
            }
        }
        System.out.println("TADA");
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

    public void setNonKeys(Set nonKeySet) {
        Set<Set<String>> prefixedSets = buildPrefixe(nonKeySet);
        for (RdfClass r : classes.values()){
            for (Set<String> singleSet : prefixedSets) {
                if(r.getAttributes().containsAll(singleSet)){
                    r.addNonKeys(singleSet);
                }
            }
        }
        System.out.println("TADA");
    }

    private Set<Set<String>> buildPrefixe(Set<String> nonKeySet){
        Set<Set<String>> retSet = new HashSet<>();
        Model m = Model.getInstance();
        for(String w : nonKeySet){
            Set<String> singleRetSet = new HashSet<String>();
            for (String s : w.split(", ")){
                if (s.equals("a")){
                    singleRetSet.add("a");
                } else {
                    String[] splitted = s.split(":");
                    try{
                        singleRetSet.add(m.getPrefix(splitted[0]) + splitted[1] + ">");
                    } catch(ArrayIndexOutOfBoundsException e){
                        singleRetSet.add(s);
                    }

                }
            }
            retSet.add(singleRetSet);
        }
        return retSet;
    }


    public Map<String, RdfClass> getClasses() {
        return classes;
    }
}
