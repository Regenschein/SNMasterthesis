package modelbuilder;

import controller.Configuration;
import model.Model;
import model.Triple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class N3ToModelTransformator implements Transformer{

    @Override
    public void transform(String file) {
        try {
            parseFile();
            System.out.println("BP");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseFile() throws IOException {
        Model model = Model.getInstance();
        BufferedReader br = null;
        try {
            System.out.println(Configuration.getInstance().getPath());
            br = new BufferedReader(new FileReader(new File(Configuration.getInstance().getPath())));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                String[] spo = line.split("> <|> ");
                try {
                    model.fill(new Triple(spo[0].substring(1), spo[1], spo[2]));
                } catch (Exception e) {
                    System.out.println(line);
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
