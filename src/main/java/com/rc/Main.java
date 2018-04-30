package com.rc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements AutoCloseable {

	private static Logger log = LoggerFactory.getLogger(Main.class);

	final private Monitor m ; 
	
	public Main( String fileName ) throws IOException {
		Path path = fileName == null ? null : Paths.get(fileName) ;
		if( path == null ) {
			m = new Monitor( 20 ) ;  // # nodes
		} else {
			int ix[] = new int[26] ;
			for( int i=0 ; i<ix.length ; i++ ) ix[i] = i + 1 ;
			m = new Monitor(path, ix ) ;
		}
	}

	public static void main(String[] args) {
		try {
			Main self = new Main( args.length>0 ? args[0] : null  ) ;
			self.start() ;
		} catch( Throwable t ) {
			t.printStackTrace() ; 
		}
	}
	
	public void start() {
		m.start() ;
	}
	
	@Override
	public void close() {
		m.close();
	}
}
