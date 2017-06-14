package com.rc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestCsvReader {

	static File testData ;
	@BeforeClass
	static public void setup() throws IOException {
		testData = File.createTempFile("test-", ".graph") ;
		testData.deleteOnExit();
		
		FileWriter fw = new FileWriter(testData) ;
		
		fw.write( "STATE,CITY,POPULATION,AIRPORTS\n" ) ;
		fw.write( "CA,LOS ANGELES,3000000,6\n" ) ;
		fw.write( "CA,SAN FRANCISCO,1500000,2\n" ) ;
		fw.write( "NY,ALBANY,20000,1\n" ) ;
		fw.write( "NY,NEW YORK,7000000,3\n" ) ;
		fw.write( "NJ,NEWARK,400000,1\n" ) ;
		fw.write( "DC,WASHINGTON,1000000,2\n" ) ;
		
		fw.close(); 
	}
	
	@Test
	public void testCsvReader() {
		CsvReader test = new CsvReader( 0,1,2,3 ) ;
		assertNotNull( "Failed to create CsvReader", test );
	}

	@Test
	public void testParse() throws IOException {
		CsvReader test = new CsvReader( 0,1,2,3 ) ;
		assertNotNull( "Failed to create CsvReader", test );
		
		List<Edge> edges = test.parse( testData.toPath() );
		assertNotEquals("Empty edge list", 0, edges.size() ) ;
	}

}
