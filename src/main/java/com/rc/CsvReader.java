package com.rc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvReader {

	final static Logger log = LoggerFactory.getLogger( CsvReader.class ) ;

	private final int columnIndices[] ;
	
	private final List< Set<String>> nodes ;
	private String nodeTexts[][] ;
	private final int nodeIndexStart[] ;
	
	public CsvReader( int ... indices ) {
		this.columnIndices = indices ;
		this.nodes = new ArrayList<>() ;
		for( int i=0 ; i<indices.length ; i++ ) {
			this.nodes.add( new HashSet<>() ) ;
		}
		this.nodeIndexStart = new int[indices.length] ; 
		log.info( "Created csv reader with {} columns", indices.length ) ;
	}
	
	public List<Edge> parse( Path path ) throws IOException {
		log.info( "Parsing {} to edges", path ) ;
		
		for( Set<String> nodeText : this.nodes ) nodeText.clear();
		
		Files.lines(path)
			.parallel()
			.map( s -> s.split("\\,") )
			.forEach( this::nodeMapper ) 
			;

		log.info( "Mapped {} columns", nodes.size() ) ;

		nodeIndexStart[0] = 0 ;
		nodeTexts = new String[nodes.size()][] ;
		for( int i=0 ; i<nodes.size() ; i++ ) {
			nodeTexts[i] = nodes.get(i).toArray(new String[0]) ;
			Arrays.sort( nodeTexts[i] ) ;
			if( i>0 ) {
				nodeIndexStart[i] = nodeIndexStart[i-1] + nodeTexts[i].length ;
			}
		}

		log.info( "Sorted column data" ) ;

		List<Edge> edges = Files.lines(path)
			.parallel()
			.map( s -> s.split("\\,") )
			.map( this::edgeMapper )
			.reduce( new ArrayList<Edge>(), (a,b) -> { a.addAll(b) ; return a ; } )
			; 

		log.info( "Created {} edges", edges.size() ) ;
		
		return edges ;
	}

	public int getNumNodes() {
		return nodeIndexStart[ columnIndices.length-1 ] + nodeTexts[columnIndices.length].length ;
	}
	
	protected void nodeMapper( String cols[] ) {
		for( int i=0 ; i<columnIndices.length ; i++ ) {
			int columnIndex = columnIndices[i] ;
			nodes.get(i).add( cols[columnIndex] ) ;
		}
	}

	protected List<Edge> edgeMapper( String cols[] ) {
		
		List<Edge> rc = new ArrayList<>() ;
		
		for( int i=0 ; i<columnIndices.length ; i++ ) {
			int fromColumnIndex = columnIndices[i] ;
			String from = cols[fromColumnIndex] ;
			
			for( int j=i+1 ; j<columnIndices.length ; j++ ) {
				int toColumnIndex = columnIndices[j] ;
				String to = cols[toColumnIndex] ;
								
				Edge edge = new Edge() ;
				edge.from = nodeIndexStart[i] + Arrays.binarySearch( nodeTexts[fromColumnIndex], from ) ;
				edge.to   = nodeIndexStart[j] + Arrays.binarySearch( nodeTexts[toColumnIndex], to ) ;
				
				rc.add( edge ) ;	
			}
		}
		
		return rc ;
	}
}


