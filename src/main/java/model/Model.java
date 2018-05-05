package model;

import modelbuilder.ClassFinder;
import modelbuilder.RdfClass;

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
    private HashSet<RdfClass> classes = new HashSet<RdfClass>();


    public HashMap<String, String> getPrefixes() {
        return prefixes;
    }

    public void addRdfClass(RdfClass rdfClass){
        classes.add(rdfClass);
    }

    public HashSet<RdfClass> getClasses(){
        return classes;
    }

    protected Model() {

    }

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
        if(!u.endsWith("/"))
            u += "/";
        prefixes.put(s, u);
    }
}
