

const MESH_SIZE = 600 ;


var scene;
var camera;
var controls;
var renderer;
var mesh;


const material = new THREE.MeshStandardMaterial({
	side : THREE.DoubleSide,
	opacity : 1.0,
	color : 0xffffff,
	wireframe : false,
	metalness : 0.85,
	roughness : 0.2
});

function processGraph( canvas3d, responseMessage ) {

		var mn = responseMessage.surface[0] ;
		var mx = mn ;
		for( let i=1 ; i<responseMessage.surface.length ; i++ ) {
			mn = Math.min( mn, responseMessage.surface[i] ) 
			mx = Math.max( mx, responseMessage.surface[i] ) 
		}
		const range = 0.5 * (mx - mn );
		for( let i=0 ; i<responseMessage.surface.length ; i++ ) {
			responseMessage.surface[i] = ( responseMessage.surface[i] - mn ) / range  
		}

		let geometry = new THREE.ParametricBufferGeometry(
				graphFunction, MESH_SIZE, MESH_SIZE )

		if( !mesh ) {
			mesh = new THREE.Mesh(geometry, material)
			scene.add( mesh ) 
		} else {
			mesh.geometry = geometry 
		}

		
		canvas3d.width = canvas3d.width 

		render() 		
}

var clientWidth  ;
var clientHeight ;

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

	camera.aspect = clientWidth / clientHeight;
	camera.updateProjectionMatrix();

	renderer.setSize(clientWidth, clientHeight);
	getData();
}


function graphFunction(u, v) {
	if( !responseMessage ) return new THREE.Vector3(0,0,0);
	
	let c = Math.floor( u * (responseMessage.N-1) ) ;
	let r = Math.floor( v * (responseMessage.N-1) ) ;
	
	let y = clientHeight / 4
			* responseMessage.surface[ c*responseMessage.N + r ] 
			- clientHeight / 8;

	let z = v * clientWidth - clientWidth / 2;
	let x = u * clientWidth - clientWidth / 2;
	
	return new THREE.Vector3(x, y, z);
}



function init( canvas3d ) {

	scene = new THREE.Scene();

	camera = new THREE.PerspectiveCamera(75, window.innerWidth
			/ window.innerHeight, 1, 10000);
	camera.position.z = 1200;

	const light1 = new THREE.AmbientLight(0xffffff, .2);
	scene.add(light1);

	const light2 = new THREE.DirectionalLight(0xff0000, 1.0);
	light2.position.set(1000, 1700, 0);
	scene.add(light2);

	const light3 = new THREE.DirectionalLight(0x0000ff, 1.0);
	light3.position.set(-1000, 1700, 0);
	scene.add(light3);

	const light4 = new THREE.DirectionalLight(0xffff00, 1.0);
	light4.position.set(0, -1000, 0);
	scene.add(light4);

	renderer = new THREE.WebGLRenderer();
	renderer.setSize(window.innerWidth, window.innerHeight);

	canvas3d.appendChild(	renderer.domElement ) ;

	controls = new THREE.OrbitControls(camera, renderer.domElement);
	controls.addEventListener('change', render);
}

function animate() {
	requestAnimationFrame(animate);
	controls.update();
}

function render() {
	renderer.render(scene, camera);
}
