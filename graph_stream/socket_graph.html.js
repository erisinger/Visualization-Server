<head>
  <!-- Plotly.js -->
<!--  <script src="plotly.min.js"></script> -->
  <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
  <script>

	var ws = new WebSocket("ws://127.0.0.1:8080/");
//	var ws = new WebSocket("ws://192.168.25.142:8080/");


	var d = new Date();
	var start = d.getTime();  

  	var w =10;

	var t = [d];
    var x = [0];
    var y = [0]; 
	var z = [0];
	
	var h = [72];
	
    var i= 0;		
    var p= 1;

	

	ws.onopen = function() {
	    console.log("Opened!");
	    ws.send("Hello Server");
	};

	ws.onmessage = function (evt) {
		var data = evt.data;
	    console.log("Message: " + data);
        var obj = JSON.parse(data);
		
		if (obj.hasOwnProperty('x')) {
			if(i>500){
				t.splice(0, 1);
			   	x.splice(0, 1);
			   	y.splice(0, 1);
				z.splice(0, 1);
			}
			
		    t.push(obj.t);
			x.push(obj.x);
			y.push(obj.y);
			z.push(obj.z);
		}
		else if (obj.hasOwnProperty('h')){ // hrt data
			if (h.length > 300){
				h.splice(0, 1);
			}
			
			h.push(obj.h);
		}
		
		i=i+1;
	};

	ws.onclose = function() {
	    console.log("Closed!");
	};

	ws.onerror = function(err) {
	    console.log("Error: " + err);
	};

      function init_plot(plot_div){
	
		//accel
		if (plot_div.id === 'myDiv'){
			var trace1 = {
				x: t, 
				y: x, 
				type: 'scatter'
			};
			var trace2 = {
				x: t,
				y: y,
				type: 'scatter'
			};
			var trace3 = {
				x: t,
				y: z,
				type: 'scatter'
			};
			var behavior_trace = {
				x: t,
				y: z,
				type: 'scatter'
			};

			var layout = {
				yaxis: {range: [-20, 20]}
			};

			Plotly.newPlot(plot_div, [trace1, trace2, trace3, behavior_trace], layout);
			return(plot_div.data);			
		}
		
		//hrt
		else if (plot_div.id === 'hrtDiv') {
			var trace1 = {
				x: i, 
				y: h, 
				type: 'scatter'			
			}
			var layout = {
				yaxis: {range: [50, 150]}
			};
			Plotly.newPlot(plot_div, [trace1], layout);
			return(plot_div.data);
		}
      }

      function update_plot(plotdata,plotdiv){
//			console.log("plotdiv: " + plotdiv.id);
			if (plotdiv.id == 'myDiv') {
				plotdata[0].x = t;
			    plotdata[0].y = x;
				var plotdiv  = document.getElementById('myDiv');
				Plotly.redraw(plotdiv);
			}
			else if (plot_div.id === 'hrtDiv') {
				plotdata[0].x = i;
			    plotdata[0].y = h;
				var plotdiv  = document.getElementById('hrtDiv');
				Plotly.redraw(plotdiv);
			}

        p=p+1;

	    if(p%100==0){
	        d = new Date();
	        now = d.getTime(); 
	        console.log("FPS: " + (p)/((now-start)/1000));
	    } 
      }

  </script>
</head>

<body>
  
  <div id="myDiv" style="width: 1000px; height: 400px;"><!-- Plotly chart will be drawn inside this DIV --></div>

<div id="hrtDiv" style="width: 1000px; height: 400px;"><!-- Plotly chart will be drawn inside this DIV --></div>

  <script>
	var accelPlotDiv  = document.getElementById('myDiv');
	var accelPlotData = init_plot(accelPlotDiv);

	//setInterval(update_data, 50);
//	setInterval(function(){ update_plot(plotData,plotDiv); }, 50);


	var hrtPlotDiv  = document.getElementById('hrtDiv');
	var hrtPlotData = init_plot(hrtPlotDiv);

	//setInterval(update_data, 50);
	setInterval(function(){ 
		update_plot(accelPlotData,accelPlotDiv);
		update_plot(hrtPlotData, hrtPlotDiv);
	 	}, 50);

  </script>



</body>

