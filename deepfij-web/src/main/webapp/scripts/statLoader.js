$(document).ready(function () {
    $.get("/api/stat/wins", { },
        function (stat) {
            $('#statName').text(stat.name)
            $('#statMean').append(stat.mean.toFixed(3))
            $('#statStdDev').append(stat.stdDev.toFixed(3))

            var data = stat.observations
            var width = 800;
            var barHeight = 14;
            var height = barHeight * data.length;

            var x = d3.scale.linear()
                .domain([0, stat.max])
                .range([0, width]);


            var chart = d3.select("#chart")
                .append("svg")
                .attr("class", "chart")
                .attr("width", width)
                .attr("height", height)

            chart.selectAll("rect")
                .data(data, function (d) {
                    return d.name;
                })
                .enter().append("rect")
                .attr("y", function (d, i) {
                    return i * barHeight;
                })
                .attr("width", function (d, i) {
                    return x(d.value);
                })
                .attr("height", barHeight)
                .attr("stroke", "white")
                .attr("fill", "steelblue");

            chart.selectAll("text")
                .data(data, function (d) {
                    return d.name;
                })
                .enter().append("text")
                .attr("x", function(d){
                    return x(d.value);
                })
                .attr("y", function (d, i) {
                    return i * barHeight;
                })
                .attr("dx", "-5px")// padding-right
                .attr("dy", "1.55em")// vertical-align: middle
                .attr("text-anchor", "end")// text-align: right
                .attr("style","color:white; font-size: 8px; font-weight: bold;")
                .text(function(d){
                    return d.name;
                });
             