$(function () {
	
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
	        	[0.5, '#DDDF0D'], // yellow
	        	[0.9, '#DF5353'] // red
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
        	max: 5,
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
                               
    // Bring life to the dials
    setInterval(function () {
        // RPM
        chart = $('#container-rpm').highcharts();
        if (chart) {
            var point = chart.series[0].points[0],
                newVal,
                inc = Math.random() - 0.5;
            
            //alert(window.Controller.get_rpm());
            
            newVal = window.Controller.get_rpm() / 1000;
            if (newVal >= 0 || newVal <= 5) {
            	point.update(newVal);
            }
            
            
        }
    }, 3000);  
    
	
});