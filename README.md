# SpectralPartioning
Spectral Partitioning of graphs

A sample of spectral partitioning  ( see also [https://en.wikipedia.org/wiki/Spectral_clustering](spectral clustering) ] )

## Instructions

* git clone  https://github.com/rcorbish/SpectralPartioning.git
* cd SpectralPartioning
* gradle build
* gradle copyDependencies
* ./run/sh
* open browser to http://localhost:8111

## Screens

### top-left
This is the raw discrete laplacian matrix for the graph

### top-right
The eigenvalues for the laplacian matrix
click on here to select which of the eigenvectors/eigenvalues to inspect

### bottom-left
a plot of the eigen vectors, the 10 eigenvectors around the chosen eigenvalue are printed
click on here to sort the eigenvector components

### bottom-right
the laplacian matrix plotted in color. It will be sorted according to the
sort of the eigenvectors. Clusters can be seen using the new sort order.

### Hints
The clusters are seen at the smallest eigenvalues

## todo
Make this recursive to spot clusters repeatedly.
 
