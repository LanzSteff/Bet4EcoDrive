var map;
var watchID;
var lat;
var long;

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
	    // Update every 3 seconds
	    var options = { frequency: 3000 };
	    watchID = navigator.geolocation.watchPosition(onSuccess, onError, options);
	}

	// onSuccess Geolocation
	function onSuccess(position) {
		lat = position.coords.latitude;
		long = position.coords.longitude;
		
		var content = 'Latitude: ' + position.coords.latitude + '<br />' +
				     'Longitude: ' + position.coords.longitude + '<br />' +
				     'Accuracy: ' + position.coords.accuracy + '<br />' +
				     'Speed: ' + position.coords.speed + '<br />' +
				     'Timestamp: ' + new Date(position.timestamp);
	     	
		var infoTemplate = new InfoTemplate();
        infoTemplate.setTitle("GPS Position");
        infoTemplate.setContent(content);

		 //alert(test);
		//alert(map.getZoom());
		 
		 //require(["esri/geometry/Point", "esri/SpatialReference", "esri/symbols/SimpleMarkerSymbol", "esri/Graphic", "esri/symbols/SimpleLineSymbol", "esri/Color"], function(Map, Point, SpatialReference, SimpleMarkerSymbol, Graphic, SimpleLineSymbol, Color) {
			 //alert(lat + ", " + long);
		 var point = new Point(long, lat, new SpatialReference({wkid:4326}));
	     var simpleMarkerSymbol =  new SimpleMarkerSymbol(SimpleMarkerSymbol.STYLE_CIRCLE, 10, new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color("#ffffff"), 1), new Color("#5AB548"));
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





