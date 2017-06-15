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
	
	public Monitor( Path path ) throws IOException {	
		gson = new Gson();
		random = new Random() ;	
		graph = Graph.create( path ) ;
	}
	public Monitor() {	
		gson = new Gson();
		random = new Random() ;	
		graph = Graph.random( 200 ) ;
	}
	
	
	public void start() {
		try {			
			spark.Spark.port( 8111 ) ;
			URL mainPage = getClass().getClassLoader().getResource( "Client.html" ) ;
			File path = new File( mainPage.getPath() ) ;
			spark.Spark.staticFiles.externalLocation( path.getParent() ) ;
			spark.Spark.get( "/data/:" + INDEX_PARAM, this::getData, gson::toJson ) ;
			spark.Spark.awaitInitialization() ;
		} catch( Exception ohohChongo ) {
			log.error( "Server start failure.", ohohChongo );
		}
	}

	/**
	 * get 1 slice of compressed data, with random shear
	 * 
	 */
	public Object getData(Request req, Response rsp) {
		Object rc = null ;
		try {
			String tmp = java.net.URLDecoder.decode( req.params( INDEX_PARAM ), "UTF-8" ) ;
			int eigenvalueIndex = Integer.parseInt(tmp) ; 
			log.debug( "REST call to getData({})", eigenvalueIndex ) ;
			
			rsp.type( "application/json" );	
			rsp.header("expires", "0" ) ;
			rsp.header("cache-control", "no-cache" ) ;
						
			ResponseMessage responseMessage = new ResponseMessage() ;

			int N = graph.getN() ;
			double eigenvectors[] = new double[N*N] ;
			
			if( eigenvalueIndex < 0 ) eigenvalueIndex = 0 ;
			if( eigenvalueIndex >= N ) eigenvalueIndex = N-1 ;

			responseMessage.N = N ;
			responseMessage.surface = graph.getAdjacency() ;
			responseMessage.eigenvalues = graph.eigenValues( eigenvectors ) ;
			responseMessage.eigenvector = new double[N] ;
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
		double surface[] ;
		double eigenvalues[] ;
		double eigenvector[] ;
	}
}
