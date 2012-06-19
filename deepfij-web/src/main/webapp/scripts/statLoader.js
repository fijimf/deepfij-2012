$(document).ready(function () {
    $.get("/api/stat/wins", { },
        function (data) {
            $('#stat #name').replaceWith(data.name)
            $('#stat #mean').replaceWith(" "+data.mean)
            $('#stat #stddev').replaceWith(" "+data.stdDev)
        });
})