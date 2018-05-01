


var clientWidth  ;
var clientHeight ;

const viewWidth = 800 ;
const viewHeight = 600 ;

var links ;
var nodes ;


function init( div ) {
}



function processGraph( canvas3d, responseMessage ) {

	while (canvas3d.hasChildNodes()) {
		canvas3d.removeChild( canvas3d.lastChild )
	}
	const svg = d3.select( canvas3d ).append("svg") 
		.attr("width", '100%')
		.attr("height", '100%')
		.attr("viewBox", "0 0 800 600") 
	
  	const svgg = svg.append("g").attr("class", "network") 

	const color = d3.scaleOrdinal(d3.schemeCategory20);

	var linkData = [] 
	var nodeData = []
	
	for( var i=0 ; i<responseMessage.N ; i++ ) {
		nodeData.push( { id: i, color: (i<responseMessage.partition ? "blue" : "green") } )
	}

	var x = 0 
	var y = 0 
	for( var i=0 ; i<responseMessage.surface.length ; i++ ) {
		x++ 
		if( x>=responseMessage.N ) {
			x=0
			y++
		}
		if( x!==y && responseMessage.surface[i] != 0 ) {
			linkData.push( { source: x, target: y } )
		}
	}
	
	const charge = -10
	const tension = 2
	const radius = 20

  	links = svgg
	    .selectAll( "line" )
    	.data( linkData )
    	.enter().append( "line" ) 
    ;

  	nodes = svgg
    	.selectAll("circle")
    	.data( nodeData )
    	.enter().append("circle")
      		.attr("r", function(d) { return (radius||50)/3 } ) 
      		.attr("fill", function(d) { return color(d.color); } )
  	;

  	function dragsubject() {
  		var svgElem = d3.event.sourceEvent.target.ownerSVGElement
  		var pt = svgElem.createSVGPoint()

  		pt.x = d3.event.x;
  		pt.y = d3.event.y;

  		var p2 = pt.matrixTransform( svgElem.getScreenCTM().inverse() ) 
  		
  		//console.log( "Mouse", d3.event.x, d3.event.y, " Node", nodeData[0].x, nodeData[0].y, "Xform", p2.x, p2.y )
  		return simulation.find(p2.x, p2.y  );
  	}

  	function dragstarted() {
  	  if (!d3.event.active) simulation.alphaTarget(0.3).restart();
  	  d3.event.subject.fx = d3.event.subject.x;
  	  d3.event.subject.fy = d3.event.subject.y;
  	}

  	function dragged() {
  	  d3.event.subject.fx = d3.event.x;
  	  d3.event.subject.fy = d3.event.y;
  	}

  	function dragended() {
  	  if (!d3.event.active) simulation.alphaTarget(0);
  	  d3.event.subject.fx = null;
  	  d3.event.subject.fy = null;
  	}
      
  	function tick() {
    	nodes
    	.attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; })
    	;
    
    	links
        .attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; })
    	;
	}


	const simulation = d3.forceSimulation()
		.nodes( nodeData ) 	
		.force( "link", d3.forceLink()
				.id( function(n) { return n.id ; } )
				.strength( function(l) { return tension||1 ; }) 
				.distance( function(l) { return 2 ; } )
				.links( linkData ) 
			)
		.force( "charge", d3.forceManyBody()
				.strength( -(charge||50) ) 
			)
		.force( "center", d3.forceCenter(viewWidth/2, viewHeight/2) ) 			
		.force( "collide", d3.forceCollide( radius||20 ) ) 
		.force( "y", d3.forceY( function(d) { return 0 ; } ).strength( 0.03 ) ) 
		.force( "x", d3.forceX( function(d) { return d.xPreferred || 0 ; } ).strength( function(d) { return d.xPreferred ? 0.1 : 0.0 ; } ) )			
		.on("tick", tick ) 
		.on("end", tick ) 
		;
	
	d3.select( canvas3d )
		.call( d3.drag()
	        .container(canvas3d)
	        .subject(dragsubject)
	        .on("start", dragstarted)
	        .on("drag", dragged)
	        .on("end", dragended)
	    ) ;
	
//	simulation.stop()
//	for( var i=0 ; i<100 ; i++ ) {
//		simulation.tick()
//	}
}


function resizeGraph( canvas3d ) {
	clientWidth = window.innerWidth / 2 ;
	clientHeight = window.innerHeight / 2 ;
	
	canvas3d.width = clientWidth ;
	canvas3d.height = clientHeight ;
	
	canvas2dVals.width = clientWidth ;
	canvas2dVals.height = clientHeight ;

	canvas2dVect.width = clientWidth ;
	canvas2dVect.height = clientHeight ;

	canvas2dClus.width = clientWidth ;
	canvas2dClus.height = clientHeight ;

	getData();
}




