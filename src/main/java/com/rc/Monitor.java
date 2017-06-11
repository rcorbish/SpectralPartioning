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
	
	final static Logger logger = LoggerFactory.getLogger( Monitor.class ) ;

	final Random random ;
	final Gson gson ;
	final Laplace laplace ;
	
	{
		gson = new Gson();
		random = new Random() ;	
		laplace = new Laplace( 200 ) ;
	}
	
	
	public void start() {
		try {			
			spark.Spark.port( 8111 ) ;
			URL mainPage = getClass().getClassLoader().getResource( "Client.html" ) ;
			File path = new File( mainPage.getPath() ) ;
			spark.Spark.staticFiles.externalLocation( path.getParent() ) ;
			spark.Spark.get( "/data", this::getData, gson::toJson ) ;
			spark.Spark.awaitInitialization() ;
		} catch( Exception ohohChongo ) {
			logger.error( "Server start failure.", ohohChongo );
		}
	}

	/**
	 * get 1 slice of compressed data, with random shear
	 * 
	 */
	public Object getData(Request req, Response rsp) {
		Object rc = null ;
		try {
			rsp.type( "application/json" );	
			rsp.header("expires", "0" ) ;
			rsp.header("cache-control", "no-cache" ) ;
						
			ResponseMessage responseMessage = new ResponseMessage() ;
			
			responseMessage.eigenvectors = new double[laplace.getN()*laplace.getN()] ;
			responseMessage.N = laplace.getN() ;
			responseMessage.surface = laplace.solve( 50 ) ;
			responseMessage.eigenvalues = laplace.eigenValues( responseMessage.eigenvectors ) ;
			
			rc = responseMessage ; 
		} catch ( Throwable t ) {
			logger.warn( "Error processing getItem request", t ) ;
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
		double surface[] ;
		double eigenvalues[] ;
		double eigenvectors[] ;
	}
}
