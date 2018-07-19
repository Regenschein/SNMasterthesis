package controller;

import dependencies.GraphDependencieMiner;
import dependencies.GraphDependencieMinerImplementation;
import keyfinder.SAKeyAlmostKeysOneFileOneN;
import keyfinder.VICKEY;
import modelbuilder.ClassFinder;
import modelbuilder.Modelreader;
import modelbuilder.RdfClass;
import modelbuilder.Splitter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import querybuilder.Querybuilder;
import querybuilder.QuerybuilderImplementation;
import shaclbuilder.Shaclbuilder;
import shaclbuilder.ShaclbuilderImplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Headless {

    static Shaclbuilder s = new ShaclbuilderImplementation();
    ClassFinder cf = new ClassFinder();
    Modelreader mr = new Modelreader();
    Querybuilder qb = new QuerybuilderImplementation();
    Splitter sp = new Splitter();

    long timestampOLD = System.currentTimeMillis();
    long timestampNEW = System.currentTimeMillis();

    public static void main(String[] args){

        Headless headless = new Headless();

        //Configuration.getInstance().setPath("./src/main/resources/data/" + "Universities-10mB" + ".ttl");
        Configuration.getInstance().setPath("D:/Wikidata/Splitterino/" + "SplittedWiki1" + ".ttl");

        char para = headless.useArgs(args);

        if(para == 'c'){
            headless.complete();
        } else if(para == 's'){
            headless.withoutKeyFinding();
        } else if (para == 'r'){
            headless.readOnly();
        } else if (para == 'm'){
            headless.mineOnly();
        }
    }

    private long duration(){
        System.out.println("Time needed for file reading: " + (timestampNEW - timestampOLD)/1000);
        timestampOLD = System.currentTimeMillis();
        return (System.currentTimeMillis() - timestampOLD)/1000;
    }

    private char useArgs(String[] args){

        char modus = 'm';

        for (int i = 0; i < args.length; i = i + 2){
            switch (args[i]) {
                case "-d":
                    Configuration.getInstance().setPath("./src/main/resources/data/" + args[i+1] + ".ttl");
                    break;
                case "-s":
                    Configuration.getInstance().setShaclpath("./src/main/resources/gen/" + args[i+1] + ".ttl");
                    modus = 's';
                case "-dl":
                    Configuration.getInstance().setPath(args[i+1] + ".ttl");
                    break;
                case "-sl":
                    Configuration.getInstance().setShaclpath(args[i+1] + ".ttl");
                    modus = 'c';
                    break;
                case "-r":
                    modus = 'r';
                    break;
                case "-m":
                    modus = 'm';
                default:
                        break;
            }
        }
        return modus;
    }

    private void mineOnly() {


        try {
            sp.split();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Spaceships Power is on start level...");
        Iterator it = FileUtils.iterateFiles(new File("/home/nikelski/wikidata-fragments/wd20150615/"), null, false);
        System.out.println("GO!");
        while(it.hasNext()){
            System.out.println("BREAKPOINT REACHED! LAUNCH THE NUKULARS");
            Configuration.getInstance().setPath(((File) it.next()).getAbsolutePath());
            System.out.println("Path: " + Configuration.getInstance().getPath());
            mr.readFile();
            qb.buildClasses();
            System.out.println("BREAKPOINT CLEAR! Continue journey.");
        }
        //cf.build(mr.getModel());
        //mr.mine();
        System.out.println("Mission complete!");
    }

    /**
     * Method to run through the whole process
     */
    private void complete(){
        //Configuration.getInstance().setPath("D:/Wikidata/wikidata-20150615-all-BETA.ttl/wikidata-20150615-all-BETA.ttl");
        mr.readFile();
        System.out.println("Time needed for file reading: " + (timestampNEW - timestampOLD)/1000);
        timestampOLD = System.currentTimeMillis();


        cf.build(mr.getModel());

        mr.mine();




        System.out.println("Time needed for class building: " + (timestampNEW - timestampOLD)/1000);
        timestampOLD = System.currentTimeMillis();
        mr.writeFile(Lang.TSV, cf);
        timestampNEW = System.currentTimeMillis();
        System.out.println("Time needed for file creation: " + (timestampNEW - timestampOLD)/1000);
        timestampOLD = System.currentTimeMillis();
        findKeys();
        timestampNEW = System.currentTimeMillis();
        System.out.println("Time needed for key finding: " + (timestampNEW - timestampOLD)/1000);
        timestampOLD = System.currentTimeMillis();
        buildShacl();
        timestampNEW = System.currentTimeMillis();
        System.out.println("Time needed for creating shacl: " + (timestampNEW - timestampOLD)/1000);
        timestampOLD = System.currentTimeMillis();
        evaluateShacl();
        timestampNEW = System.currentTimeMillis();
        System.out.println("Time needed for evaluating shacl: " + (timestampNEW - timestampOLD)/1000);
    }

    /**
     * Method to run through the whole process
     */
    private void readOnly(){
        //Configuration.getInstance().setPath("D:/Wikidata/wikidata-20150615-all-BETA.ttl/wikidata-20150615-all-BETA.ttl");
        System.out.println("Start with the reading process:");
        mr.readFile();
        //cf.build(mr.getModel());
        //mr.writeFile(Lang.TSV, cf);
        //findKeys();
        //buildShacl();
    }

    private void withoutKeyFinding(){
        mr.readFile();
        cf.build(mr.getModel());
        mr.writeFile(Lang.TSV, cf);
        evaluateShacl();
    }

    private void findKeys(){
        String[] args = new String[2];
        args[0] = Configuration.getInstance().getTsvpath();
        args[1] = "1";
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
        /**
        try {
            VICKEY.main(args);
            System.out.println(VICKEY.nonKeySet);
            cf.setConditionalKeys(mr.getModel(), VICKEY.conditionalKeys);
            System.oshut.println("Conditional keys found and saved");
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to find and save Conditional keys");
        }
        */
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
