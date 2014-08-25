'use strict';
/**
 * @author hill.hu
 */
angular.module('fd.setting', ["fd.project"]).
    controller('SettingCtrl', function($scope,$http,Project,$location) {

        $scope.deleteProject =function(){
            if(confirm("你确认删除此监控项目吗?")) {

                $http.get("./projects/"+$scope.project.name+"/destroy").success(function(result){
                   if(result.success){
                        $location.path("#/list");
                    }
                    $scope.renderResult(result);
                });



            }

        }
    }).
    controller('SettingBasicCtrl', function($scope,$http) {
        $scope.admins= $scope.project.admins.join(',')  ;
          $scope.updateBasic=function(){
              $scope.project.admins=$scope.admins.split(",");
              $http.post("./projects/"+ $scope.project.name+"/basic", $scope.project).success(function(result){
                  $scope.renderResult(result);
              });

          };
         $scope.initMode={capped:true,unit:1024*1024,size:10,success:false};
        $scope.dbMode={capped:false,unit:1024*1024,size:1024,success:false,count:0,dbEditing:false};

        var project=$scope.project;
          $scope.updateDbSize=function(){
              var size=$scope.initMode.size*$scope.initMode.unit;

              var script="db."+project.logCollection+".drop() ;db.createCollection('"+
                  project.logCollection+ "', {capped:"+ $scope.initMode.capped+", size:"+size+"});db."+
                  project.logCollection+".ensureIndex({timestamp:1});";
              console.log(script);
              var params = jQuery.param({script:script});
              $http.post("./projects/"+project.name+"/mongo/console?format=json&"+params) .success(function(msg) {
                  $scope.renderResult({success:true});

                  $scope.queryDb();
              }) ;

          };

        $scope.queryDb=function(){
            var script="db."+ project.logCollection+".stats()" ;
            var params = jQuery.param({script:script});
            $http.post("./projects/"+project.name+"/mongo/console?format=json&"+params) .success(function(dbResponse) {
                $scope.dbMode.capped= dbResponse.retval.capped;
                var size=  dbResponse.retval.size;
                $scope.dbMode.size =size/(1024*1024);
                $scope.dbMode. unit=(1024*1024);
                $scope.dbMode. count=dbResponse.retval.count;
                $scope.dbMode.dbEditing=false;
            }) ;
        };
        $scope.queryDb();
    }).
    controller('SettingExtCtrl', function($scope,$http,Project,$location) {
        var properties=$scope.project.properties;
        if(!properties.httpNotifyConfig_url ){
            properties.httpNotifyConfig_url="";
            properties.httpNotifyConfig_encode="utf-8";
            properties.httpNotifyConfig_template="user=$x";
            properties.httpNotifyConfig_properties="x=1";

        }
        $('#http_alert_test_form').submit(function() {
            var data=$(this).serialize();
            $http.get("./projects/"+$scope.project.name+"/notifier/http/test?"+data).success(
                function(){
                    $scope.addMessage("发送成功!");
                }
            );

        return false;
    });
        $scope.updateExt=function(){
            $http.post("./projects/"+$scope.project.name+"/ext",properties).success(function(result){
                 $scope.renderResult(result);
            });
        };

    }).
    controller('SettingDogCtrl', function($scope,$http,Project,$location) {

        $scope.addDog=function(){
            $scope.activeDog={startTime:"00:00:00",endTime:"24:00:00",mailList:$scope.project.mailList};
        };
        $scope.updateDog=function(){
          $http.post("./projects/"+$scope.project.name+"/dog/",$scope.activeDog).success(
              function(result){
                  $scope.project.metricDogs= result.list;
                  $scope.renderResult(result);
                  $scope.activeDog=null;
              }
          );
        };
        $scope.removeDog=function(){
            $http.get("./projects/"+$scope.project.name+"/dog/destroy?dogName="+encodeURIComponent($scope.activeDog.name)).success(
                function(result){
                    $scope.project.metricDogs= result.list;
                    $scope.activeDog=null;
                    $scope.renderResult(result);
                }
            );
        };
        $scope.editDog=function(dog){
            $scope.activeDog=dog;
        }
    }).
    controller('SettingViewCtrl', function($scope,$http,Project,$location) {

        $scope.addView=function(view){
            if(view.name==$scope.ALL_VIEW_NAME)
                return;
            var projectNames=[];
            $("#viewDialog").find("input:checked").each(function(index,item){
                projectNames.push($(item).val());
            });
            view.projectNames=projectNames;
            $http.post("./admin/views/save",view).success(function(result){
                if(result.success) {
                    $scope.loadProjects();
                }
                $scope.renderResult(result);
            });
        };
        $scope.editView=function(view){
           if(view){
               $scope.selectedView=view;
           }else{
               $scope.selectedView={name:'',projectNames:[]} ;
           }
        } ;
        $scope.removeView=function(view){

            $http.get("./admin/views/destroy?name="+encodeURIComponent(view.name)).success(function(){
                $scope.loadProjects();
                $scope.addMessage("删除成功!");
            });


        }
    });