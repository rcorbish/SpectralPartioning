package com.rc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.netlib.lapack.Dsygv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Graph {
	final static Logger log = LoggerFactory.getLogger( Graph.class ) ;

	private double a[] = null ;
	private double w[] = null ;

	private final int N ;
	
	private final double adjacency[] ;
	private final double degree[] ;


	public static Graph random( int size ) {
		
		log.info( "Creating random Graph of {} x {}", size, size );
		
		final Random random = new Random( 783 ) ;

		List<Integer> ixt = new ArrayList<>() ;
		for( int i=0 ; i<size ; i++ ) ixt.add(i) ;
		Collections.shuffle(ixt);
		int ix[] = new int[size] ;
		for( int i=0 ; i<size ; i++ ) ix[i] = ixt.get(i) ;

		int E = size * 3;

		List<Edge> edges = new ArrayList<>() ;
		int group1 = 0 ;
		int group2 = size / 3  ;
		int group3 = 2 * size / 3 ;
		int nodeIndex = 0 ;
		for (int i = 0; i < E; i++, nodeIndex++ ) {
			if( nodeIndex>=size ) nodeIndex=0 ;
			int g = nodeIndex > group3 ? group3 : nodeIndex > group2 ? group2 : group1 ;

			Edge edge = new Edge() ;
			edge.from = ix[nodeIndex] ;

			float f = random.nextFloat() ; 

			do {
				if( f < 0.25f ) {
					edge.to = ix[ random.nextInt( size ) ]  ;
				} else  {
					edge.to = ix[ random.nextInt( size/3 ) + g ]  ;
				}
			} while( edge.to == edge.from ) ;
			edges.add( edge ) ;
		}

		log.info( "Created {} edges",  size );

		Graph rc = new Graph( size ) ;

		for( Edge edge : edges ) rc.addEdge(edge);
		return rc ;		
	}


	public static Graph create( Path path, int ... indices ) throws IOException {
		CsvReader csvr = new CsvReader( indices ) ;

		List<Edge> parsedEdges = csvr.parse( path );
		Graph rc = new Graph( csvr.getNumNodes() ) ;
		for( Edge edge : parsedEdges ) rc.addEdge(edge);
		return rc ;		
	}

	public void addEdge( Edge edge ) {
		// build D matrix
			degree[edge.from]+= edge.weight  ; 
			degree[edge.to]+= edge.weight ;

		// build A matrix
			int ix1 = edge.from * N + edge.to ;
			int ix2 = edge.to * N + edge.from ;
			adjacency[ix1] ++ ; 
			adjacency[ix2] ++ ; 		
	}

	public Graph( int N ) {
		
		this.N = N ;
		adjacency 		= new double[N*N] ;
		degree 	= new double[N] ;
	}

	public double[] getLaplacian() {
		double [] laplacian = new double[N*N] ;
		// build L matrix
		for( int i=0 ; i<laplacian.length ; i++ ) {
			laplacian[i] = -adjacency[i] ; 
		}
		for( int i=0 ; i<degree.length ; i++ ) {
			laplacian[i*(N+1)] = degree[i] ; 
		}
		log.info( "Created Laplacian" );

		return laplacian ;
	}
	public double[] getAdjacency() {
		return adjacency ;
	}
	
	public double[] eigenValues() {
		return eigenValues( null ) ;
	} 

	public int getN() {
		return N ;
	}

	public synchronized double[] eigenValues( double[] v ) {

		if( a == null ) {
			log.info( "Calculating eigenvectors" ) ;
			
			int itype = 2;
			
			String jobz = "V" ;
			String uplo = "U" ;
	
			a = getLaplacian() ;
			
//			a = new double[ getAdjacency().length ] ;
//			System.arraycopy( getAdjacency(), 0, a, 0, a.length ) ; 
			int lda = N ;
			double I[] = new double[N*N] ;
			
			for( int i=0 ; i<N ; i++ ) I[i*(N+1)] = 1.0 ;	// make identity matrix
			
			int ldb = N;
			w = new double[N];
	
	
			double tmp[] = new double[1] ;
			
			org.netlib.util.intW info = new org.netlib.util.intW(0);
	
			// calc optimum workspace size
			int lwork = -1 ;
			Dsygv.dsygv(itype, jobz, uplo, N, a, 0, lda, I, 0, 
					ldb, w, 0, tmp, 0, -1, info);
	
			assert( info.val == 0 ) ;
			
			lwork = (int) tmp[0] ;
			double []work = new double[lwork];
	
			Dsygv.dsygv(itype, jobz, uplo, N, a, 0, lda, I, 0, 
					ldb, w, 0, work, 0, lwork, info);
	
			assert( info.val == 0 ) ;
			log.info( "Eigenvectors calculate for {} x {}", N, N ) ;
		}
		
		if( v != null ) {
			System.arraycopy( a,0, v,0, a.length ) ;
		}
		return w ;
	}

	public int eigenvectorPartition( double ev[] ) {
		double sev[] = new double[ev.length] ;
		System.arraycopy(ev, 0, sev, 0, sev.length );
		Arrays.sort( sev ) ;

		int rc = 1 ;
		double mxg = 0.0 ;
		double mx = sev[0] ;
		double mn = mx ;
		for( int i=1 ; i<sev.length ; i++ ) {
			double grad = sev[i] - sev[i-1] ;
			if( grad>mxg ) {
				mxg = grad ;
				rc = i ;
				if( mxg > 0.025 ) break ;
			}
			//log.info( "{}\t{}", grad, mxg ) ;
		}
		log.info( "Partition = {}, dy/dx = {}", rc, mxg ) ;	
		return rc ;
	}
}


class Edge implements Comparable<Edge> {
	int from ;
	int to ;
	double weight = 1.0 ;
	public int compareTo( Edge o ) {
		return from==o.from ? to - o.to : from - o.from ;
	}
}

