
function statHistogram(stat) {
   var values = stat.data.map( function(d) { return d.value;});

   // A formatter for counts.
   var formatCount = d3.format(",.0f");

   var margin = {top: 30, right: 30, bottom: 30, left: 30};
   var width = 480 - margin.left - margin.right;
   var height = 300 - margin.top - margin.bottom;

   var x = d3.scale.linear().domain([0, d3.max(values)]).range([0, width]);

   // Generate a histogram using twenty uniformly-spaced bins.
   var data = d3.layout.histogram().bins(x.ticks(d3.max(values),16))(values);

   //console.info(data)

   var y = d3.scale.linear().domain([0, d3.max(data, function(d) { return d.y; })]).range([height, 0]);

   var xAxis = d3.svg.axis().scale(x).orient("bottom");

   var hist = d3.select("#graphPanel").append("svg")
                  .attr("width", width + margin.left + margin.right)
                  .attr("height", height + margin.top + margin.bottom)
                  .append("g")
                  .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

   var bar = hist.selectAll(".bar")
                  .data(data)
                  .enter().append("g")
                  .attr("class", "bar")
                  .attr("transform", function(d) { return "translate(" + x(d.x) + "," + y(d.y) + ")"; });

   bar.append("rect")
                  .attr("x", 1)
                  .attr("width", x(data[0].dx) - 1)
                  .attr("height", function(d) { return height - y(d.y); });

   bar.append("text")
                  .attr("dy", "-.75em")
                  .attr("y", 6)
                  .attr("x", x(data[0].dx) / 2)
                  .attr("text-anchor", "middle")
                  .text(function(d) { return formatCount(d.y); });

   hist.append("g")
                  .attr("class", "x axis")
                  .attr("transform", "translate(0," + height + ")")
                  .call(xAxis);

   var scatter = d3.select("#graphPanel").append("svg")
                  .attr("width", width + margin.left + margin.right)
                  .attr("height", height + margin.top + margin.bottom)
                  .append("g")
                  .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


   var scatterY = d3.scale.linear().domain([0, d3.max(values, function(d) { return d; })]).range([height, 0]);

   var scatterX = d3.scale.ordinal().domain([1, values.length]).range([0, width]);

  var scatterXAxis = d3.svg.axis().scale(scatterX).orient("bottom");
  var scatterYAxis = d3.svg.axis().scale(scatterY).orient("left");
      scatter.append("g")
          .attr("class", "axis")
          .attr("transform", "translate(0, "+margin.top+")")
          .call(scatterXAxis);

      scatter.append("g")
          .attr("class", "axis")
          .attr("transform", "translate("+(margin.left)+", 0)")
          .call(scatterYAxis);

}
