package com.rc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements AutoCloseable {

	private static Logger log = LoggerFactory.getLogger(Main.class);

	final private Monitor m ; 
	
	public Main() {
		m = new Monitor() ;
	}

	public static void main(String[] args) {
		try {
			Main self = new Main() ;
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
