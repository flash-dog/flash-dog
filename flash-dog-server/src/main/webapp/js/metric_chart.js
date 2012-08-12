var drawCharts = function(chartPanel, url, varName) {

    jQuery.ajax({

                url: url,
                data:'format=json',
                dataType:"json",
                headers: { "Accept": "application/json" },
                success: function(response) {
                    var data =  google.visualization.arrayToDataTable(response.data);

                    var bodyWidth = document.body.clientWidth;
                    var bodyHeight = document.body.clientHeight;

                    var options = {
                        width: bodyWidth*0.95, height: 440,
                        title: varName
                    };

                    var chart = new google.visualization.LineChart(document.getElementById(chartPanel));
                    chart.draw(data, options);
                }
            });

};
var drawStats = function(chartPanel, url, varName, params) {

    jQuery.ajax({
                url: url,
                dataType:"json",
                success: function(response) {
                    var data = new google.visualization.DataTable();
                    data.addColumn('string', 'Task');
                    data.addColumn('number', 'Hours per Day');
                    data.addRows(response.data);

                    var options = {
                        width: 450, height: 300,
                        title: varName
                    };

                    var chart = new google.visualization.PieChart(document.getElementById(chartPanel));
                    chart.draw(data, options);
                }
            });

};


