package modelbuilder;

import controller.Configuration;
import java.io.*;
import java.util.Properties;

public class TurtleToTabSepTransformer implements Transformer{

    @Override
    public void transform(String file) {
        try {
            parseFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseFile() throws IOException {

        BufferedReader br = null;
        try {
            System.out.println(Configuration.getInstance().getPath());
            br = new BufferedReader(new FileReader(new File(Configuration.getInstance().getPath())));
            String line = null;
            while((line = br.readLine()) != null) {
                // Ganze Zeile:
                // System.out.println(line);
                //String[] parts = line.split("\"(?<!)\\w*\\s");
                //String[] parts = line.split("\\s(?<!\")");
                if(!line.startsWith("@prefix")) {
                    String[] parts = line.split(" ");
                    System.out.println("Vorname: " + parts[0]);
                    for (String s : parts){
                        if(s.startsWith("\"")){
                            //TODO concat " "
                        }
                    }

                }
                // ...
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
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
