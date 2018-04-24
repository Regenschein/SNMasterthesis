package modelbuilder;

import model.Model;
import model.Triple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassFinder {

    private Map<String, rdfClass> classes = new HashMap<String, rdfClass>();

    public ClassFinder() {
    }


    public void build() {
        Model model = Model.getInstance();
        try {
            for (Triple triple : model.getTriples()) {
                if (triple.getPredicate().equals("a")){
                    if(!classes.containsKey(triple.getObject()))
                        classes.put(triple.getObject(), new rdfClass(triple.getObject()));
                    rdfClass rdfTempClass = classes.get(triple.getObject());
                    rdfTempClass.addInstance(triple.getSubject());
                    classes.put(triple.getObject(), rdfTempClass);
                }
            }
            for (Triple triple : model.getTriples()) {
                for (rdfClass r : classes.values()){
                    if (r.instances.contains(triple.getSubject())) {
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
        for (rdfClass r : classes.values()){
            for (Set<String> singleSet : prefixedSets) {
                if(r.attributes.containsAll(singleSet)){
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
        for (rdfClass r : classes.values()){
            for (Set<String> singleSet : prefixedSets) {
                if(r.attributes.containsAll(singleSet)){
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

    public class rdfClass{
        private String name;
        private Set<String> attributes;
        private Set<String> instances;
        private Set<Set<String>> nonKeys = new HashSet<>();
        private Set<Set<String>> almostKeys = new HashSet<>();

        public rdfClass(String name){
            this.name = name;
            attributes = new HashSet<String>();
            instances = new HashSet<String>();
        }

        public void addAttribute(String attribute){
            this.attributes.add(attribute);
        }

        public void addInstance(String instance){
            this.instances.add(instance);
        }

        public void addNonKeys(Set nonKeys){
            this.nonKeys.add(nonKeys);
        }

        public void addAlmostKeys(Set almostKeys){
            this.almostKeys.add(almostKeys);
        }

        public String getName() {
            return name;
        }

        public Set<String> getAttributes() {
            return attributes;
        }

        public Set<String> getInstances() {
            return instances;
        }

        public Set<Set<String>> getNonKeys() {
            return nonKeys;
        }

        public Set<Set<String>> getAlmostKeys() {
            return almostKeys;
        }


    }

    public Map<String, rdfClass> getClasses() {
        return classes;
    }
}
