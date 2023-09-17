package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge> grafo;
	private Map<String, Business> idMap;
	private List<Business> vertici;
	
	public Model() {
		this.dao = new YelpDao();
		this.idMap = new HashMap<String, Business>();
	}
	
	
	
	public void creaGrafo(String citta, int anno) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//riempio l'identity map con i vertici
		this.vertici = this.dao.getVertici(citta, anno);
		for(Business b : vertici) {
			this.idMap.put(b.getBusinessId(), b);
		}
				
		Graphs.addAllVertices(this.grafo, vertici);
		
		List<Arco> archi = dao.getArchi(citta, anno) ;
		for(Arco arco : archi) {
			Graphs.addEdge(this.grafo,
					this.idMap.get(arco.getbId1()),
					this.idMap.get(arco.getbId2()), 
					arco.getPeso()) ;
		}
				
		System.out.println("Grafo creato con " + this.grafo.vertexSet().size() + " vertici e " + this.grafo.edgeSet().size() + " archi");
	}
	
	
	
	//metodo per creare il bilancio
	public Business getBilancio() {
		
		double max = 0.0 ;
		Business result = null ;
		
		for(Business b: this.grafo.vertexSet()) {
			double val = 0.0 ;
			for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b))
				val += this.grafo.getEdgeWeight(e) ;
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b))
				val -= this.grafo.getEdgeWeight(e) ;
			
			if(val>max) {
				max = val ;
				result = b ;
			}
		}
		return result; 	
	}
	
	
	
	
	//metodo che mi da una lista di vertici
	public List<Business> getVertici(String citta, int anno){
		return this.dao.getVertici(citta, anno);
	}
	
	
	
	//metodo per le citta
	public List<String> getCitta(){
		return this.dao.getCitta();
	}
	
	
	
	//metodo che mi restituisce il numero di vertici
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
			
	//metodo che mi restituisce il numero di archi
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
}
