package com.rc;

import java.io.File;
import java.net.URL;
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
	final Laplace laplace ;
	final Graph graph ;
	
	final int N = 250 ;
	{
		gson = new Gson();
		random = new Random() ;	
		laplace = new Laplace( N ) ;
		graph = new Graph(N) ;
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
			log.info( "REST call to getData({})", eigenvalueIndex ) ;
			
			double eigenvectors[] = new double[N*N] ;
			rsp.type( "application/json" );	
			rsp.header("expires", "0" ) ;
			rsp.header("cache-control", "no-cache" ) ;
						
			ResponseMessage responseMessage = new ResponseMessage() ;
			
					
//			responseMessage.eigenvectors = new double[N*10] ;
//			responseMessage.N = N ;
//			responseMessage.surface = laplace.solve( 50 ) ;
//			responseMessage.eigenvalues = laplace.eigenValues( eigenvectors ) ;

			responseMessage.eigenvectors = new double[N*10] ;
			responseMessage.N = N ;
			responseMessage.surface = graph.getLaplacian() ;
			responseMessage.eigenvalues = graph.eigenValues( eigenvectors ) ;

			int st = eigenvalueIndex - 5 ;
			if( st<0 ) st = 0 ;
			if( st>=(N-10) ) st = N - 11 ;  

			System.arraycopy(eigenvectors, st*N, responseMessage.eigenvectors, 0, responseMessage.eigenvectors.length );
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
		public int N ;
		int surface[] ;
		double eigenvalues[] ;
		double eigenvectors[] ;
	}
}
