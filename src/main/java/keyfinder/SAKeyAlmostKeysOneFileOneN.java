/*
 * Decompiled with CFR 0_123.
 */
package keyfinder;

import sakey.compTreeSet;
import sakey.keyFinder;
import sakey.newDiscoverNonKeys5AlmostNew;
import sakey.tripleFileParser;
import sakey.tripleFileParserCWA;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SAKeyAlmostKeysOneFileOneN {
    static String dataInputFileName1 = null;
    static String option = "Almost_newGraphPruning";
    static Integer numberOfExceptions = null;
    public static HashMap<String, String> sameAsLink = new HashMap();
    static boolean oneClass = true;

    public static HashSet<HashSet<String>> almostKeys = new HashSet<HashSet<String>>();

    public static void main(String[] args) throws IOException {
        try {
            dataInputFileName1 = args[0];
            numberOfExceptions = Integer.parseInt(args[1]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("File or threshold not given");
        }
        HashSet<HashSet<Integer>> keySet = new HashSet();
        TreeSet<compTreeSet> nonKeySet = new TreeSet<compTreeSet>();
        tripleFileParser fileParser = new tripleFileParser();
        if (option.equals("Almost_newGraphPruning") && dataInputFileName1 != null && numberOfExceptions != null) {
            long time = System.currentTimeMillis();
            keyFinder.propertiesSet = new compTreeSet();
            keyFinder.complementSets = new TreeSet();
            keyFinder.propertyCount = new HashMap();
            keyFinder.countProperty = new TreeMap();
            keyFinder.currentPropertyCountPerPro = new HashMap();
            keyFinder.currentCountPropertyPerPro = new TreeMap();
            keyFinder.currentComplementSetsPerPro = new HashMap();
            keyFinder.maxCountPropertyPerProperty = new TreeMap();
            BufferedReader br = new BufferedReader(new FileReader(dataInputFileName1));
            String line = null;
            HashMap<Integer, HashMap<Integer, compTreeSet>> proObjSubMap = null;
            line = br.readLine();
            if (line != null) {
                //proObjSubMap = line.contains("<") ? tripleFileParser.proObjSubMapOneClass(dataInputFileName1, "http://chefmoz.org/rdf/elements/1.0/restaurant.owl#Restaurant") : tripleFileParser.proObjSubMapOneClassSpace(dataInputFileName1, "http://chefmoz.org/rdf/elements/1.0/restaurant.owl#Restaurant");
                proObjSubMap = line.contains("<") ? tripleFileParser.proObjSubMapOneClass(dataInputFileName1) : tripleFileParser.proObjSubMapOneClassSpace(dataInputFileName1);
            }
            ArrayList<TreeSet<compTreeSet>> proSubList = new ArrayList<TreeSet<compTreeSet>>(tripleFileParser.proSubListCreationFinalNewPrun(proObjSubMap));
            TreeSet<compTreeSet> singleNonKeys = tripleFileParser.nonKeySetList(proObjSubMap);
            for (compTreeSet singleNonkey : singleNonKeys) {
                if (tripleFileParser.proInst.get((Integer)singleNonkey.first()).size() < numberOfExceptions) continue;
                nonKeySet.add(singleNonkey);
            }
            TreeMap proProSubsTMap = new TreeMap();
            TreeSet nonkeys = new TreeSet();
            newDiscoverNonKeys5AlmostNew discoverNonKeys5Almost = new newDiscoverNonKeys5AlmostNew();
            nonKeySet.addAll(discoverNonKeys5Almost.nonKeyFinderValInd(proSubList, option, numberOfExceptions));
            long timeMinutes = (System.currentTimeMillis() - time) / 1000;
            nonKeySet.addAll(nonkeys);
            TreeSet<compTreeSet> newNonKeySet = new TreeSet<compTreeSet>();
            for (compTreeSet nonKey : nonKeySet) {
                compTreeSet newNonKey = new compTreeSet();
                Iterator i$ = nonKey.iterator();
                while (i$.hasNext()) {
                    int pro = (Integer)i$.next();
                    int proOld = tripleFileParser.newPropertyToOld.get(pro);
                    newNonKey.add(proOld);
                }
                newNonKeySet.add(newNonKey);
            }
            SAKeyAlmostKeysOneFileOneN ex = new SAKeyAlmostKeysOneFileOneN();
            HashSet<HashSet<String>> realNonKeyset = ex.nonkeyPrinter(newNonKeySet, tripleFileParser.propertyToPropertyReal, tripleFileParser.intToStringWord);
            System.out.println(numberOfExceptions + "-non keys: " + realNonKeyset);
            keySet = keyFinder.keyFinderNew3(newNonKeySet, tripleFileParser.propertyToPropertyReal.keySet());
            HashSet<HashSet<String>> realKeyset = ex.keyPrinter(keySet, tripleFileParser.propertyToPropertyReal, tripleFileParser.intToStringWord);
            System.out.println("");
            almostKeys = realKeyset;
            System.out.println("" + (numberOfExceptions - 1) + "-almost keys:" + realKeyset);
        }
    }

    public void nonKeyPrinter(HashMap<Integer, HashMap<Integer, compTreeSet>> proObjSubMap, TreeSet<compTreeSet> nonKeySet, TreeSet<compTreeSet> uNKeySet) {
        int a;
        String nonkeyString;
        int p;
        Iterator<Integer> i$;
        Iterator iter;
        String nonkey;
        int pro;
        tripleFileParser dis = new tripleFileParser();
        nonKeySet = tripleFileParser.simplifyNonKeySet(nonKeySet);
        for (compTreeSet set : nonKeySet) {
            nonkeyString = null;
            nonkey = null;
            iter = set.iterator();
            while (iter.hasNext()) {
                a = (Integer)iter.next();
                p = 0;
                i$ = proObjSubMap.keySet().iterator();
                while (i$.hasNext()) {
                    pro = i$.next();
                    if (p == a) {
                        p = pro;
                        break;
                    }
                    ++p;
                }
                nonkey = nonkey + ", " + a;
                nonkeyString = nonkeyString + ", " + tripleFileParser.intToStringWord.get(p);
            }
            System.out.println("nonKey:" + nonkeyString);
        }
        uNKeySet = tripleFileParser.simplifyNonKeySet(uNKeySet);
        for (compTreeSet set : uNKeySet) {
            nonkeyString = null;
            nonkey = null;
            iter = set.iterator();
            while (iter.hasNext()) {
                a = (Integer)iter.next();
                p = 0;
                i$ = proObjSubMap.keySet().iterator();
                while (i$.hasNext()) {
                    pro = i$.next();
                    if (p == a) {
                        p = pro;
                        break;
                    }
                    ++p;
                }
                nonkey = nonkey + ", " + a;
                nonkeyString = nonkeyString + ", " + tripleFileParser.intToStringWord.get(p);
            }
            System.out.println("uNKey:" + nonkeyString);
        }
    }

    public HashSet<HashSet<String>> nonKeyPrinter2(tripleFileParser newFileParser, HashMap<Integer, Integer> propertyToPropertyReal, HashMap<Integer, String> intToStringWord, TreeSet<compTreeSet> nonKeySet) {
        nonKeySet = tripleFileParser.simplifyNonKeySet(nonKeySet);
        HashSet<HashSet<String>> realNonKeySet = new HashSet<HashSet<String>>();
        for (compTreeSet nonKey : nonKeySet) {
            HashSet<String> realNonKey = new HashSet<String>();
            for (Integer nonKeyPart : nonKey) {
                realNonKey.add(intToStringWord.get(propertyToPropertyReal.get(nonKeyPart)));
            }
            realNonKeySet.add(realNonKey);
        }
        System.out.println("nonKeySet::" + realNonKeySet);
        return realNonKeySet;
    }

    public void nonKeyPrinter2(tripleFileParserCWA newFileParser, HashMap<Integer, Integer> propertyToPropertyReal, HashMap<Integer, String> intToStringWord, TreeSet<compTreeSet> nonKeySet) {
        nonKeySet = newFileParser.simplifyNonKeySet(nonKeySet);
        HashSet realNonKeySet = new HashSet();
        for (compTreeSet nonKey : nonKeySet) {
            HashSet<String> realNonKey = new HashSet<String>();
            for (Integer nonKeyPart : nonKey) {
                realNonKey.add(intToStringWord.get(propertyToPropertyReal.get(nonKeyPart)));
            }
            realNonKeySet.add(realNonKey);
        }
        System.out.println("nonKeySet:" + realNonKeySet);
    }

    public HashSet<HashSet<String>> keyPrinter(HashSet<HashSet<Integer>> keySet, HashMap<Integer, Integer> propertyToPropertyReal, HashMap<Integer, String> intToStringWord) throws IOException {
        HashSet<HashSet<String>> realKeySet = new HashSet<HashSet<String>>();
        for (HashSet<Integer> key : keySet) {
            HashSet<String> realKey = new HashSet<String>();
            for (Integer keyPart : key) {
                realKey.add(intToStringWord.get(propertyToPropertyReal.get(keyPart)));
            }
            realKeySet.add(realKey);
        }
        return realKeySet;
    }

    public HashSet<HashSet<String>> nonkeyPrinter(TreeSet<compTreeSet> nonKeySet, HashMap<Integer, Integer> propertyToPropertyReal, HashMap<Integer, String> intToStringWord) throws IOException {
        HashSet<HashSet<String>> realKeySet = new HashSet<HashSet<String>>();
        for (compTreeSet nonkey : nonKeySet) {
            HashSet<String> realKey = new HashSet<String>();
            for (Integer keyPart : nonkey) {
                realKey.add(intToStringWord.get(propertyToPropertyReal.get(keyPart)));
            }
            realKeySet.add(realKey);
        }
        BufferedWriter pw = new BufferedWriter(new FileWriter("keys.nt"));
        for (HashSet key : realKeySet) {
            pw.write(key.toString() + "\n");
        }
        pw.close();
        return realKeySet;
    }

    public void propertyPrinter(compTreeSet set, HashMap<Integer, Integer> propertyToPropertyReal, HashMap<Integer, String> intToStringWord) throws IOException {
        HashSet realKeySet = new HashSet();
        HashSet<String> realKey = new HashSet<String>();
        for (Integer keyPart : set) {
            realKey.add(intToStringWord.get(propertyToPropertyReal.get(keyPart)));
        }
        realKeySet.add(realKey);
        BufferedWriter pw = new BufferedWriter(new FileWriter("keys.nt"));
        pw.write(realKeySet.toString());
        pw.close();
        System.out.println("!set:" + realKeySet);
    }

    public void SetPrinter(compTreeSet set, HashMap<Integer, Integer> propertyToPropertyReal, HashMap<Integer, String> intToStringWord) throws IOException {
        HashSet realKeySet = new HashSet();
        HashSet<String> realKey = new HashSet<String>();
        for (Integer keyPart : set) {
            realKey.add(intToStringWord.get(keyPart));
        }
        realKeySet.add(realKey);
        BufferedWriter pw = new BufferedWriter(new FileWriter("keys.nt"));
        pw.write(realKeySet.toString());
        pw.close();
        System.out.println("set:" + realKeySet);
    }

    public compTreeSet findPropNumber(HashMap<Integer, HashMap<Integer, compTreeSet>> proObjSubMap, compTreeSet nonKey) {
        tripleFileParser dis = new tripleFileParser();
        compTreeSet realNonKeySet = new compTreeSet();
        Object nonkeyString = null;
        Object nonkey = null;
        Iterator iter = nonKey.iterator();
        while (iter.hasNext()) {
            int a = (Integer)iter.next();
            int p = 0;
            Iterator<Integer> i$ = proObjSubMap.keySet().iterator();
            while (i$.hasNext()) {
                int pro = i$.next();
                if (p == a) {
                    p = pro;
                    break;
                }
                ++p;
            }
            realNonKeySet.add(p);
        }
        return realNonKeySet;
    }
}

