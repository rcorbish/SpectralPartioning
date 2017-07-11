package com.rc;

import java.io.IOException;
<<<<<<< HEAD
import java.nio.file.Path;
=======
>>>>>>> 1dbdb6d91fcbad59045f27c7f5913ba30b1df7a0
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements AutoCloseable {

	private static Logger log = LoggerFactory.getLogger(Main.class);

	final private Monitor m ; 
	
	public Main( String fileName ) throws IOException {
		Path path = fileName == null ? null : Paths.get(fileName) ;
		if( path == null ) {
			m = new Monitor() ;
		} else {
			m = new Monitor(path) ;
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
