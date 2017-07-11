# SpectralPartioning
Spectral Partitioning of graphs

A sample of spectral partitioning  see (my refs.)

https://en.wikipedia.org/wiki/Spectral_clustering

https://www.cs.purdue.edu/homes/dgleich/demos/matlab/spectral/spectral.html

https://devblogs.nvidia.com/parallelforall/fast-spectral-graph-partitioning-gpus/

https://csustan.csustan.edu/~tom/Clustering/GraphLaplacian-tutorial.pdf

http://math.mit.edu/~kelner/Publications/Docs/LowGenusJournal.pdf



## Instructions (linux)

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
a plot of the eigenvector is printed
click on here to sort the eigenvector components

### bottom-right
the laplacian matrix plotted in color. It will be sorted according to the
sort of the eigenvectors. Clusters can be seen using the new sort order.

### Hints
The clusters are seen at the Fiedler value ( &labmda; <sub>1</sub> )
## todo
Make this recursive to spot clusters repeatedly.
 
