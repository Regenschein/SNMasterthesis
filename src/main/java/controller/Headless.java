package controller;

import keyfinder.SAKeyAlmostKeysOneFileOneN;
import keyfinder.VICKEY;
import modelbuilder.ClassFinder;
import modelbuilder.Modelreader;
import modelbuilder.RdfClass;
import org.apache.jena.riot.Lang;
import shaclbuilder.Shaclbuilder;
import shaclbuilder.ShaclbuilderImplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Headless {

    static Shaclbuilder s = new ShaclbuilderImplementation();
    ClassFinder cf = new ClassFinder();
    Modelreader mr = new Modelreader();

    public static void main(String[] args){

        Headless headless = new Headless();
        headless.useArgs(args);

        headless.getInfos();

        if(headless.useArgs(args) == true){
            headless.complete();
        } else {
            headless.withoutKeyFinding();
        }
        evaluateShacl();
    }

    private boolean useArgs(String[] args){

        boolean withKeyFinding = true;

        for (int i = 0; i < args.length; i = i + 2){
            switch (args[i]) {
                case "-d":
                    Configuration.getInstance().setPath("./src/main/resources/data/" + args[i+1] + ".ttl");
                    break;
                case "-s":
                    Configuration.getInstance().setShaclpath("./src/main/resources/gen/" + args[i+1] + ".ttl");
                    withKeyFinding = false;
                case "-dl":
                    Configuration.getInstance().setPath(args[i+1] + ".ttl");
                    break;
                case "-sl":
                    Configuration.getInstance().setShaclpath(args[i+1] + ".ttl");
                    withKeyFinding = true;
                default:
                        break;
            }
        }
        return withKeyFinding;
    }

    /**
     * Method to run through the whole process
     */
    private void complete(){
        mr.readFile();
        cf.build(mr.getModel());
        mr.writeFile(Lang.TSV, cf);
        findKeys();
        buildShacl();
    }

    private void withoutKeyFinding(){
        mr.readFile();
        cf.build(mr.getModel());
        mr.writeFile(Lang.TSV, cf);
    }

    private void findKeys(){
        String[] args = new String[2];
        args[0] = Configuration.getInstance().getTsvpath();
        args[1] = "0";
        try {
            SAKeyAlmostKeysOneFileOneN.main(args);

            cf.setAlmostKeys(mr.getModel(), SAKeyAlmostKeysOneFileOneN.almostKeys);
            System.out.println("Almost keys found and saved");
        } catch (IOException e) {
            System.out.println("Failed to find and save Almost keys");
        }
        args = new String[1];
        args[0] = Configuration.getInstance().getTsvpath();
        //args[0] = "./src/main/resources/data/museum.tsv";
        try {
            VICKEY.main(args);
            System.out.println(VICKEY.nonKeySet);
            cf.setConditionalKeys(mr.getModel(), VICKEY.conditionalKeys);
            System.out.println("Conditional keys found and saved");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to find and save Conditional keys");
        }

    }

    private void showMeWhatYouGot(){
        for (Map.Entry<String, RdfClass> rdfClass :cf.getClasses().entrySet()){
            System.out.println(rdfClass.getValue().getName());
            for(Set<String> set : rdfClass.getValue().getAlmostKeys()){
                System.out.println("NEW AMK");
                for (String string : set){
                    System.out.println("This is amk: " + string);
                }
            }

            for(Set<String> set : rdfClass.getValue().getNonKeys()){
                System.out.println("NEW NK");
                for (String string : set){
                    System.out.println("This is nk: " + string);
                }
            }
        }
    }

    private void buildShacl() {
        s.buildNonKeys(mr.getModel(), cf.getClasses());
        s.buildAlmostKeys(mr.getModel(), cf.getClasses());
        s.buildConditionalKeys(mr.getModel(), cf.getClasses());
    }

    private static void evaluateShacl(){
        s.build();
    }

    private void getInfos(){

        File file = new File("D:/Wikidata/wikidata-20150615-all-BETA.ttl/wikidata-20150615-all-BETA.ttl");

        if (!file.canRead() || !file.isFile())
            System.exit(0);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("D:/Wikidata/wikidata-20150615-all-BETA.ttl/wikidata-20150615-all-BETA.ttl"));
            String zeile = null;
            int professionellerInkrementalzaehler = 0;
            while ((zeile = in.readLine()) != null && professionellerInkrementalzaehler < 1000) {
                System.out.println(zeile);
                professionellerInkrementalzaehler = professionellerInkrementalzaehler + 1;
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
