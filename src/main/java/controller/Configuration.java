package controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Configuration {

    private static ResourceBundle bundle;
    private static Locale locale = new Locale("en", "EN");
    private static Configuration instance;
    private static String language;
    private static String country;
    private static String modus;
    private static String path;

    private Configuration(){
        try {
            parseProperties();
            locale = new Locale(language, country);
            bundle = ResourceBundle.getBundle("Properties/properties", locale);
        } catch (IOException e) {
            bundle = ResourceBundle.getBundle("Properties/properties", locale);
            System.out.println(bundle.getString("Configuration.properties.notLoaded"));
        }
    }

    public static synchronized Configuration getInstance () {
        if (Configuration.instance == null) {
            Configuration.instance = new Configuration ();
        }
        return Configuration.instance;
    }

    private void parseProperties() throws IOException {
        Properties properties = new Properties();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream("Properties/config"));
        properties.load(stream);
        stream.close();
        language = properties.getProperty("language");
        country = properties.getProperty("country");
        modus = properties.getProperty("modus");
        path = properties.getProperty("path");
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

}

