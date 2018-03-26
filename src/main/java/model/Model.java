package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Model {

    private static Model instance;
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

    private void addPlus(String fileName){
        File file = new File(fileName);

        if (!file.canRead() || !file.isFile())
            System.exit(0);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                System.out.println("Read line " + zeile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
        }
    }
}
