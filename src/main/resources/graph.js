
function NetworkGraph( div ) {

	const width = 800 ;
    const height = 600 ;	
	var simulation 

	var svg = d3.select( div ).append("svg") 
		.attr("width", '100%')
		.attr("height", '100%')
		.attr("viewBox", "0 0 800 600")

	dragstarted = function(d) {
		if (!d3.event.active) simulation.alphaTarget(0.3).restart();
		d.fx = d.x;
		d.fy = d.y;
	}

	dragged = function(d) {
		d.fx = d3.event.x;
		d.fy = d3.event.y;
	}

	dragended = function(d) {
		if (!d3.event.active) simulation.alphaTarget(0);
		d.fx = null;
		d.fy = null;
	}

	this.processData = function( msg ) {

		const nodeData = [] 
		const linkData = [] 

		const si = responseMessage.eigenvector.map( function(e,i) { return {x:e, i:i } ; } )
		  .sort( function(a, b){return a.x-b.x} ) 
		  .map( function(e) { return e.i ; } )
		  .slice( 0, msg.partition+1 ) 		
		
		for( let i=0 ; i<msg.N ; i++ ) {
			nodeData.push( { id:i, color:(si.indexOf(i)<0 ? "blue" : "red")  } )
		}
		let x=0 
		let y=0
		for( let i=0 ; i<msg.surface.length ; i++ ) {
			if( x!==y && msg.surface[i] !== 0 ) {
				linkData.push( { source: x, target: y } ) 
			}
			x++
			if( x>= msg.N ) {
				x=0
				y++
			}
		}

		const links = svg
			.selectAll("line")
			.data( linkData )
			.enter()
				.append("line") 
					.attr( 'class', 'link' )
					.attr( "stroke", "white" ) 
			;

		const nodes = svg
			.selectAll("circle.node")
			.data( nodeData )
			.enter()
				.append("circle")
					.attr( 'class', 'node' )
					.attr( 'id', function(d) { return d.id } )
					.attr( "r", function(d) { return 12 } ) 
					.attr( "stroke", "none" ) 
					.call(d3.drag()
						.on("start", dragstarted)
						.on("drag", dragged)
						.on("end", dragended)) 
			;


		this.tick = function() {
			nodes
			.attr("cx", function(d) { return d.x; })
			.attr("cy", function(d) { return d.y; })
			.attr("fill", function(d) { return d.color; })			
			;

			links
			.attr("x1", function(d) { return d.source.x; })
			.attr("y1", function(d) { return d.source.y; })
			.attr("x2", function(d) { return d.target.x; })
			.attr("y2", function(d) { return d.target.y; })
			;
		}
		
		simulation = d3.forceSimulation()
			.nodes( nodeData )
			.force( "link", d3.forceLink()
					.strength( 1.5 )
					.links( linkData ) 
				)
			.force( "charge", d3.forceManyBody()
					.strength( -3 ) 
				)
			.force( "center", d3.forceCenter(width/2, height/2) )
			.force( "collide", d3.forceCollide( 20 ) ) 
			// .force( "x", d3.forceX()
			// 		.x( function(d) { 
			// 			return d.fx ; 
			// 		})
			// 		.strength( function(d) { return 1 ; } ) 
			// 	)  
			// .force( "y", d3.forceY()
			// 		.y( function(d) { 
			// 			return d.fy  ; 
			// 		})
			// 		.strength( function(d) { return 1 ; } ) 
			// 	)  
			.on("tick", this.tick ) 
			.on("end", this.tick ) 
	} // end processData()

	this.resize = function() {
	}
}
	
