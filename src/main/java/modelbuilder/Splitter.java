package modelbuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Splitter {

    public Splitter(){

    }

    private boolean isEntitiyEndingPoint(byte[] chars){
        if (chars[4] == ' ')
            return true;
        if (chars[4] == '"' || (chars[3] == '"' && chars[4] == '"'))
            return true;
        if (chars[0] == '@' || chars[1] == '@' || chars[2] == '@')
            return true;
        return false;
    }

    public void split() throws IOException {
        FileInputStream in = new FileInputStream("/home/nikelski/wikidata-20150615-all-BETA.ttl");
        //FileInputStream in = new FileInputStream("D:\\Wikidata\\wikidata-20150615-all-BETA.ttl\\wikidata-20150615-all-BETA.ttl");
        //final int split_size = 1024000000 ;
        //final int split_size = 1024000000 ; 14303975
        final int split_size = 512000000 ;
        byte[] data = new byte[split_size];
        byte[] dataOld = new byte[]{};;
        //ArrayList<Byte> data = new ArrayList<>();
        int numbytes = 0;
        for(int i = 0; (numbytes = in.read(data)) != -1; i++){
            ArrayList<Byte> arrayList = new ArrayList<>();
            ArrayList<Byte> arrayListPrefixes = new ArrayList<>();
            if(data[data.length - 1] != '.'){
                //for (int o = 1; (data[data.length - o] != '.' && data[data.length - (o + 1)] != ' ') ; o++){
                for (int o = 1; (data[data.length - o] != '.' || (data[data.length -o] == '.' && !isEntitiyEndingPoint(new byte[]{data[data.length - (o + 5)], data[data.length - (o + 4)], data[data.length - (o + 3)], data[data.length - (o + 2)], data[data.length - (o + 1)]}))) ; o++){
                    arrayList.add(data[data.length - o]);
                    data[data.length - o] = ' ';
                }
                Collections.reverse(arrayList);
            }

            boolean inUri = false;
            boolean inGansefoot = false;
            for(int a = 0; a < data.length; a++)
            {
                if (data[a] == '"'){
                    if (a != 0) {
                        if (data[a - 1] != '\\'){
                            if (inGansefoot)
                                inGansefoot = false;
                            else {
                                inGansefoot = true;
                            }
                        }
                    } else {
                        if (inGansefoot)
                            inGansefoot = false;
                        else {
                            inGansefoot = true;
                        }
                    }
                }

                if(!inGansefoot){
                    if (data[a] == '<' && (data[a - 1] == '\n' || data[a - 1] == ' '))
                        inUri = true;
                    if (data[a] == '>' && (data[a +1] == '\n' || data[a + 1] == ' ' || data[a + 1] == ';' || data[a + 1] == ',' || data[a + 1] == '.'))
                        inUri = false;
                    if(inUri && data[a] == ' ')
                        data[a] = 's';
                }

            }

            //FileOutputStream of = new FileOutputStream("D:\\Wikidata\\wikidata-20150615-all-BETA.ttl\\wikidataSplitti"+(i + 1)+".ttl", false);
            FileOutputStream of = new FileOutputStream("/home/nikelski/wikidata-fragments/wd20150615/splittedWiki"+(i + 1)+".ttl", false);
            of.write(dataOld, 0, dataOld.length);
            of.write(data, 0, numbytes);
            of.close();

            for (String s : getPrefixe()){
                for(byte b : s.getBytes()){
                    arrayListPrefixes.add(b);
                }
            }

            arrayListPrefixes.addAll(arrayList);

            dataOld = new byte[arrayListPrefixes.size()];
            int counter = 0;

            for (byte b : arrayListPrefixes){
                dataOld[counter] = b;
                counter++;
            }
            System.out.println("Launching Reactor #" + i + ".");
        }
        in.close();
    }


    private HashSet<String> getPrefixe(){

        HashSet<String> prefixSet = new HashSet<>();
        BufferedReader bufferedReader = null;
        String filePath ="/home/nikelski/wikidata-20150615-all-BETA.ttl";
        //String filePath ="D:\\Wikidata\\wikidata-20150615-all-BETA.ttl\\wikidata-20150615-all-BETA.ttl";
        File file = new File(filePath);
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            boolean breaky = false;
            while ((null != (line = bufferedReader.readLine())) && breaky == false) {
                if (!line.startsWith("@")){
                    breaky = true;
                } else {
                    prefixSet.add(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prefixSet;
    }

}
