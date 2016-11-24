var allDataRPM = "RPM, ";
var allDataLat = "Latitude, ";
var allDataLon = "Longitude, ";
var allDataAcc = "Accuracy, ";
var allDataSpeed = "Speed, ";
var allDataTime = "Time, ";
var maxRPMUser = "Max RPM, ";

$(function() {
	
	$("#save").bind("tap", store_LocalXML);
	
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
	
	//alert("BT: found device " + window.Controller.findDevice());
	
	if(window.Controller.connect()) {
		alert("BT: connected");
	}
	else {
		alert("BT: not connected");
	}
	
	/*setInterval(function() {
		alert("RPM: " + window.Controller.get_rpm())
			
		
	}, 3000);*/
	
	var maxRPM = localStorage.getItem("sharedDZ") / 1000;
	
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
		    
		    var gpsColor = '#55BF3B';
		                               
		    // Bring life to the dials
		    setInterval(function () {
		        // RPM
		        chart = $('#container-rpm').highcharts();
		        if (chart) {
		            var point = chart.series[0].points[0],
		                newVal;
		            
		            //alert(window.Controller.get_rpm());
		            
		            newVal = window.Controller.get_rpm() / 1000;
		            //maxRPM_onePercent = maxRPM / 100;
		            if (newVal >= 0 && newVal <= (maxRPM + maxRPM * 0.2)) {		            	
		            	/*if(newVal / maxRPM_onePercent < 50){
		            		gpsColor = '#55BF3B';
		            	}
		            	else if(newVal / maxRPM_onePercent < 90){
		            		gpsColor = '#DDDF0D';
		            	}
		            	else {
		            		gpsColor = '#DF5353';
		            	}*/
		            	
		            	point.update(newVal);
		            }
		            
		        }
		    }, 3000);  
	
		    var map;
		    var watchID;
		    var lat;
		    var long;
		    var ready = false;
		    var lives = 3;


		    var m = require(["esri/map", "esri/dijit/Geocoder", "esri/dijit/OverviewMap", "esri/dijit/Scalebar", "esri/dijit/Legend",  "esri/layers/ArcGISDynamicMapServiceLayer", "esri/tasks/locator", "esri/graphic", "esri/InfoTemplate", "esri/symbols/SimpleMarkerSymbol", "esri/symbols/Font", "esri/symbols/TextSymbol", "dojo/_base/array",  "esri/TimeExtent", "esri/dijit/TimeSlider",  "esri/dijit/LocateButton", "dojo/_base/Color", "esri/layers/StreamLayer", "esri/layers/GraphicsLayer", "esri/symbols/PictureMarkerSymbol",  "esri/geometry/Point", "esri/layers/WMTSLayerInfo", "esri/layers/WMTSLayer",  "esri/layers/MapImage", "esri/layers/MapImageLayer", "esri/geometry/Extent", "esri/SpatialReference", "esri/symbols/SimpleLineSymbol", "esri/geometry/jsonUtils", "dojo/_base/connect", "dojo/number", "dojo/parser", "dojo/dom", "dijit/registry", "dojo/domReady!"], function(Map, Geocoder, OverviewMap, Scalebar, Legend, ArcGISDynamicMapServiceLayer, Locator, Graphic, InfoTemplate, SimpleMarkerSymbol, Font, TextSymbol, arrayUtils, TimeExtent, TimeSlider, LocateButton, Color, StreamLayer, GraphicsLayer, PictureMarkerSymbol, Point, WMTSLayerInfo, WMTSLayer, MapImage, MapImageLayer, Extent, SpatialReference, SimpleLineSymbol, jsonUtils, connect, number, parser, dom, registry) {
		    	// Definition of the map and its properties
		    	//var initExtent = new Extent(1451519.2166422403,  6061157.883795095, 1452665.7720666754, 6061812.375849876, new SpatialReference({ wkid:3857 }));
		    	map = new Map("mapDiv", {
		    		basemap: "gray",
		    		slider: true,
		    		logo: false
		    	});
		    	
		    	// Wait for PhoneGap to load
		    	//
		    	document.addEventListener("deviceready", onDeviceReady, false);
		    	watchID = null;
		    	
		    	// PhoneGap is ready
		    	//
		    	function onDeviceReady() {
		    		ready = true;
		    	    // Update every 3 seconds
		    	    //var options = { frequency: 3000 };
		    	    //watchID = navigator.geolocation.getCurrentPosition(onSuccess, onError);
		    	}

		    	  // Bring life to the dials
			    setInterval(function () {
			        // RPM
			       chart = $('#container-rpm').highcharts();
			        if (chart) {
			            var point = chart.series[0].points[0],
			                newVal;
			            
			            //alert(window.Controller.get_rpm());
			            
			            newVal = window.Controller.get_rpm() / 1000;
			            maxRPM_onePercent = maxRPM / 100;
			            
			            //allData.push("RPM: " + newVal + ", ");
			            //allData.push("MAX RPM: " + maxRPM + "\n ");
			            allDataRPM += (newVal * 1000) + ", ";
			            maxRPMUser += (maxRPM * 1000) + ", ";
			            
			            //alert(maxRPM + ", " + maxRPM_onePercent);
			            if (newVal >= 0 && newVal <= maxRPM) {
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
			            	/*else{
			            		gpsColor = '#000000';
			            	}*/
			            	
			            	point.update(newVal);
			            }
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
			            	else if(lives == 1){
			            		document.getElementById("life1").src = "images/herz_gray.png";
			            		lives -= 1;
			            		navigator.notification.beep(3);
			            	}
			            	/*else{
			            		//alert("Game Over!");
			            		lives = -1;
			            		navigator.notification.beep(3);
			            	}*/
			            }
			            
			            /*if(newVal > maxRPM && lives >= 0){
			            	
			                //tg.startTone(ToneGenerator.TONE_PROP_BEEP);
			            	//navigator.notification.beep(2);
			            	
			            }*/
			            
			            navigator.geolocation.getCurrentPosition(onSuccess, onError);
			            
			        }
			    }, 3000);  
		    	
		    	// onSuccess Geolocation
		    	function onSuccess(position) {
		    		lat = position.coords.latitude;
		    		long = position.coords.longitude;
		    		
		    		var content = 'Latitude: ' + position.coords.latitude + '<br />' +
		    				     'Longitude: ' + position.coords.longitude + '<br />' +
		    				     'Accuracy: ' + position.coords.accuracy + '<br />' +
		    				     'Speed: ' + position.coords.speed + '<br />' +
		    				     'Timestamp: ' + new Date(position.timestamp);
		    		
		    		/*allData.push("Latitude: " + position.coords.latitude + ", ");
		    		allData.push("Longitude: " + position.coords.longitude + ", ");
		    		allData.push("Accuracy: " + position.coords.accuracy + ", ");
		    		allData.push("Speed: " + position.coords.speed + ", ");
		    		allData.push("Timestamp: " + new Date(position.timestamp) + "\n ");*/
		    		
		    		allDataLat += position.coords.latitude + ", ";
		    		allDataLon += position.coords.longitude + ", ";
		    		allDataAcc += position.coords.accuracy + ", ";
		    		allDataSpeed += position.coords.speed + ", ";
		    		allDataTime += new Date(position.timestamp) + ", ";
		    		
		    		//alert(content);
		    	     	
		    		var infoTemplate = new InfoTemplate();
		            infoTemplate.setTitle("GPS Position");
		            infoTemplate.setContent(content);

		    		 //alert(test);
		    		//alert(map.getZoom());
		    		 
		    		 //require(["esri/geometry/Point", "esri/SpatialReference", "esri/symbols/SimpleMarkerSymbol", "esri/Graphic", "esri/symbols/SimpleLineSymbol", "esri/Color"], function(Map, Point, SpatialReference, SimpleMarkerSymbol, Graphic, SimpleLineSymbol, Color) {
		    			 //alert(lat + ", " + long);
		    		 var point = new Point(long, lat, new SpatialReference({wkid:4326}));
		    	     var simpleMarkerSymbol =  new SimpleMarkerSymbol(SimpleMarkerSymbol.STYLE_CIRCLE, 10, new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color('#ffffff'), 1), new Color(gpsColor));
		    	     var graphic = new Graphic(point, simpleMarkerSymbol);
		             map.graphics.add(graphic);
		             //map.graphics.setInfoTemplate(infoTemplate);
		             map.centerAndZoom(point, 20);
		    	         //alert(map.getZoom());
		    	         
		    		 //});
		    	}

		    	// onError Callback receives a PositionError object
		    	//
		    	function onError(error) {
		    	    alert('code: '    + error.code    + '\n' +
		    	          'message: ' + error.message + '\n');
		    	}
		    	
		    });
		    
		    function getAllData(){
		    	var content = "";
		    	for (i = 0; i < allData.length; i++) {
		    	    content += allData[i];
		    	}
		    }
		    	 
		    /* Create Local Xml Files */
		    function store_LocalXML() {
		        window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, gotFS, fail);
		    }
		 
		    function gotFS(fileSystem) {
		        
		        // create dir
		        fileSystem.root.getDirectory("HCI_2014", {
		            create: true,
		            exclusive: false
		        }, gotDirEntry, fail);
		        
		    }

		    function gotDirEntry(dirEntry) {
		        // create file
		        dirEntry.getFile("User_Data", {
		            create: true,
		            exclusive: false
		        }, gotFileEntry, fail);
		        
		    }

		    function gotFileEntry(fileEntry) {
		        fileEntry.createWriter(gotFileWriter, fail);
		    }

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
	
//	alert("TestByte: " + window.Controller.returnByte());
	
	//alert("BT: initDongle read1 " + window.Controller.initDongle());
	
//	alert("BT: found device " + window.Controller.findDevice());
	
});