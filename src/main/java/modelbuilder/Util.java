package modelbuilder;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

class Util {

    static String transformSubject(Model model, Statement fact){
        return model.getNsURIPrefix(fact.getSubject().getNameSpace()) + ":" + fact.getSubject().getLocalName();
    }

    static String transformPredicate(Model model, Statement fact){
        if(fact.getPredicate().getURI().contains("rdf-syntax-ns#type")){
            return "a";
        } else {
            return model.getNsURIPrefix(fact.getPredicate().getNameSpace()) + ":" + fact.getPredicate().getLocalName();
        }
    }


    static String transformObject(Model model, Statement fact){
        String obje = null;
        if(fact.getObject().isLiteral()){
            Literal literal = fact.getObject().asLiteral();
            if(!literal.getLanguage().equals("")){
                obje = "\"" + literal.getLexicalForm() + "\"@" + literal.getLanguage();
            } else if (literal.getDatatype() != null){
                String[] datatype = literal.getDatatype().getURI().split("#");
                obje = "\"" + literal.getLexicalForm() + "\"^^" + model.getNsURIPrefix(datatype[0] + "#") + ":" + datatype[1];
            }
        } else if (fact.getObject().isResource()) {
            obje = model.getNsURIPrefix(fact.getObject().asResource().getNameSpace()) + ":" + fact.getObject().asResource().getLocalName();
        }
        return obje;
    }
}
