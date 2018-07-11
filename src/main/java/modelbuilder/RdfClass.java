package modelbuilder;

import java.util.*;

public class RdfClass {

    private String name;
    private Set<String> attributes;
    private Set<String> instances;
    private Set<Set<String>> nonKeys = new HashSet<>();
    private Set<Set<String>> almostKeys = new HashSet<>();
    private Set<ConditionalKey> condtionalKeys = new HashSet<>();

    private HashMap<String, List<Constraint>> constraints = new HashMap<>();

    public void addConstraints(String attribute, List<Constraint> constraintList){
        constraints.put(attribute, constraintList);
    }

    public void addConstraint(String attribute, String constraint, String value){
        //TODO: ADD CONSTRAINTS
    }

    public RdfClass(String name){
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


    public void addConditionalKey(ConditionalKey conditionalKey) {
        this.condtionalKeys.add(conditionalKey);
    }

    public void setCondtionalKeys(Set<ConditionalKey> condtionalKeys) {
        this.condtionalKeys = condtionalKeys;
    }

    public Set<ConditionalKey> getConditionalKeys() {
        return condtionalKeys;
    }
}
