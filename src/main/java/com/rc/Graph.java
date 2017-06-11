package com.rc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.netlib.lapack.Dsygv;

public class Graph {

	final Random random ;

	private final int N ;
	private final int edges[][] ;
	
	private final int adjacency[] ;
	private final int connectivity[] ;
	private final int laplacian[] ;


	public Graph( int N ) {
		random = new Random( 783 ) ;
		int E = N * 33_000 ;
		edges = new int[E][2];
		this.N = N ;

		List<Integer> ixt = new ArrayList<>() ;
		for( int i=0 ; i<N ; i++ ) ixt.add(i) ;
		Collections.shuffle(ixt);
		int ix[] = new int[N] ;
		for( int i=0 ; i<N ; i++ ) ix[i] = ixt.get(i) ;
		
		int group1 = 0 ;
		int group2 = N / 3  ;
		int group3 = 2 * N / 3 ;
		
		for (int i = 0; i < E; i++ ) {
			float f = random.nextFloat() ; 
			if( f < 0.05f ) {
				edges[i][0] = ix[ random.nextInt( N/3 ) + group1 ] ;
				edges[i][1] = ix[ random.nextInt( N/3 ) + group2 ] ;				
			} else if( f < 0.1f ) {
				edges[i][0] = ix[ random.nextInt( N/3 ) + group2 ] ;
				edges[i][1] = ix[ random.nextInt( N/3 ) + group3 ] ;				
			} else if( f < 0.5f ) {
				edges[i][0] = ix[ random.nextInt( N/3 ) + group1 ] ;
				do { 
					edges[i][1] = ix[ random.nextInt( N/3 ) + group1 ] ;
				} while( edges[i][1] == edges[i][0] ) ;				
			} else if( f < 0.8f ) {
				edges[i][0] = ix[ random.nextInt( N/3 ) + group2 ] ;
				do { 
					edges[i][1] = ix[ random.nextInt( N/3 ) + group2 ] ;
				} while( edges[i][1] == edges[i][0] ) ;				
			} else {
				edges[i][0] = ix[ random.nextInt( N/3 ) + group3 ] ;
				do { 
					edges[i][1] = ix[ random.nextInt( N/3 ) + group3 ] ;
				} while( edges[i][1] == edges[i][0] ) ;				
			}
		}
		
		adjacency 		= new int[N*N] ;
		connectivity 	= new int[N] ;
		laplacian 		= new int[N*N] ;
		
		// build D matrix
		for( int i=0 ; i<E ; i++ ) {
			connectivity[edges[i][0]]++ ; 
			connectivity[edges[i][1]]++ ; 
		}

		// build A matrix
		for( int i=0 ; i<E ; i++ ) {
			int ix1 = edges[i][0] * N + edges[i][1] ;
			int ix2 = edges[i][1] * N + edges[i][0] ;
			adjacency[ix1]++ ; 
			adjacency[ix2]++ ; 
		}

		// build L matrix
		for( int i=0 ; i<laplacian.length ; i++ ) {
			laplacian[i] = -adjacency[i] ; 
		}
		for( int i=0 ; i<connectivity.length ; i++ ) {
			laplacian[i*(N+1)] = connectivity[i] ; 
		}

	}

	public int[] getLaplacian() {
		return laplacian ;
	}
	
	public double[] eigenValues() {
		return eigenValues( null ) ;
	} 

	public synchronized double[] eigenValues( double[] v ) {

		int itype = 2;
		
		String jobz = "V" ;
		String uplo = "U" ;

		double a[] = new double[laplacian.length] ;
		for( int i=0 ; i<a.length ; i++ ) a[i] = laplacian[i] ;
		
		int lda = N ;
		double I[] = new double[N*N] ;
		
		for( int i=0 ; i<N ; i++ ) I[i*(N+1)] = 1.0 ;	// make identity matrix
		
		int ldb = N;
		double w[] = new double[N];


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

		if( v != null ) {
			System.arraycopy( a,0, v,0, a.length ) ;
		}
		return w ;
	}

}


