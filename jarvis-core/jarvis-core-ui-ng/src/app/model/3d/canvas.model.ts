/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as THREE from 'three';
import * as _ from 'lodash';

import { OrbitControls } from 'three-orbitcontrols-ts';
import { Observable } from 'rxjs/Rx';
import { TreeNode } from 'primeng/primeng';

import { LoggerService } from '../../service/logger.service';

export class Scene {
    protected scene: THREE.Scene;
    protected camera: THREE.PerspectiveCamera;
    private renderer: THREE.WebGLRenderer;
    private loader: THREE.TextureLoader;
    private canvas: HTMLCanvasElement;
    private raycaster: THREE.Raycaster = new THREE.Raycaster();
    private mouse: THREE.Vector2 = new THREE.Vector2();
    protected _logger: LoggerService;
    protected controls: OrbitControls;
    public world: TreeNode[] = [];
    
    /**
     * build a new scene
     * @param name 
     */
    initialize(logger: LoggerService, name: string): void {
        this._logger = logger;
        this.scene = new THREE.Scene();
        this.scene.name = name;
        this.loader = new THREE.TextureLoader();
        this.canvas = <HTMLCanvasElement> document.getElementById(name);
        this.renderer = new THREE.WebGLRenderer({ canvas: this.canvas, antialias: true });
        this.canvas.width = this.canvas.clientWidth;
        this.canvas.height = this.canvas.clientHeight;
        this.renderer.setViewport(0, 0, this.canvas.clientWidth, this.canvas.clientHeight);
        this.renderer.setSize(this.canvas.clientWidth, this.canvas.clientHeight);
        logger.info("Renderer", 0, 0, this.canvas.clientWidth, this.canvas.clientHeight);

        /**
         * add default camera
         */
        this.camera = new THREE.PerspectiveCamera(45, this.canvas.width / this.canvas.height, 1, 1000);
        this.camera.name = "default";
        this.camera.position.z = 50;

        // FLOOR
        this.loader.load('assets/checkerboard.jpg', (floorTexture) => {
            floorTexture.wrapS = floorTexture.wrapT = THREE.RepeatWrapping;
            floorTexture.repeat.set(10, 10);
            var floorMaterial = new THREE.MeshBasicMaterial({ map: floorTexture, side: THREE.DoubleSide, opacity: 0.5, transparent: true });
            var floorGeometry = new THREE.PlaneGeometry(1000, 1000, 10, 10);
            var floor = new THREE.Mesh(floorGeometry, floorMaterial);
            floor.position.y = -0.5;
            floor.rotation.x = Math.PI / 2;
            floor.name = "Checkerboard Floor";
            this.scene.add(floor);
        });

        // LIGHT
        var light = new THREE.PointLight(0xffffff);
        light.position.set(0, 250, 0);
        this.scene.add(light);

        // CONTROLS
        // Cf. https://github.com/nicolaspanel/three-orbitcontrols-ts/blob/master/src/index.ts
        this.controls = new OrbitControls(this.camera, this.renderer.domElement);
        this.controls.enableDamping = true;
        this.controls.dampingFactor = 0.25;
        this.controls.enableZoom = false;
        this.controls.enablePan = false;
        this.controls.enableKeys = false;
        this.controls.enableRotate = true;

        // Refresh every 20 ms
        Observable.interval(20).subscribe(x => {
            this.render();
        });
    }

    /**
     * retrieve elements
     */
    public root(): THREE.Scene {
        return this.scene;
    }

    /**
     * retrieve elements
     */
    public children(): THREE.Object3D[] {
        return this.scene.children;
    }

    /**
     * load a resource
     * @param resource load resource
     */
    public load(resource: string): void {
        // instantiate a loader
        var loader = new THREE.ObjectLoader();
        let that = this;

        // load a resource
        loader.load(
            // resource URL
            resource,

            // pass the loaded data to the onLoad function.
            // Here it is assumed to be an object
            function (obj) {
                //add the loaded object to the scene
                that.scene.add(obj);
            },

            // Function called when download progresses
            function (xhr) {
                console.log((xhr.loaded / xhr.total * 100) + '% loaded');
            },

            // Function called when download errors
            function (xhr) {
                console.error('An error happened');
            }
        );
    }

    /**
     * find interactions
     * @param event 
     */
    public getIntersections(event: any): THREE.Intersection[] {
        // calculate mouse position in normalized device coordinates
        // (-1 to +1) for both components
        this.mouse.x = (event.offsetX / this.getCanvasWidth()) * 2 - 1;
        this.mouse.y = - (event.offsetY / this.getCanvasHeight()) * 2 + 1;

        // update the picking ray with the camera and mouse position
        this.raycaster.setFromCamera(this.mouse, this.getCamera());

        // calculate objects intersecting the picking ray
        return this.raycaster.intersectObjects(this.scene.children);
    }

    /**
     * render this scene
     */
    public render() {
        if (this.renderer) {
            this.renderer.render(this.scene, this.camera);
        }
    }

    /**
     * find any mesh by its id
     * @param objectId 
     */
    public findMeshById(objectId: number): THREE.Mesh {
        return <THREE.Mesh>this.scene.getObjectById(objectId);
    }

   /**
   * dictionnary
   */
  public dictionnary(): void {
    /**
     * retrieve root
     */
    let objects = <TreeNode>{
      "data": {
        "type": "Scene",
        "id": this.scene.id,
        "uuid": this.scene.uuid,
        "name": this.scene.name
      },
      children: []
    };
    this.world.push(objects);

    let camera = <TreeNode>{
      "data": {
        "type": "Camera",
        "id": this.camera.id,
        "uuid": this.camera.uuid,
        "name": this.camera.name
      },
      children: []
    };
    this.world.push(camera);

    /**
     * iterate on mesh/Object3D
     */
    _.each(this.scene.children, (obj) => {
      let mesh: THREE.Object3D = <THREE.Object3D> obj;
      let node = <TreeNode> {
        "data": {
          "id": mesh.id,
          "type": mesh.type,
          "uuid": mesh.uuid,
          "name": mesh.name,
          "datasource": mesh
        },
        "children": []
      };
      _.each(obj.children, (level1Object) => {
        let level1Mesh: THREE.Object3D = <THREE.Object3D> level1Object;
        let level1 = <TreeNode> {
          "data": {
            "id": level1Mesh.id,
            "type": level1Mesh.type,
            "uuid": level1Mesh.uuid,
            "name": level1Mesh.name,
            "datasource": level1Mesh
          },
          "children": []
        };
        node.children.push(level1);
      });
      objects.children.push(node);
      console.log("all", obj);
    });
  }

   /**
     * add a new standard block
     * @param x 
     * @param y 
     * @param z 
     */
    public block(x: number, y: number, z: number, value: string): THREE.Group {
        // create group for this block
        let group = new THREE.Group();

        // create main box
        let geometry = new THREE.BoxBufferGeometry( 10, 10, 1 );
        let material = new THREE.MeshBasicMaterial( {color: 0xffd200} );
        let cube = new THREE.Mesh( geometry, material );
        cube.name = 'Board';
        group.add( cube );

        // text area
        var loader = new THREE.FontLoader();
        
        loader.load( 'assets/fonts/Open Sans_Regular.json', ( font ) => {
            var textMaterial = new THREE.MeshBasicMaterial( {color: 0xff0000} );
            let textGeometry = new THREE.TextGeometry( value, {
                font: font,
                size: 1,
                height: 1,
                curveSegments: 12,
                bevelEnabled: false,
                bevelThickness: 1,
                bevelSize: 1
            } );
            let text = new THREE.Mesh( textGeometry, textMaterial );
            text.name = 'Text area';
            cube.geometry.computeBoundingBox();
            let cubeBox = cube.geometry.boundingBox;
            text.geometry.computeBoundingBox();
            let textBox = text.geometry.boundingBox;
            text.position.x = cubeBox.getCenter().x - (textBox.getSize().x / 2);
            text.position.y = cubeBox.getCenter().y + (textBox.getSize().y / 2);
            text.position.z = cubeBox.getCenter().z + (textBox.getSize().z / 2);
            group.add( text );
            group.position.x = x;
            group.position.y = y;
            group.position.z = z;
        } );
        
        // add this element to the current scene
        this.scene.add(group);

        console.log(this.scene);

        return group;
    }

    /**
     * get default camera position
     */
    public setCameraPosition(x: number, y: number, z: number): void {
        this.camera.position.x = x;
        this.camera.position.y = y;
        this.camera.position.z = z;
    }

    /**
     * get default camera look at
     */
    public setCameraLookAtPosition(pos: THREE.Vector3): void {
        this.camera.lookAt(pos);
    }

    /**
     * get default camera
     */
    public getCamera(): THREE.PerspectiveCamera {
        return this.camera;
    }

    /**
     * get default canvas
     */
    public getCanvas(): HTMLCanvasElement {
        return this.canvas;
    }

    /**
     * get default canvas width
     */
    public getCanvasWidth(): number {
        return this.canvas.width;
    }

    /**
     * get default canvas width
     */
    public getCanvasHeight(): number {
        return this.canvas.height;
    }
}