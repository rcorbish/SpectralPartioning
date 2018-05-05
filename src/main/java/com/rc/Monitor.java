package com.rc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;

/**
 * This handles the web pages. 
 * 
 * We use spark to serve pages. It's simple and easy to configure. 
 * 
 * @author richard
 *
 */
public class Monitor implements AutoCloseable {
	
	final static Logger log = LoggerFactory.getLogger( Monitor.class ) ;

	public final static String INDEX_PARAM = "INDEX" ;
	
	final Random random ;
	final Gson gson ;
	final Graph graph ;
	

	public Monitor( int size, double connectedness ) {	
		gson = new Gson();
		random = new Random() ;	
		graph = Graph.random( size, connectedness ) ;
	}
	
	public Monitor(Path path, int ... indices ) throws IOException {
		gson = new Gson();
		random = new Random() ;	
		graph = Graph.create( path, indices ) ;
	}
	
	
	public void start() {
		try {			
			spark.Spark.port( 8111 ) ;
			URL mainPage = getClass().getClassLoader().getResource( "index.html" ) ;
			File path = new File( mainPage.getPath() ) ;
			spark.Spark.staticFiles.externalLocation( path.getParent() ) ;
			spark.Spark.get( "/data/:" + INDEX_PARAM, this::getData, gson::toJson ) ;
			spark.Spark.awaitInitialization() ;
		} catch( Exception ohohChongo ) {
			log.error( "Server start failure.", ohohChongo );
		}
	}

	/**
	 * 
	 */
	public Object getData(Request req, Response rsp) {
		Object rc = null ;
		try {
			String tmp = java.net.URLDecoder.decode( req.params( INDEX_PARAM ), "UTF-8" ) ;
			int eigenvalueIndex = Integer.parseInt(tmp) ; 
			log.info( "REST call to getData({})", eigenvalueIndex ) ;

			tmp = req.queryParams( "cut" ) ;
			int cut[] = null ;
			if( tmp != null ) {
				try {
					String cuts[] = tmp.split("," ) ;
					cut = new int[cuts.length] ;
					for( int i=0 ; i<cut.length ; i++ ) {
						cut[i] = Integer.parseInt( cuts[i] ) ;
					}
					log.info( "Cutting graph at {}", cut ) ;
				} catch( Throwable t ) {
					log.error( "Cannot parse {} to integer for cut param", tmp ) ;
				}
			}

			rsp.type( "application/json" );	
			rsp.header("expires", "0" ) ;
			rsp.header("cache-control", "no-cache" ) ;
						
			ResponseMessage responseMessage = new ResponseMessage() ;

			int N = graph.getN() ;
			double eigenvectors[] = new double[N*N] ;
			
			responseMessage.N = N ;
			responseMessage.surface = graph.getAdjacency() ;
			responseMessage.eigenvalues = graph.eigenValues( eigenvectors ) ;
			responseMessage.eigenvector = new double[N] ;

			responseMessage.numGroups = 0 ;
			for( int i=0 ; i<responseMessage.eigenvalues.length ; i++ ) {
				if( Math.abs( responseMessage.eigenvalues[i] ) > 1e-8 ) {
					break ;
				}
				responseMessage.numGroups++ ;	
			}

			if( eigenvalueIndex < 0 ) eigenvalueIndex = responseMessage.numGroups ;
			if( eigenvalueIndex >= N ) eigenvalueIndex = responseMessage.numGroups ;

			System.arraycopy(eigenvectors, eigenvalueIndex*N, responseMessage.eigenvector, 0, responseMessage.eigenvector.length );
			responseMessage.partition = graph.eigenvectorPartition( responseMessage.eigenvector ) ;

			rc = responseMessage ; 
		} catch ( Throwable t ) {
			log.warn( "Error processing getItem request", t ) ;
			rsp.status( 400 ) ;	
		}
		return rc ;
	}


	@Override
	public void close() {
		spark.Spark.stop() ;
	}

	static class ResponseMessage {
		int N ;
		int partition ;
		int numGroups ;
		double surface[] ;
		double eigenvalues[] ;
		double eigenvector[] ;
	}
}
