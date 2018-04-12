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
            System.out.println("choo");
        }catch (Exception e) {
            e.printStackTrace();
        }
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
                    singleRetSet.add(m.getPrefix(splitted[0]) + splitted[1] + ">");
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
        private Set<String> nonKeys;

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
            this.nonKeys = nonKeys;
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

        public Set<String> getNonKeys() {
            return nonKeys;
        }
    }

    public Map<String, rdfClass> getClasses() {
        return classes;
    }
}
