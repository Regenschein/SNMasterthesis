package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Model {

    private static Model instance;
    private HashMap<String, String> prefixes = new HashMap<String, String>();
    private Set<Triple> triples = new HashSet<Triple>();

    protected Model() {

    }

    // Runtime initialization
    // By defualt ThreadSafe
    public static Model getInstance() {
        if (Model.instance == null) {
            Model.instance = new Model ();
        }
        return Model.instance;
    }

    public void fill(Triple triple){
        triples.add(triple);
    }

    public Set<Triple> getTriples(){
        return triples;
    }

    public String getPrefix(String s){
        return prefixes.get(s);
    }

    public void addPrefix(String s, String u){
        prefixes.put(s, u);
    }
}
