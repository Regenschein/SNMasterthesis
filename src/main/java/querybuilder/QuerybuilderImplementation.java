package querybuilder;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class QuerybuilderImplementation implements Querybuilder{


    public void build2() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix world: <" + WORLD_NS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";


        showQuery( m,
                prefix +
                        "select ?citizen where {" +
                        //"    ?citizen a rdfs:Class. " +
                        "    ?citizen rdfs:label \"Erik\"@de. " +
                        //"    ?citizen rdfs:subClassOf <http://schema.org/Person>. " +
                        "}");
    }

    public void build() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix world: <" + WORLD_NS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";

        showQuery( m,
                prefix +
                    "SELECT ?value (count(?value) as ?count) " +
                    "WHERE {" +
                    "    $this $PATH ?value . " +
                    "    $that $PATH ?value . " +
                    "   FILTER (?this != ?that) . " +
                    "}" +
                    "GROUP BY ?value");
    }

    public void buildback() {
        OntModel m = getModel();
        loadData( m );
        String prefix = "prefix world: <" + WORLD_NS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";

        showQuery( m,
                prefix +
                        "SELECT ?value (count(distinct ?value) as ?count) " +
                        "WHERE {" +
                        "    $this $PATH ?value . " +
                        "            FILTER (?value != \"torben\") " +
                        "}" +
                        "GROUP BY ?value");
    }

    protected OntModel getModel() {
        return ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
    }

    protected void loadData( Model m ) {
        //FileManager.get().readModel( m, SOURCE + "pizza.owl.rdf" );
        FileManager.get().readModel( m, SOURCE + "world-0.1.ttl" );
    }

    protected void showQuery( Model m, String q ) {
        Query query = QueryFactory.create( q );
        QueryExecution qexec = QueryExecutionFactory.create( query, m );
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out( results, m );
        }
        finally {
            qexec.close();
        }

    }
}
