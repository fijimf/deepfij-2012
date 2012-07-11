function teamStatBubbleChart(s) {
    $.get("/api/stat/bubble/" + s, { },
        function (stat) {
            $('#statName').text(stat.name);
//            $('#statMean').append(stat.mean.toFixed(3));
//            $('#statStdDev').append(stat.stdDev.toFixed(3));

            var data = stat;


            var r = 960;
            var format = d3.format(",d");
            var fill = d3.scale.category20c();

            var bubble = d3.layout.pack()
                .sort(null)
                .size([r, r]);

            var vis = d3.select("#chart").append("svg")
                .attr("width", r)
                .attr("height", r)
                .attr("class", "bubble");


            var node = vis.selectAll("g.node")
                .data(bubble.nodes(data))
                .enter().append("g")
                .attr("class", "node")
                .attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });

            node.append("title")
                .text(function (d) {
                    return d.name+":"+ d.value;
                });

            node.append("circle")
                .attr("r", function (d) {
                    return d.value;
                })
                .style("fill", function (d) {
                    if (d.children) {
                        return "#FFF";
                    }
                    else {
                        if (d.color){
                            return d.color;
                        } else {
                            return fill(d.name);
                        }
                    }
                }).style("stroke","#222");

//            node.append("text")
//                .attr("text-anchor", "middle")
//                .attr("dy", ".3em")
//                .text(function (d) {
//                    return d.name;
//                });
        });

}