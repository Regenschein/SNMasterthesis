package modelbuilder;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

class Util {

    static String transformSubject(Model model, Statement fact){
        String ns = fact.getSubject().getNameSpace();
        String s = model.getNsURIPrefix(fact.getSubject().getNameSpace()) + ":" + fact.getSubject().getLocalName();
        if (model.getNsURIPrefix(fact.getSubject().getNameSpace()) == null){
            return fact.getSubject().toString();
        } else
            return model.getNsURIPrefix(fact.getSubject().getNameSpace()) + ":" + fact.getSubject().getLocalName();
    }

    static String transformPredicate(Model model, Statement fact, String classname){
        if(fact.getPredicate().getURI().contains("rdf-syntax-ns#type")){
            return "a";
        } else {
            return classname + "%&%" + model.getNsURIPrefix(fact.getPredicate().getNameSpace()) + ":" + fact.getPredicate().getLocalName();
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
            if (model.getNsURIPrefix(fact.getObject().asResource().getNameSpace()) == null){
                return fact.getObject().asResource().toString();
            } else
                obje = model.getNsURIPrefix(fact.getObject().asResource().getNameSpace()) + ":" + fact.getObject().asResource().getLocalName();
        }
        return obje;
    }
}
