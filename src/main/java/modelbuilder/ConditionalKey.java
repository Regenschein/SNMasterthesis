package modelbuilder;

import java.util.HashMap;
import java.util.HashSet;

public class ConditionalKey {

    private HashSet<String> keyAttributes;
    private HashMap<String, String> constraints;
    private float n1;
    private float n2;

    public ConditionalKey(HashSet<String> keyAttributes, HashMap<String, String> constraints, float n1, float n2) {
        this.keyAttributes = keyAttributes;
        this.constraints = constraints;
        this.n1 = n1;
        this.n2 = n2;
    }

    public HashSet<String> getKeyAttributes() {
        return keyAttributes;
    }
    public void setKeyAttributes(HashSet<String> keyAttributes) {
        this.keyAttributes = keyAttributes;
    }
    public float getN1() {
        return n1;
    }
    public void setN1(float n1) {
        this.n1 = n1;
    }
    public float getN2() {
        return n2;
    }
    public void setN2(float n2) {
        this.n2 = n2;
    }
    public HashMap<String, String> getConstraints() {
        return constraints;
    }
    public void setConstraints(HashMap<String, String> constraints) {
        this.constraints = constraints;
    }
}
