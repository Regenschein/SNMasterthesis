package modelbuilder;

import controller.Configuration;
import model.Model;
import model.Triple;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TurtleToModelTransformator implements Transformer{

    private Set<Triple> tripleSet = new HashSet<Triple>();

    @Override
    public void transform(String file) {
        try {
            parseFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildClassObject(String triples){
        StringBuilder subjectB = new StringBuilder();
        String subject = "";
        boolean curInLiteral = false;
        for (char c : triples.toCharArray()){
            if (c == '\"' && curInLiteral == false){
                curInLiteral = true;
            } else if(c == '\"' && curInLiteral == true){
                curInLiteral = true;
            }
            if (subject.equals("") && (curInLiteral == true || c != ' ')){
                subjectB.append(c);
            } else if(subject.equals("")){
                subject = subjectB.toString();
                System.out.println(subject);
            } else if (!subject.equals("")){

                //TODO: Continue here.


            }
        }
    }

    private void parseSubject(String triples){

        Model model = Model.getInstance();

        String comaPatt = "\\s*,\\s*";
        String semiPatt = "\\s*;\\s*";
        Pattern p = Pattern.compile(comaPatt);
        Matcher m = p.matcher(triples);
        triples = m.replaceAll(", ");
        p = Pattern.compile(semiPatt);
        m = p.matcher(triples);
        triples = m.replaceAll("; ");
        String[] splitted = triples.split(" ");
        System.out.println("---------------------------------------");
        boolean inLiteral = false;
        String subject = "";
        String predicate = "";
        StringBuilder object = new StringBuilder();

        for (String s : splitted){
            if (subject.equals(""))
                subject = s;
            else if (!s.equals(" ") && predicate.equals("")){
                predicate = s;
            } else if (!s.equals(" ") && !predicate.equals("")){
                if(s.startsWith("\"")){
                    inLiteral = true;
                }
                if(s.substring(1).contains("\"")){
                    inLiteral = false;
                }
                if (inLiteral){
                    object.append(s + " ");
                }
                else if(!inLiteral){
                    object.append(s);
                    boolean lastObject = false;
                    if(object.toString().endsWith(",")){
                        lastObject = false;
                        object = object.deleteCharAt(object.length() -1);
                    } else if(object.toString().endsWith(";")){
                        lastObject = true;
                        object = object.deleteCharAt(object.length() -1);
                    }
                    if (!object.toString().equals(".")){
                        //tripleSet.add(new Triple(subject, predicate, object.toString()));
                        model.fill(new Triple(subject, predicate, object.toString()));
                        object = new StringBuilder();
                        if(lastObject == true){
                            predicate = "";
                        }
                    }
                }

            }
            System.out.println(s);
        }
    }

    private void parseFile() throws IOException {
        BufferedReader br = null;
        try {
            System.out.println(Configuration.getInstance().getPath());
            br = new BufferedReader(new FileReader(new File(Configuration.getInstance().getPath())));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                if(!line.startsWith("prefix", 1)){
                    sb.append(line);
                    if(!line.equals("")){
                        if (line.charAt(line.length() - 1) == '.'){
                            //buildClassObject(sb.toString());
                            parseSubject(sb.toString());
                            sb = new StringBuilder();
                        }
                    }
                } else {
                    String[] split = line.split(" +");
                    if (split.length == 3)
                        Model.getInstance().addPrefix(split[1].substring(0, split[1].length() -1), split[2].substring(0, split[2].length() -2));
                    else {
                        Model.getInstance().addPrefix(split[1].substring(0, split[1].length() -1), split[2].substring(0, split[2].length() -2));
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
