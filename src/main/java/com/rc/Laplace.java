package com.rc;

import java.util.Arrays;
import java.util.Random;

import org.netlib.lapack.Dsygv;

public class Laplace {

	final Random random ;

	double left[] ;
	double right[] ;
	double top[] ;
	double bottom[] ;

	private final double[] V ;
	private final int N ;


	public Laplace( int N ) {
		random = new Random( 6748 ) ;

		left	= new double[N] ;
		right	= new double[N] ;
		top		= new double[N] ;
		bottom 	= new double[N] ;
		
		V = new double[N*N];
		this.N = N ;

		for (int r = 0; r < N; r++)
			for (int c = 0; c < N; c++)
				V[c*N + r] = random.nextGaussian() * 5.0 ;

		
		for (int i = 0; i < N; i++) {
			left[i]		=  Math.sin( i * Math.PI / ( N ) ) ; 
			right[i]	=  - Math.sin( i * Math.PI / (  N ) ) ; 
			top[i]		=  -Math.sin( i * Math.PI / (  N ) ) ; 
			bottom[i]	=  Math.sin( i * Math.PI / (  N ) ) ; 
		}
	}

	protected double get( int c, int r ) {
		if( r<0 && c<0 ) return top[0] + left[0] / 2.0 ;
		if( r<0 && c>=N ) return top[N-1] + right[0] / 2.0 ;
		if( r>=N && c<0 ) return bottom[0] + left[0] / 2.0 ;
		if( r>=N && c>=N ) return bottom[N-1] + right[N-1] / 2.0 ;
		
		if( r<0 ) return top[c] ;
		if( r>=N ) return bottom[c] ;
		if( c<0 ) return left[r] ;
		if( c>=N ) return right[r] ;
		
		return V[c*N + r ] ;
	}
	
	public synchronized double[] solve( final int iterations ) {

		double buf1[] = new double [N] ;
		double buf2[] = new double [N] ;

		for( int n=0 ; n<iterations ; n++ ) {
			
			for( int r=0 ; r<N ; r++ ) {				
				for( int c=0 ; c<N ; c++ ) {					
					buf1[c] = 0.25 * ( get( c-1, r ) + get( c+1, r ) + get( c, r-1 ) + get( c, r+1 ) ) ;
				}

				if( r>0 ) {
					for( int c=0 ; c<N ; c++ ) {
						V[c*N + r-1] = buf2[c] ;
					}
				}

				double tmp[] = buf2 ;
				buf2 = buf1 ;
				buf1 = tmp ;
			}

			for( int c=0 ; c<N ; c++ ) {
				V[c*N + N-1] = buf2[c] ;
			}

		}	    
		return V ;
	}

	public double[] eigenValues() {
		return eigenValues( null ) ;
	} 

	public synchronized double[] eigenValues( double[] v ) {

		int itype = 1;
		
		String jobz = "V" ;
		String uplo = "U" ;

		double a[] = Arrays.copyOf( V, V.length ) ;
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

	public int getN() {
		return N;
	}

}


