'use strict';
/**
 * @author hushan.
 */
angular.module('fd.project', [])  .
    controller('ProjectListCtrl', function($scope,$http,$location) {
        $scope.setPageTitle("首页");
        $scope. ALL_VIEW_NAME="全部";
        $scope.loadProjects=function(){
            $http.get("project/list").success(function(data,status){
                $scope.projects=data.projects;
                $scope.views=data.views;


                var all = [];
                angular.forEach( $scope.projects, function(item) {
                    all.push(item.name);
                });
                $scope.views.unshift({name:$scope.ALL_VIEW_NAME,projectNames:all});
                $scope.switchView($scope.views[0])       ;
            }) ;
        }  ;
        $scope.loadProjects();
        $scope.switchView=function(view){
            $scope.visibleProjects=[];
            $scope.activeView=view;
            console.log(view.projectNames);
            angular.forEach( $scope.projects, function(item) {

                if( $.inArray(item.name, view.projectNames)>-1)
                     $scope.visibleProjects.push(item);

            });


        } ;

    }).
    controller('ProjectNewCtrl', function($scope,$http,Project,$location,$rootScope) {
        $("#add_form").validate();
        $scope.project={};
        $scope.projectNameChanged=function(){
            var name=$scope.project.name.replace(/-/g,"_");
            $scope.project.logCollection=name+"_log";
            $scope.project.metricCollection=name+"_metrics"
        };
        $scope.addProject=function(){
            $http.post("projects/add", $scope.project).success(function(result){

                if(result.success) {
                    $location.path("/show/"+$scope.project.name+"/task/new") ;
                    $scope.addMessage("创建成功，从模板开始，先添加几个测试任务吧") ;
                }else{
                    $scope.renderResult(result);
                }



            });


        }

    }).
    controller("ProjectWarningsCtrl",function($scope,$http,$routeParams){
        $http.get("projects/"+$routeParams.name+"/warning/list").success(
          function(data){
              $scope.alerts=data.list;
          }
        );
        $scope.clearAlerts=function(){
            $http.get("projects/"+$routeParams.name+"/warning/clear").success(
                function(data){
                    $scope.alerts=[];
                }
            );
        }


    }).
    controller('ProjectCtrl', function($scope,$http,$routeParams,$rootScope) {
        jQuery.ajax({
            async:false,
            url: "project/"+$routeParams.name,
            data:'format=json',
            dataType:"json",
            headers: { "Accept": "application/json" },
            success: function(data) {
                $scope.project=data.project;
                $scope.setPageTitle($scope.project.alias)  ;
                $scope.metricNames=data.metricNames;
            }});

    }).
    controller('TaskControl', function($scope,$http,$routeParams,$location) {
        $scope.removeTask=function(task){
            if(confirm("您确定要删除此任务["+task.name +"]?"))  {
                $http.post("projects/"+$scope.project.name+"/tasks/"+task.name+"/destroy",{}).success(function(result) {
                    angular.forEach( $scope.project.tasks, function(item,index) {
                        if( item.name== task.name ) {
                            $scope.project.tasks.splice(index,1);

                        }
                    });
                   $scope.renderResult(result);
                });
            }

        }
    }).
    controller('TaskEditControl', function($scope,$http,$routeParams,$location) {

        $scope.task={name:"task",cron:"40 */5 * * * *",timeout:40};
        angular.forEach($scope.project.tasks,function(task){
            if($routeParams.taskName==task.name){
                $scope.task=task;
                $scope.initScript=($scope.task.script);
            }
        });

        $scope.updateTask=function(){
            $scope.task.script=$scope.script_text.getValue();
            $http.post("projects/"+$scope.project.name+"/tasks/update",$scope.task).success(function(result) {
                $scope.renderResult(result);
                $location.path("/show/"+$scope.project.name+"/task") ;
            });
            return false;
        };
    }).
    controller('MongodbCtrl', function($scope,$http,$routeParams) {

    }).
    controller('ProjectShowCtrl', function($scope,$http,$routeParams) {
       console.log("1");
      $scope.$watch("project",function(){
       if($scope.project&& $scope.project.chartViews.length>0){
            $scope.drawChartView($scope.project.chartViews[0].title);
        }
        }) ;
        $scope.chartData={chartTitle:"",data:[]};
        $scope.drawChartView=function(chartTitle){
            if(chartTitle)
                $scope.chartTitle=chartTitle;
            var url=    "projects/"+$scope.project.name+"/metrics/show"+"?title="+encodeURIComponent($scope.chartTitle) ;
            $http.get(url).success(
                function(response) {
                    $scope.chartData={chartTitle:chartTitle,data:response.data};



            });

        };
        $scope.addChartView=function(){
            $scope.chartView= {title:'',metricNames:[]} ;
        } ;
        $scope.addMetric=function(metric){
           if(!$scope.chartView.title){
               $scope.chartView.title= metric;
           }
        };
        $scope.saveChartView=function(chartView){
            var title = chartView.title;
            if(!title || title.length<1)
                return;
            var metricNames=[];
                $("#metricsViewForm").find("input[name='metricName']:checked").each(function(index,item){
                    metricNames.push($(item).val());
            });
            console.log(metricNames) ;
            chartView.metricNames=metricNames;
            $http.post("projects/"+$scope.project.name+"/metrics/add?",chartView)
                .success(function(data){
                 $scope.project.chartViews.push(chartView);
            });
        };
        $scope.removeChartView=function(){
            var title = $scope.chartTitle;
            if(!title)
                return;
            if(confirm("您确定要删除视图["+title+"]吗")){
                $http.get("projects/"+ $scope.project.name+"/metrics/destroy?title="+encodeURIComponent(title)).success(function(){
                    var chartViews = $scope.project.chartViews;
                    angular.forEach(chartViews, function(item,index) {
                        if( item.title== $scope.chartTitle ) {
                            chartViews.splice(index,1);
                            $scope.drawChartView(chartViews[0].title);
                        }
                    });
                    $scope.addMessage("删除成功!");

                });

            }
        };
        $scope.updateTimeRange=function(){
            $http.post("projects/"+$scope.project.name+"/setting/timeRange",$scope.project.timeRange).success(function(){
                $scope.drawChartView();
            });
        } ;
    }).
    filter('projectFilter', function() {
        return function( items,view) {
            if(!items)
            return;
            console.log('projectView',arguments);
            var filtered = [];
            angular.forEach(items, function(item) {
                if($.inArray(item.name,items)>-1  ) {
                    filtered.push(item);
                }
            });
            return filtered;
        };
    }).
    factory("Project",["$http",function($http,$scope){
        return {

        };
    }]).   controller('LogCtrl', function($scope,$http,$routeParams) {
        var now = new Date();
        var today =now.getFullYear()+"-"+(now.getMonth()+1)+"-"+now.getDate()+" 00:00:00";
        $("#datepicker_start").val(today);
       var script_text = CodeMirror.fromTextArea(document.getElementById("script_text"), {
            lineNumbers: true,
            electricChars: false
        });
        $scope.autoLoad=false;
        $scope.clearLog=function(){
            script_text.setValue("");
        } ;
        $scope.downloadLog=function(){
            var params = jQuery.param({
                start:$("#datepicker_start").val(),
                end:$("#datepicker_end").val(),
                keyWord:$("#key_word_input").val(),
                level: $("#level").val()});

            window.location  ="projects/"+$scope.project.name+"/logs/download?"+ params;
        }  ;
        $scope.loadLog=function(){
            var params = jQuery.param({
                start:$("#datepicker_start").val(),
                end:$("#datepicker_end").val(),
                keyWord:$("#key_word_input").val(),
                level: $("#level").val()});
            $("#search_btn").attr("disabled",true);
            $("#search_btn").val("请等待");
            jQuery.ajax({
                url:"projects/"+$scope.project.name+"/logs/more?format=json",
                data: params,
                type:'get',
                success : function(msg) {
                    $("#search_btn").val("查询");
                    $("#search_btn").attr("disabled",false);
                    script_text.setValue(msg);
                    if($scope.autoLoad){
                        setTimeout($scope.loadLog,5000);
                    }
                }
            });
        }
    }).directive('jsConsole', ["$http", function ($http) {
        return {
            restrict: 'EA',
            link: function ($scope, element, attrs) {
                $scope.script_text= CodeMirror.fromTextArea(element.find(".script-text")[0], {
                    lineNumbers: true,
                    matchBrackets: true,
                    theme:"blackboard"
                });
                var script_console = CodeMirror.fromTextArea(element.find(".script-console")[0], {
                    lineNumbers: true
                });

               $scope.$watch('initScript',function(nv){
                   if(nv){
                       $scope.script_text.setValue(nv.trim());
                       $scope.script_text.scrollTo(1,1);
                   }

               });
                $scope.clearConsole=function(){
                    script_console.setValue('');
                }  ;
                $scope.runScript=function(){
                    var params = jQuery.param({script:$scope.script_text.getValue()});
                    $http.post("projects/"+$scope.project.name+"/mongo/console?format=json&"+params).success(function(msg) {
                        if(script_console){
                            script_console.setValue(script_console.getValue()+ JSON.stringify(msg) +"\n");}
                    });

                };
                $(".template_btn").click(function(){
                    var template=$("#"+$(this).attr("data-target"));
                    var cron=template.attr("data-cron");

                    $scope.script_text.setValue(template.html().trim());
                });
            }
        };
    }]).directive('chartView', function () {
        return {
            restrict: 'C',
            replace: true,
            scope: false,
            controller: function ($scope, $element, $attrs) {
                console.log(2);

            },
            template: '<div id="container" style="margin: 0 auto">loading</div>',
            link: function (scope, element, attrs) {
                Highcharts.setOptions({
                    global: {
                        useUTC: false
                    }
                });
                var drawPlot=function(title,series){
                    var chart = new Highcharts.Chart({
                        xAxis: {
                            type: 'datetime',
                            dateTimeLabelFormats: {
                                day: '%Y/%m/%d %H:%M'
                            }
                        },
                        yAxis: {
                            title: {
                                text: ''
                            }
                        },
                        chart: {
                            renderTo: 'container',
                            type: 'spline'
                        },

                        type: 'datetime',

                        title: {
                            text: ""
                        },
                        tooltip: {
                            formatter: function () {
                                return '<b>' + this.series.name + '</b><br/>' +
                                    Highcharts.dateFormat('%Y-%m-%d %H:%M', this.x) + ':<br/>' +
                                    Highcharts.numberFormat(this.y, 2);
                        }},

                        series: series
                    });
                };
                scope.$watch("chartData", function (chartData) {

                    if(!scope.chartData || !scope.chartData.data||scope.chartData.data.length<1)
                    return;
                    console.log("render fire") ;
                    var series=[];
                    var columns = chartData.data[0];
                    angular.forEach(columns,function(column,index){
                        if(index>0)
                            series.push({name:column,data:[], marker: {
                                enabled: true,
                                symbol: 'circle',
                                radius: 0

                            },  turboThreshold:50000});
                    }) ;
                    angular.forEach(chartData.data,function(row,rowIndex){
                        if(rowIndex>0)  {
                            for(var i=1;i<columns.length;i++)  {
                                var items= series[i-1];
                                items.data.push({x:row[0],y:row[i]});
                            }

                        }
                    }) ;

                    drawPlot(chartData.chartTitle,series);
                }, true);

            }
        }
    });



