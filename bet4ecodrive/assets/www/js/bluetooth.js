// Variables for storing values for log-file
var allDataRPM = "RPM, ";
var allDataLat = "Latitude, ";
var allDataLon = "Longitude, ";
var allDataAcc = "Accuracy, ";
var allDataSpeed = "Speed, ";
var allDataTime = "Time, ";
var maxRPMUser = "Max RPM, ";

$(function() {
	
	$("#save").bind("tap", store_LocalXML);
	
	// Bluetooth connection
	if(window.Controller.checkBluetooth()) {
		//alert("BT: available");
	}
	else {
		//alert("BT: not available");
	}
	
	if(window.Controller.enableBluetooth()) {
		//alert("BT: on");
	}
	else {
		//alert("BT: off");
	}
	
	alert("BT: found device " + window.Controller.findDevice());
	
	if(window.Controller.connect()) {
		alert("BT: connected");
	}
	else {
		alert("BT: not connected");
	}
	
	// Get the maximum RPM value for the bet
	var maxRPM = localStorage.getItem("sharedDZ") / 1000;
	
	// Create the chart for showing the RPM values in real-time
	var gaugeOptions = {
				
			    chart: {
			        type: 'solidgauge'
			    },
			    
			    title: null,
			    
			    pane: {
			    	center: ['50%', '85%'],
			    	size: '140%',
			        startAngle: -90,
			        endAngle: 90,
			        background: {
		                backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
		                innerRadius: '60%',
		                outerRadius: '100%',
		                shape: 'arc'
		            }
			    },

			    tooltip: {
			    	enabled: false
			    },
			       
			    // the value axis
			    yAxis: {
					stops: [
						[0.1, '#55BF3B'], // green
			        	[0.8, '#DDDF0D'], // yellow
			        	[0.95, '#DF5353'] // red
					],
					lineWidth: 0,
		            minorTickInterval: null,
		            tickPixelInterval: 400,
		            tickWidth: 0,
			        title: {
		                y: -70
			        },
		            labels: {
		                y: 16
		            }        
			    },
		        
		        plotOptions: {
		            solidgauge: {
		                dataLabels: {
		                    y: 5,
		                    borderWidth: 0,
		                    useHTML: true
		                }
		            }
		        }
		    };
		    
		    // The RPM gauge
		    $('#container-rpm').highcharts(Highcharts.merge(gaugeOptions, {
		        yAxis: {
		        	min: 0,
		        	max: maxRPM,
			        title: {
			            text: 'RPM'
			        }       
			    },
			
			    series: [{
			        name: 'RPM',
			        data: [1],
			        dataLabels: {
			        	format: '<div style="text-align:center"><span style="font-size:25px;color:' + 
		                    ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.1f}</span><br/>' + 
		                   	'<span style="font-size:12px;color:silver">* 1000 / min</span></div>'
			        },
			        tooltip: {
			            valueSuffix: ' revolutions/min'
			        }      
			    }]
			
			}));
		    
		    // Color for gps points on the map, default = green (until driver gets in a higher RPM range)
		    var gpsColor = '#55BF3B';
		                               
		    // Bring life to the dials, update RMP gauge every 3 seconds
		    setInterval(function () {
		        // RPM
		        chart = $('#container-rpm').highcharts();
		        if (chart) {
		            var point = chart.series[0].points[0],
		                newVal;
				    
		            // Get the actual RPM value
		            newVal = window.Controller.get_rpm() / 1000;

		            // If the RPM value is between 0 and maximal 20% more than the max RPM, the gauge graph is updated
		            if (newVal >= 0 && newVal <= (maxRPM + maxRPM * 0.2)) {		            			            	
		            	point.update(newVal);
		            }
		            
		        }
		    }, 3000);  
	
		    // Map 
		    var map;
		    var watchID;
		    var lat;
		    var long;
		    var ready = false;
		    var lives = 3;

		    var m = require(["esri/map", "esri/dijit/Geocoder", "esri/dijit/OverviewMap", "esri/dijit/Scalebar", "esri/dijit/Legend",  "esri/layers/ArcGISDynamicMapServiceLayer", "esri/tasks/locator", "esri/graphic", "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol", "esri/symbols/Font", "esri/symbols/TextSymbol", "dojo/_base/array",  "esri/TimeExtent", "esri/dijit/TimeSlider",  "esri/dijit/LocateButton", "dojo/_base/Color", "esri/layers/StreamLayer", "esri/layers/GraphicsLayer", "esri/symbols/PictureMarkerSymbol",  "esri/geometry/Point", "esri/layers/WMTSLayerInfo", "esri/layers/WMTSLayer",  "esri/layers/MapImage", "esri/layers/MapImageLayer", "esri/geometry/Extent", "esri/SpatialReference", "esri/symbols/SimpleLineSymbol", "esri/geometry/jsonUtils", "dojo/_base/connect", "dojo/number", "dojo/parser", "dojo/dom", "dijit/registry", "dojo/domReady!"], function(Map, Geocoder, OverviewMap, Scalebar, Legend, ArcGISDynamicMapServiceLayer, Locator, Graphic, InfoTemplate, SimpleMarkerSymbol, Font, TextSymbol, arrayUtils, TimeExtent, TimeSlider, LocateButton, Color, StreamLayer, GraphicsLayer, PictureMarkerSymbol, Point, WMTSLayerInfo, WMTSLayer, MapImage, MapImageLayer, Extent, SpatialReference, SimpleLineSymbol, jsonUtils, connect, number, parser, dom, registry) {
		    	// Definition of the map and its properties
		    	map = new Map("mapDiv", {
		    		basemap: "gray",
		    		center: [13.06165, 47.78873],
		    		zoom: 14,
		    		slider: true,
		    		logo: false
		    	});
		    	
		    	// Wait for device
		    	document.addEventListener("deviceready", onDeviceReady, false);
		    	watchID = null;
		    	
		    	// Device is ready
		    	function onDeviceReady() {
		    		ready = true;
		    	}

		    	// Bring life to the dials, update RPM and GPS every 3 seconds
			    setInterval(function () {
			       chart = $('#container-rpm').highcharts();
			        if (chart) {
			            var point = chart.series[0].points[0],
			                newVal;
			            
			            // The actual RPM value
			            newVal = window.Controller.get_rpm() / 1000;
			            maxRPM_onePercent = maxRPM / 100;
			            // Store RPM values for log-file
			            allDataRPM += (newVal * 1000) + ", ";
			            maxRPMUser += (maxRPM * 1000) + ", ";
			            
			            // If the RPM value is between 0 and the maximal value, the gauge graph is updated
			            if (newVal >= 0 && newVal <= maxRPM) {
			            	// Dependig on how near the actual RPM value is to the maximum RPM value, the color for the gps point on the map is set
			            	if(newVal / maxRPM_onePercent < 80){
			            		gpsColor = '#55BF3B';
			            	}
			            	else if(newVal / maxRPM_onePercent < 95){
			            		gpsColor = '#DDDF0D';
			            	}
			            	else if(newVal / maxRPM_onePercent <= 100){
			            		gpsColor = '#DF5353';
			            		navigator.notification.beep(1);
			            	}          	
			            	point.update(newVal);
			            }
			            // If the RPM value is higher than the maximum value, the driver looses a live, the gps point on the map is highlighted in black
			            // The driver gets a beep notification as sound feedback if he/she is over the maximum value, than he/she has 3 seconds time to reduce the RPMs, else he/she looses a live
			            else if(newVal > maxRPM && lives > 0){
			            	gpsColor = '#000000';
			            	if(lives == 3){
			            		document.getElementById("life3").src = "images/herz_gray.png";
			            		lives -= 1;
			            		navigator.notification.beep(2);
			            	}
			            	else if(lives == 2){
			            		document.getElementById("life2").src = "images/herz_gray.png";
			            		lives -= 1;
			            		navigator.notification.beep(2);
			            	}
			            	// After loosing 3 lives, the driver is game over and has lost the bet
			            	else if(lives == 1){
			            		document.getElementById("life1").src = "images/herz_gray.png";
			            		lives -= 1;
			            		navigator.notification.beep(3);
			            		alert("Game Over!");
			            	}
			            }
			         	// Get the current gps position using the smartphone intern gps function		            
			            navigator.geolocation.getCurrentPosition(onSuccess, onError);
			            
			        }
			    }, 3000);  
		    	
		    	// If the smartphone provides a gps position
		    	function onSuccess(position) {
		    		lat = position.coords.latitude;
		    		long = position.coords.longitude;
		    		// Create content for the infoTemplate
		    		var content = 'Latitude: ' + position.coords.latitude + '<br />' +
		    				     'Longitude: ' + position.coords.longitude + '<br />' +
		    				     'Accuracy: ' + position.coords.accuracy + '<br />' +
		    				     'Speed: ' + position.coords.speed + '<br />' +
		    				     'Timestamp: ' + new Date(position.timestamp);
		    		// Store gps data for the log-file
		    		allDataLat += position.coords.latitude + ", ";
		    		allDataLon += position.coords.longitude + ", ";
		    		allDataAcc += position.coords.accuracy + ", ";
		    		allDataSpeed += position.coords.speed + ", ";
		    		allDataTime += new Date(position.timestamp) + ", ";
		    		// Create a infoTemplate with infos about the gps position and accuracy
		    		var infoTemplate = new InfoTemplate();
		            infoTemplate.setTitle("GPS Position");
		            infoTemplate.setContent(content);
		            // Add gps position as point to the map
		    		var point = new Point(long, lat, new SpatialReference({wkid:4326}));
		    	    var simpleMarkerSymbol =  new SimpleMarkerSymbol(SimpleMarkerSymbol.STYLE_CIRCLE, 10, new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color('#ffffff'), 1), new Color(gpsColor));
		    	    var graphic = new Graphic(point, simpleMarkerSymbol);
		            map.graphics.add(graphic);
		       	}

		    	function onError(error) {
		    	    alert('code: '    + error.code    + '\n' +
		    	          'message: ' + error.message + '\n');
		    	}
		    	
		    });
		    	 
		    // Create local Xml file on the smartphone store
		    function store_LocalXML() {
		        window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, gotFS, fail);
		    }
		 
		    function gotFS(fileSystem) {
		        // Create directory
		        fileSystem.root.getDirectory("HCI_2014", {
		            create: true,
		            exclusive: false
		        }, gotDirEntry, fail); 
		    }

		    function gotDirEntry(dirEntry) {
		        // Create file
		        dirEntry.getFile("User_Data", {
		            create: true,
		            exclusive: false
		        }, gotFileEntry, fail);
		        
		    }

		    function gotFileEntry(fileEntry) {
		        fileEntry.createWriter(gotFileWriter, fail);
		    }

		    // Write content to the file
		    function gotFileWriter(writer) {
		        writer.onwrite = function (evt) {
		           //alert("write completed");
		        };
		        var content = maxRPMUser + "\n" + allDataRPM + "\n" + allDataLon + "\n" + allDataLat + "\n" + allDataAcc + "\n" + allDataSpeed + "\n" + allDataTime + "\n";
		        writer.write(content);
		        writer.abort();
		        alert("stored");
		    }

		    function fail(error) {
		        alert(error.code);
		    }
	
});