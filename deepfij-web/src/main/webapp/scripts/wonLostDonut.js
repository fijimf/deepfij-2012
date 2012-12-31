


var width = 960,
    height = 80,
    radius = Math.min(width, height) / 2;

var games = [
    {"score":100, "oppScore":70},
    {"score":65, "oppScore":70},
    {"score":120, "oppScore":1150},
    {"score":90, "oppScore":70},
    {"score":70, "oppScore":80},
    {"score":100, "oppScore":70}
];

var svg = d3.select("#donuts").append("svg")
                                    .attr("width", width)
                                    .attr("height", height)
                                    .append("g")
                                    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");


var arc = d3.svg.arc()
    .outerRadius(radius - 2)
    .innerRadius(radius - 14);

var pie = d3.layout.pie()
    .sort(null)
    .value(function(d) { return d.oppScore; });

var g = svg.selectAll(".arc")
      .data(pie(games))
      .enter().append("g")
      .attr("class", "arc");

  g.append("path")
      .attr("d", arc)
      .style("fill", function(d) { if (d.score>d.oppScore) {return "green";} else {return "red";} })
      .style("stroke", "#333")
      .style("stroke-width", 1);

  g.append("text")
      .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
      .attr("dy", ".35em")
      .style("text-anchor", "middle")
      .text(function(d) { return d.data.age; });
