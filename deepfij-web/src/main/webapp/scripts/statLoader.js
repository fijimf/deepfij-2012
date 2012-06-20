$(document).ready(function () {
    $.get("/api/stat/wins", { },
        function (data) {
            $('#statName').text(data.name)
            $('#statMean').append(data.mean.toFixed(3))
            $('#statStdDev').append(data.stdDev.toFixed(3))

            var width = 800;
            var barHeight = 14;
            var height = barHeight * data.observations.length;

            // var data = [4, 8, 15, 16, 23, 42];

            var x = d3.scale.linear()
                .domain([0, data.max])
                .range([0, width]);


            var chart = d3.select("#chart")
                .append("svg")
                .attr("class", "chart")
                .attr("width", width)
                .attr("height", height)

            chart.selectAll("rect")
                .data(data.observations, function (d) {
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
                .data(data.observations, function (d) {
                    return d.name;
                })

                .enter().append("text")
                .attr("x", x(d.value))
                .attr("y", function (d, i) {
                    return i * barHeight;
                })
                .attr("dx", -3)// padding-right
                .attr("dy", ".35em")// vertical-align: middle
                .attr("text-anchor", "end")// text-align: right
                .text(d.name);

        });
})