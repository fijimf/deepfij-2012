$(document).ready(function () {
    $.get("/api/stat/wins", { },
        function (data) {
            $('#stat #name').replaceWith(data.name)
            $('#stat #mean').replaceWith(" "+data.mean)
            $('#stat #stddev').replaceWith(" "+data.stdDev)

            var outerW = 500;
            var outerH = 200;
            d3.select("#stat #shit").append()
              var colours = ["#08519C", "#3182BD", "#6BAED6", "#BDD7E7", "#EFF3FF"]; // ColorBrewer Blues
              var vis;
              var x0 = function (d) { return d.y0 * w; }; // lower bound
              var x1 = function (d) { return (d.y0 + d.y) * w; } // upper bound
              var y = function (d) { return d.x * h / 3; };

                vis = d3.select("#vis")
                    .append("svg:svg")
                    .attr("width", outerW)
                    .attr("height", outerH);

        });
})