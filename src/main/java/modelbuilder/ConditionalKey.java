package modelbuilder;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ConditionalKey {

    private HashSet<String> keyAttributes;
    private HashMap<String, String> constraints;
    private float n1;
    private float n2;

    public ConditionalKey(HashSet<String> keyAttributes, HashMap<String, String> constraints, float n1, float n2) {
        this.keyAttributes = keyAttributes;
        this.constraints = new HashMap<String, String>();

        Iterator it = constraints.entrySet().iterator();
        HashMap<String, String> mapping = Mapping.getInstance().ret();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("here we go: " + pair.getKey().toString() + " . . . " + mapping.get(pair.getValue().toString()));

            this.constraints.put(pair.getKey().toString(),  mapping.get(pair.getValue().toString()));

/*            try {
                if(mapping.get(pair.getValue()).equals(null)){
                    System.out.println("THE FAILURE VALUE: " + pair.getValue());
                    System.exit(0);
                }
                int counter = 0;
                if(mapping.get(pair.getValue().toString()).contains("\"")){
                    for (char c : mapping.get(pair.getValue().toString()).toCharArray()){
                        if (c == '\"')
                            counter++;
                    }
                    if(counter % 2 == 0){

                    }
                }
            } catch (NullPointerException np){
                this.constraints.put(pair.getKey().toString(), pair.getValue().toString());
            }*/


            it.remove(); // avoids a ConcurrentModificationException
        }
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
