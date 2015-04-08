'use strict';
/**
 * @author hill.hu.
 */
var  _auth;

angular.module('app', ['fd.project','fd.setting','fd.user','ngRoute','ngAnimate',"fd.directives", 'pascalprecht.translate'])

.controller('MainController', function($scope, $route, $routeParams, $location,$rootScope) {
    $scope.$route = $route;
    $scope.$location = $location;
    $scope.$routeParams = $routeParams;
    $scope.auth={
        username:"",
        isAdmin:false,
        isAuthenticated:false,
        hasProject:function(project){
         if(!project)
             return false;
         return  this.isAdmin  || project.admins.indexOf(this.username)>-1;
     }
    };
        $scope.setPageTitle=function(pageTitle){
            $scope.pageTitle=pageTitle;

     }  ;
    jQuery.extend($scope.auth,_auth);

}).
    config(["$routeProvider","$httpProvider","$translateProvider",function($routeProvider,$httpProvider,$translateProvider){
        $routeProvider.when("/list",{
            controller:"ProjectListCtrl",
            templateUrl:"app/partial/project/list.html"

        }).when("/show/:name",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/project/show.html" ,
            activeTab: 'show'
        }).when("/new",{
            controller:"ProjectNewCtrl",
            templateUrl:"app/partial/project/new.html"
        }).when("/show/:name/warning",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/project/warning.html" ,
            activeTab: 'warning'
        }).when("/show/:name/log",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/project/log.html" ,
            activeTab: 'log'
        }).when("/show/:name/setting",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/setting/setting.html" ,
            activeTab: 'setting'
        }).when("/show/:name/task",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/project/task.html" ,
            activeTab: 'task'
        }).when("/show/:name/task/:taskName",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/project/task_edit.html" ,
            activeTab: 'task'
        }).when("/show/:name/mongodb",{
            controller:"ProjectCtrl",
            templateUrl:"app/partial/project/mongodb.html" ,
            activeTab: 'mongodb'
        })  .when("/user/list",{
            controller:"UserListCtrl",
            templateUrl:"app/partial/admin/user.html"
        })
            .otherwise({redirectTo:"/list"});

        var interceptor = ['$location', '$q', function($location, $q,$scope) {
            function success(response) {

                return response;
            }

            function error(response) {

                if(response.status === 401) {
                    window.location.href=".";
                    return $q.reject(response);
                }
                else {

                    return $q.reject(response);
                }
            }

            return function(promise) {
                return promise.then(success, error);
            }
        }];

        $httpProvider.responseInterceptors.push(interceptor);

        $translateProvider.useUrlLoader('/flash-dog/resource/messages');
//        $translateProvider.useStorage('UrlLanguageStorage');
        $translateProvider.preferredLanguage(_auth.lang);
        $translateProvider.fallbackLanguage('en');
    }]).directive('fdMessage', function () {
        return {
            restrict: 'C',
//            replace: true,
            template: '',
            link: function (scope, element, attrs) {
                scope.messages= [];
                scope.addMessage=function(content,level){
                    level=level||"success";
                    var messages = scope.messages;
                    messages.push({content:content,level:level});
                    setTimeout(function(){
                        messages.pop();
                        scope.$apply("messages");
                    },3000);
                } ;
                scope.renderResult=function(result){
                    if(result.success){
                       scope.addMessage(result.message||"tip.success","success")
                    } else{
                        scope.addMessage(result.message||"tip.fail","danger")
                    }
                }
            }}
    })
    . factory('UrlLanguageStorage', ['$location', function($location) {
        return {
            put: function (name, value) {},
            get: function (name) {
                return $location.search()['lang']
            }
        };
    }])
;
