/**
 *   Copyright 2009-2013 Jérôme David &amp; Anthony Delaby Université de Grenoble, François Scharffe Université de Montpellier *   
 *   IndexerStreamRDF.java is part of Melinda.
 *
 *   Melinda is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Melinda is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Melinda; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package keyfinder;

import java.util.Set;

import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inrialpes.exmo.rdfkeys.index.Index;

public class IndexerStreamRDF implements StreamRDF
{
    private final static Logger LOG = LoggerFactory.getLogger(IndexerStreamRDF.class);
	private Index index ;
	private Set<String> consideredProperties;
	
	/**
	 * 
	 * @param index the object in which triple will be indexed
	 * @param properties only properties specified in this collection will be considered
	 */
	public IndexerStreamRDF(Index index, Set<String> properties)
	{
		this.index = index ;
		consideredProperties=properties;
	}
	
	public IndexerStreamRDF(Index index)
	{
		this(index,null);
	}

	public void base(String base)
	{
		// TODO : check what to do with that
	}

	public void start()
	{
		// TODO : check if there's anything particular to do here
	}


	public void finish()
	{
	}

	public void prefix(String prefix, String iri)
	{
		// TODO : handle prefixes in indexing process
	}

	public void tuple(Tuple<Node> tuple) {}
	
	public void triple(Triple triple)
	{
		handle(triple.getSubject(), triple.getPredicate(), triple.getObject()) ;
	}

	public void quad(Quad quad)
	{
		handle(quad.getSubject(), quad.getPredicate(), quad.getObject()) ;
	}
	
	private void handle(Node s, Node p, Node o)
	{	
	    String subject = null ;
	    String predicate = p.getURI() ;
	    String object = null ;
		
	    if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#type".equals(predicate) && consideredProperties!=null && !consideredProperties.contains(predicate)) {
		LOG.debug("Property {} skipped",predicate);
		return;
	    }
		
		if(s.isBlank())
			subject = s.getBlankNodeLabel() ;
		else if(s.isURI())
			subject = s.getURI() ;
		
		if(o.isBlank())
			object = o.getBlankNodeLabel() ;
		else if(o.isURI())
			object = o.getURI() ;
		else if(o.isLiteral())
			object = o.getLiteral().toString();//.getLexicalForm() ;
		
		index.addTriple(subject, predicate, object) ;

	}
}
