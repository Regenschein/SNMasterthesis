package controller;

import keyfinder.SAKeyAlmostKeysOneFileOneN;
import keyfinder.VICKEY;
import modelbuilder.ClassFinder;
import modelbuilder.Modelreader;
import modelbuilder.RdfClass;
import modelbuilder.Splitter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RiotException;
import querybuilder.Querybuilder;
import querybuilder.QuerybuilderImplementation;
import shaclbuilder.Shaclbuilder;
import shaclbuilder.ShaclbuilderImplementation;

import java.io.*;
import java.util.*;

public class Headless {

    static Shaclbuilder s = new ShaclbuilderImplementation();
    ClassFinder cf = new ClassFinder();
    Modelreader mr = new Modelreader();
    Querybuilder qb = new QuerybuilderImplementation();
    Splitter sp = new Splitter();

    static long timestampOLD = System.currentTimeMillis();
    static long timestampNEW = System.currentTimeMillis();
    static int conditionalKeyShapefiles = 6;

    public static void main(String[] args){

        //evaluateAll();
        //System.exit(0);

        Headless headless = new Headless();
        //Configuration.getInstance().setPath("C:/Users/Basti/Dropbox/Masterarbeit/2 - code/SNMasterthesis/src/main/resources/eval/sparql-test-data.ttl");

        //Configuration.getInstance().setPath("./src/main/resources/data/" + "world-0.1" + ".ttl");
        //Configuration.getInstance().setPath("./src/main/resources/data/" + "Universities-10mB" + ".ttl");
        //Configuration.getInstance().setPath("D:/Wikidata/Splitterino/" + "SplittedWiki1" + ".ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20150615/wdnoP105.ttl");  // -------------------------------------------------
        //Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20180319/wdnoP105.ttl");  // -------------------------------------------------
        //Configuration.getInstance().setPath("/home/nikelski/wikidata-20170821-all-BETA.ttl");

        char para = headless.useArgs(args);

        if(para == 'c'){
            headless.complete();
        } else if(para == 's'){
            headless.withoutKeyFinding();
        } else if (para == 'r'){
            headless.readOnly();
        } else if (para == 'm'){
            headless.mineOnly(true);
        } else if (para == 'a'){
            headless.mineOnly(false);
        } else if (para == 'n'){
            headless.mineSpecificClass(true);
        } else if (para == 'l'){
            headless.evaluateShacl();
        }
    }

    private static long duration(){
        //System.out.println("Time needed for file reading: " + (timestampNEW - timestampOLD));
        timestampOLD = System.currentTimeMillis();
        return (System.currentTimeMillis() - timestampOLD);
    }

    private char useArgs(String[] args){

        char modus = 'c';

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
                    break;
                case "-c":
                    modus = 'c';
                    break;
                case "-a":
                    modus = 'a';
                    break;
                case "-n":
                    modus = 'n';
                    break;
                case "-l":
                    modus = 'l';
                    break;
                default:
                        break;
            }
        }
        System.out.println("Modus: " + modus);
        return modus;
    }

    private void mineOnly(boolean newStart) {

        if (newStart) {
            try {
                sp.split();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Spaceships Power is on start level...");
        Iterator it = FileUtils.iterateFiles(new File("/home/nikelski/wikidata-fragments/latest-all/"), null, false);
        System.out.println("GO!");
        int counter = 0;
        while(it.hasNext()){
            System.out.println("BREAKPOINT REACHED! LAUNCH THE NUKULARS");
            String path = ((File) it.next()).getAbsolutePath();
            //Configuration.getInstance().setPath(((File) it.next()).getAbsolutePath());
            Configuration.getInstance().setPath(path);
            System.out.println("Path: " + Configuration.getInstance().getPath());
            try {
                mr.readFile();
                qb.buildClasses();
            }catch(RiotException e){
                e.printStackTrace();
            }
            System.out.println("BASES DESTROYED: (" + counter + "/?)");
            counter++;
            new File(path).delete();
        }
        System.out.println("Mission complete!");
    }

    private void mineSpecificClass(boolean newStart){
            if (newStart) {
                try {
                    sp.splitAndMine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * Method to run through the whole process
     */
    private void complete(){
        mr.readFilePlus();
        mr.writeFile(Lang.TTL);
        Configuration.getInstance().setPath(Configuration.getInstance().getTtlpath());
        cf.buildPlus(mr.getModel(), "http://www.wikidata.org/prop/novalue/P105");    //-------------------------------------------------------------------------------------------
        //cf.build(mr.getModel());
        mr.mine();
        mr.writeFile(Lang.TSV, cf);
        findKeys();
        buildShacl();
        evaluateShaclN();
    }

    private void readOnly(){
        System.out.println("Start with the reading process:");
        mr.readFile();
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
        args[1] = "0";

/*        try {
            SAKeyAlmostKeysOneFileOneN.main(args);

            cf.setAlmostKeys(mr.getModel(), SAKeyAlmostKeysOneFileOneN.almostKeys);
            System.out.println("Almost keys found and saved");
        } catch (IOException e) {
            System.out.println("Failed to find and save Almost keys");
        }*/

        args = new String[1];
        args[0] = Configuration.getInstance().getTsvpath();

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
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile.ttl");
        //Configuration.getInstance().setShaclpath("C:/Users/Basti/Dropbox/Masterarbeit/2 - code/SNMasterthesis/src/main/resources/eval/sparql-shape-test.ttl"); //----------------------------
        s.buildNonKeys(mr.getModel(), cf.getClasses());
        s.buildAlmostKeys(mr.getModel(), cf.getClasses());
        conditionalKeyShapefiles = s.buildConditionalKeys(mr.getModel(), cf.getClasses());
        System.out.println("Build Shapefiles: #" + conditionalKeyShapefiles);
    }

    private static void evaluateShaclN(){
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile.ttl");

        try{
            //s.build(Configuration.getInstance().getShaclpath().replace(".ttl", "--MaxNonKeys.ttl"));
        } catch(Exception e){
            System.out.println("Error during the evaluation of the MaxNonKeys");
            e.printStackTrace();
        }
        try{
            s.build(Configuration.getInstance().getShaclpath().replace(".ttl", "--AlmostKeys.ttl"));
        } catch(Exception e){
            System.out.println("Error during the evaluation of the AlmostKeys");
            e.printStackTrace();
        }
        for (int i = 0; i <= conditionalKeyShapefiles; i++){
            try{
                s.build(Configuration.getInstance().getShaclpath().replace(".ttl", "--ConditionalKeys - " + i + ".ttl"));
            } catch(Exception e){
                System.out.println("Error during the evaluation of the ConditionalKeys");
                e.printStackTrace();
            }
        }
    }

    private static void evaluateShaclHere(){
        //Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--ConditionalKeys - 0.ttl");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile-NormalKeys.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20160502/wdnoP102.ttl");
        s.build();
    }

    private static void evaluateAll(){
        // *************************************************************** P105 **************************************************************
        /*
            2016
         */
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--MaxNonKeys-P105.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20160502/wdnoP105.ttl");
        s.buildWithName("-2016-MaxNonKeys-P105");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--0AlmostKeys-P105.ttl");
        s.buildWithName("-2016-AlmostKeys-P105");
        /*
            2017
         */
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--MaxNonKeys-P105.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20170821/wdnoP105.ttl");
        s.buildWithName("-2017-MaxNonKeys-P105");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--0AlmostKeys-P105.ttl");
        s.buildWithName("-2017-AlmostKeys-P105");
        /*
            2018
         */
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--MaxNonKeys-P105.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20180319/wdnoP105.ttl");
        s.buildWithName("-2018-MaxNonKeys-P105");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--0AlmostKeys-P105.ttl");
        s.buildWithName("-2018-AlmostKeys-P105");
        // *************************************************************** P102 **************************************************************
        /*
            2016
         */
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--MaxNonKeys-P102.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20160502/wdnoP102.ttl");
        s.buildWithName("-2016-MaxNonKeys-P102");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--0AlmostKeys-P102.ttl");
        s.buildWithName("-2016-AlmostKeys-P102");
        /*
            2017
         */
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--MaxNonKeys-P102.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20170821/wdnoP102.ttl");
        s.buildWithName("-2017-MaxNonKeys-P102");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--0AlmostKeys-P102.ttl");
        s.buildWithName("-2017-AlmostKeys-P102");
        /*
            2018
         */
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--MaxNonKeys-P102.ttl");
        Configuration.getInstance().setPath("/home/nikelski/Headless/src/main/resources/generated/classes-wd20180319/wdnoP102.ttl");
        s.buildWithName("-2018-MaxNonKeys-P102");
        Configuration.getInstance().setShaclpath("/home/nikelski/Headless/src/main/resources/generated/shapefile--0AlmostKeys-P102.ttl");
        s.buildWithName("-2018-AlmostKeys-P102");
    }

    private static void evaluateShacl(){
        List<String> results = new LinkedList<String>();
        boolean init = false;
        for (int i = 1; i < 1001; i = i * 10){
            Configuration.getInstance().setPath("/home/nikelski/lubm/lubm-" + i + "-o.ttl");
            for (int x = 1; x < 1001; x = x * 10 ){
                Configuration.getInstance().setShaclpath("/home/nikelski/lubm/ShapefileShaclLubm-" + x + ".ttl");
                if(init == false){
                    init = true;
                    s.build();
                }
                timestampOLD = System.currentTimeMillis();
                try{
                    s.build();
                    long evaltime = System.currentTimeMillis() - timestampOLD;
                    String res = "Evaluation time for lubm-" + i + "with SHAQL-ShapefileLubm-" + x + ":" + evaltime + "ms.";
                    results.add(res);
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter("eval-shaql-lubm.txt", true);
                        fileWriter.write(res + "\n");
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch(OutOfMemoryError outOfMemoryError){
                    System.out.println("MEMORY IST ALLE");
                }
            }
        }
        for (String res : results){
            System.out.println(res);
        }
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
