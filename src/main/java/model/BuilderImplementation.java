package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class BuilderImplementation {

    protected String replace(HashMap<String, String> prefixes, String s){
        Iterator it = prefixes.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(s.contains(pair.getValue().toString()) && (countSlashes(s) == countSlashes(pair.getValue().toString()))){
                return (pair.getKey().toString() + ":" + s.replace(pair.getValue().toString(), "").replace(">", ""));
            }
        }
        return "";
    }

    private static int countSlashes(String str) {
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            char currentLetter = str.charAt(i);
            if (currentLetter == '/')
                count++;
        }
        return count;
    }

    protected String buildShort(HashMap<String, String> prefixes, String s){
        Iterator it = prefixes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(s.contains(pair.getValue().toString()) && !pair.getValue().equals("")){
                if(pair.getKey().equals("")){
                    //return trimToName(s);
                }
                return (pair.getKey().toString() + ":" + trimToName(s));
            } else {
                //return trimToName(s);
            }
        }
        return "";
    }

    protected String trimToName(String s){
        s = s.split("%§%")[1];
        String[] split = s.split("/");
        //return split[split.length - 1];
        return split[split.length - 1].replace(">", "").replace(".","DOT").replace("-","DASH").replace("_","US").replace(":", "0");
    }

    protected String trimTo(String s) {
        String[] split = s.split("/");
        //return split[split.length - 1];
        return split[split.length - 1].replace(">", "");
    }

}
