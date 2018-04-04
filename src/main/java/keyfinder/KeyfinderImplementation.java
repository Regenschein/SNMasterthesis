package keyfinder;

import com.github.jsonldjava.core.JSONLDProcessingError;
import fr.inrialpes.exmo.rdfkeys.algorithm.*;
import fr.inrialpes.exmo.rdfkeys.index.Index;
import fr.inrialpes.exmo.rdfkeys.index.POSIndex;
import fr.inrialpes.exmo.rdfkeys.index.PSOIndex;
import fr.inrialpes.exmo.rdfkeys.renderers.PSetRenderer;
import fr.inrialpes.exmo.rdfkeys.server.PyServer;
import fr.inrialpes.exmo.rdfkeys.server.PythonCallback;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import keyfinder.IndexerStreamRDF;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.*;
import org.apache.jena.atlas.AtlasException;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.RDFLanguages;
//import org.apache.jena.riot.RiotReader;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.adapters.RDFReaderFactoryRIOT;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py4j.GatewayServer;

public class KeyfinderImplementation implements Keyfinder{
    private final static Logger LOG = LoggerFactory.getLogger(KeyfinderImplementation.class);


    public static void extract(PythonCallback callback, String filePath, String keyType, Double supportTreshold, Double discriminabilityTreshold,
                               Boolean undefined, Set<String> rdfTypes, Set<String> rdfProperties, boolean eachTypes, String outputFile, int extractionPk, String rdfFormat) throws IOException {
        extract( callback,  filePath,  keyType,  supportTreshold,  discriminabilityTreshold,
                undefined,  rdfTypes,  rdfProperties,  eachTypes,  outputFile,  extractionPk,  rdfFormat,  false);
    }

    public static void extract(PythonCallback callback, String filePath, String keyType, Double supportTreshold, Double discriminabilityTreshold,
                               Boolean undefined, Set<String> rdfTypes, Set<String> rdfProperties, boolean eachTypes, String outputFile, int extractionPk, String rdfFormat, boolean allTested) throws IOException
    {
        Index index = null ;
        if(keyType.equals("simple"))
            index = new POSIndex() ;
        else if(keyType.equals("ads"))
            index = new PSOIndex() ;

        LOG.info("Indexing started");
        IndexerStreamRDF handler = new IndexerStreamRDF(index,rdfProperties);
        try
        {
            //Lang lang = RDFLanguages.filenameToLang(filePath) ;
            URLConnection cnx = new URL(filePath).openConnection();
            String mimeType = cnx.getHeaderField("content-type");
            if (mimeType!=null && mimeType.indexOf(';')>-1) {
                mimeType=mimeType.substring(0,mimeType.indexOf(';'));
            }
            InputStream input = cnx.getInputStream() ;

            RiotReader.parse(input, mimeType==null?null:RDFLanguages.contentTypeToLang(mimeType),handler) ;
            input.close();
        }
        catch (MalformedURLException e)
        {
            final File fin = new File(filePath);

            if (fin.isDirectory())
            {
                final File[] files = fin.listFiles();

                for (File f : files)
                {
                    try
                    {
                        RiotReader.parse(f.getAbsolutePath(), RDFLanguages.filenameToLang(f.getName()),handler ) ;
                    }
                    catch (AtlasException ae)
                    {
                        LOG.debug("{} is not a RDF file",f);
                    }
                }
            }
            else
            {
                RiotReader.parse(filePath, RDFLanguages.filenameToLang(filePath), handler) ;
            }
        }
        handler=null;

        LOG.info("Indexing finished");

        PSetFilter discFilter = new DiscFilter(discriminabilityTreshold);
        PSetFilter supportFilter = new SupportFilter(supportTreshold, index.getIndividualsNumber()); // TODO : check if getIndividualsNumber is the right value

        PrintStream out = null ;
        StringBuffer keySet = null ;
        if(null == callback)
        {
            if(null == outputFile)
                out = System.out ;
            else
            {
                File file = new File(outputFile) ;
                file.createNewFile() ;
                out = new PrintStream(file) ;
            }
        }
        else
            keySet = new StringBuffer() ;

        List<PSet2> psets = null ;
        KeyAlgorithm algo = null ;
        Iterable<PSet2> keys = null ;
        PSetRenderer renderer = new PSetRenderer() ;

        if (eachTypes)
            rdfTypes = index.getTypes();

        if(null != rdfTypes)
        {
            boolean noKey = true ;

            for (String rdfType : rdfTypes)
            {
                psets = index.getPsets(rdfType, undefined);
                index.swapToDisk();
                LOG.info("Key search for {} started",rdfType);
                algo = new KeyAlgorithm(psets, discFilter, supportFilter,allTested);


                keys = algo.getSelectedPseudoKeys();

                if (allTested) {
                    keys=algo.getTestedPSets();
                }

                if(keys.iterator().hasNext())
                    noKey = false ;

                for(PSet2 pset : keys)
                    renderer.put(rdfType, pset, index) ;


                LOG.info("Key search for {} finished",rdfType);
            }

            if(noKey && null == callback)
                System.exit(1) ;
        }
        else
        {
            LOG.info("Key search for the whole graph started");
            psets =  index.getPsets(undefined) ;
            index.swapToDisk();

            algo = new KeyAlgorithm(psets, discFilter, supportFilter, allTested) ;

            keys = algo.getSelectedPseudoKeys() ;
            if (allTested) {
                keys=algo.getTestedPSets();
            }

            if(! keys.iterator().hasNext() && null == callback)
                System.exit(1) ;

            for(PSet2 pset : keys)
                renderer.put(null, pset, index) ;

            LOG.info("Key search for the whole graph finished");
        }

        if(null != out)
        {
            out.println(renderer.toString(rdfFormat)) ;
            out.close() ;
        }
        else if(null != keySet)
            keySet.append(renderer.toString(rdfFormat)) ;

        if(null != out)
            out.close();

        if(null != callback)
            callback.save_keys(extractionPk, keySet.toString()) ;


    }

    public static void main(String[] args) throws IOException, JSONLDProcessingError
    {
        final String cmdLine = "java -jar pseudo-keys.jar";

        String filepath = null ;
        Double supportTreshold = 0d ;
        Double discTreshold = 1d ;
        String discMeasure ;
        String keyType = "ads" ;
        Boolean noValue = null ;
        String outputFile = null ;
        String rdfFormat = null ;
        //String type = null ;
        Set<String> types=null;
        boolean eachType = false ;
        Set<String> consideredProperties=null;

        Options options = new Options() ;

        Option server = new Option("pyserv", true, "starts the python server") ;
        server.setLongOpt("python-server");
        server.setRequired(false) ;
        server.setArgs(0);

        Option path = new Option("i", true, "the rdf file from which pseudo-keys will be computed. The file formats recognized are Turtle, RDF/XML, N-Triples, RDF/JSON, TriG, N-Quads") ;
        path.setArgName("file") ;
        path.setRequired(true) ;
        path.setType(java.lang.String.class) ;
        path.setLongOpt("input") ;

        Option key = new Option("k", true, "the type of the desired keys : value should be either 'simple' or 'ads'(default)") ;
        key.setArgName("key-type") ;
        key.setRequired(false) ;
        key.setType(java.lang.String.class) ;
        key.setLongOpt("key") ;

        Option undefined = new Option("u", false, "consider subjects with no value associated to a predicate (not set by default)") ;
        undefined.setRequired(false) ;
        undefined.setLongOpt("undefined") ;

        Option output = new Option("o", true, "the file where to put the keys. If nothing is specified the standard output is used") ;
        output.setArgName("file") ;
        output.setRequired(false) ;
        output.setType(java.lang.String.class) ;
        output.setLongOpt("output") ;

        Option format = new Option("f", true, "the format in which you want the keys to be rendered. Default is turtle.") ;
        format.setArgName("format");
        format.setRequired(false) ;
        format.setType(java.lang.String.class);
        format.setLongOpt("format") ;

        Option support = new Option("s", true, "the support threshold between 0 and 1 (0 by default)") ;
        support.setArgName("value") ;
        support.setRequired(false) ;
        support.setType(java.lang.Double.class) ;
        support.setLongOpt("support-threshold") ;

        Option discr = new Option("d", true, "the discriminability threshold between 0 and 1 (1 by default)") ;
        discr.setArgName("value") ;
        discr.setRequired(false) ;
        discr.setType(java.lang.Double.class) ;
        discr.setLongOpt("discriminability-threshold") ;

        Option measure = new Option("m", true, "the discriminability measure to use (option not available yet)") ;
        measure.setArgName("unit") ;
        measure.setRequired(false) ;
        measure.setType(java.lang.String.class) ;
        measure.setLongOpt("discriminability-measure") ;

        Option typeFiltering = new Option("t", true, "Compute pseudo-keys for each type (object of property rdf:type) given in the list. Each value of the list is given as an URI form (not prefix:atype form). " +
                "If the list is empty pseudo-keys will be computed for each type contained in the graph. If this option is not set the pseudo-keys will be computed for the whole graph.") ;
        typeFiltering.setArgName("list-of-types") ;
        typeFiltering.setOptionalArg(true) ;
        typeFiltering.setRequired(false) ;
        typeFiltering.setType(java.lang.String.class) ;
        typeFiltering.setLongOpt("types") ;
        typeFiltering.setArgs(100);

        Option properties = new Option("p", true, "the list of properties (space separated set) that will be considered (if not specified, all properties are considered)") ;
        properties.setArgName("list") ;
        properties.setRequired(false) ;
        properties.setType(java.lang.String.class) ;
        properties.setLongOpt("properties") ;
        properties.setOptionalArg(false);
        properties.setArgs(100);


        Option help = new Option("h", false, "print the help") ;
        help.setRequired(false) ;
        help.setLongOpt("help") ;

        // required options
        options.addOption(path) ;
        options.addOption(key) ;

        // not required options
        options.addOption(format) ;
        options.addOption(undefined) ;
        options.addOption(output) ;
        options.addOption(support) ;
        options.addOption(discr) ;
        options.addOption(measure) ;
        options.addOption(help) ;
        options.addOption(typeFiltering) ;
        options.addOption(properties);
        options.addOption(server);

        //CommandLineParser parser = new BasicParser() ;
        CommandLineParser parser = new DefaultParser() ;

        // setting up help output
        HelpFormatter hf = new HelpFormatter() ;
        String header = "KeyExtraction is a software for discovering pseudo-keys in RDF datasets.\n \n" ;
        String footer = "\n If you notice a bug please yell at jerome.david@inria.fr" ;


        try
        {
            Options optServer = new Options();
            //server.setRequired(true);
            optServer.addOption(server);
            if (parser.parse(optServer, args,true).hasOption("pyserv")) {
                startPyServer();
            }
            else {

                CommandLine line = parser.parse(options, args) ;
                if(line.hasOption('h'))
                    hf.printHelp(cmdLine, header, options, footer, true) ;
                else
                {
                    filepath = line.getOptionValue('i') ;

                    rdfFormat = line.getOptionValue('f') ;

                    if(line.hasOption('k'))
                        keyType = line.getOptionValue('k') ;

                    noValue = line.hasOption('u') ;

                    outputFile = line.getOptionValue('o') ;

                    if(line.hasOption('s'))
                        supportTreshold = Double.parseDouble(line.getOptionValue('s')) ;

                    if(line.hasOption('d'))
                        discTreshold = Double.parseDouble(line.getOptionValue('d')) ;

                    discMeasure = line.getOptionValue('m') ; // TODO this can be null, deal with it

                    if (line.hasOption('t')) {
                        String[] vals = line.getOptionValues('t');
                        eachType=(vals==null);
                        if (!eachType) {
                            types=new HashSet<String>();
                            for (String t : vals) types.add(t);
                        }
                    }


                    if (line.hasOption('p')) {
                        String[] props = line.getOptionValues('p');
                        consideredProperties=new HashSet<String>();
                        for (String p : props) {
                            consideredProperties.add(p);
                        }
                    }
                    KeyfinderImplementation.extract(null, filepath, keyType, supportTreshold, discTreshold, noValue, types,consideredProperties,  eachType, outputFile, 0, rdfFormat) ;
                }
            }
        }
        catch (ParseException e)
        {
            if(e instanceof MissingOptionException) {
                System.err.println("missing argument : -i <file> is required.") ;
            }
            else if(e instanceof UnrecognizedOptionException)
                System.err.println("unrecognized option : ") ;

            hf.printHelp(cmdLine, header, options, footer, true) ;

            System.exit(1) ;
        }



    }


    private static void startPyServer() {
        PyServer var0 = new PyServer();
        GatewayServer var1 = new GatewayServer(var0, 25698);
        var1.start();
    }


    @Override
    public void find(String[] args) throws IOException, InterruptedException {

    }
}
