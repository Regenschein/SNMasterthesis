package modelbuilder;


import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelbuilderImplementation implements Modelbuilder{

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( ModelbuilderImplementation.class );

    public ModelbuilderImplementation(){

    }

    public void build(){
        Model m = ModelFactory.createDefaultModel();
        FileManager.get().readModel( m, WORLD_DATA_FILE );

        // generate some output
        showModelSize( m );
        listTriple( m );
    }

    public void run() {

    }

    protected void showModelSize( Model m ) {
        System.out.println( String.format( "The model contains %d triples", m.size() ) );
    }

    /**
     * Lists all triples
     */
    protected void listTriple( Model m ) {
        StmtIterator i = m.listStatements();

        while (i.hasNext()) {
            Statement curObj = i.next();
            Resource subject = curObj.getSubject();
            Property property = curObj.getPredicate();
            RDFNode obj = curObj.getObject();
            System.out.println( String.format( "Subject: %s , predicate: %s ,  object: %s !", subject.getURI(), property.toString(), obj.toString() ) );
        }
    }
}
