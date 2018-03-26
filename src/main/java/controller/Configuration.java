package controller;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Configuration {

    private static ResourceBundle bundle;
    private static Locale locale = new Locale("en", "EN");
    private static Configuration instance;
    private static String language = "";
    private static String country = "";
    private static String modus = "";
    private static String path = "";
    private static String tsvpath = "";
    private static String n3path = "";

    private Configuration(){
        parseProperties();
    }

    public static Configuration getInstance () {
        if (Configuration.instance == null) {
            Configuration.instance = new Configuration ();
        }
        return Configuration.instance;
    }

    private void parseProperties() {
        String result = "";
        InputStream inputStream;

        try {
            Properties properties = new Properties();
            String propFileName = "Properties/config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                System.out.println("property file '" + propFileName + "' not found in the classpath");
            }

            language = properties.getProperty("language");
            country = properties.getProperty("country");
            modus = properties.getProperty("modus");
            path = properties.getProperty("path");
            tsvpath = properties.getProperty("tsvpath");
            n3path = properties.getProperty("n3path");
            locale = new Locale(language, country);
            bundle = ResourceBundle.getBundle("Properties/properties", locale);
        } catch(IOException ignored){

        } catch(NullPointerException np){
            np.printStackTrace();
        }
    }




    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String getLanguage() {
        return language;
    }

    public static String getCountry() {
        return country;
    }

    public static String getModus() {
        return modus;
    }

    public static String getPath() {
        return path;
    }

    public static String getTsvpath() {
        return tsvpath;
    }

    public static String getN3path() {
        return n3path;
    }
}

