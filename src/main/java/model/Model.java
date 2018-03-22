package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Model {

    private static final Object instance = new Object();
    private Set triple = new HashSet();

    protected Model() {

    }

    // Runtime initialization
    // By defualt ThreadSafe
    public static Object getInstance() {
        return instance;
    }

    public void fill(){
        triple.add("");
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
