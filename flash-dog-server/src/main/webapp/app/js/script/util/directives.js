'use strict';
/**
 * @author hill.hu.
 */
angular.module('fd.directives', []).
    directive('inputTip', function () {
    return {
        restrict: 'A',
        scope:true,
        transclude:true,
        template: '<span ng-transclude></span><span class="text-info" ng-show="showTip">{{inputTip}}</span>',
        link: function (scope, element, attrs) {
            scope.inputTip=attrs.inputTip;
            scope.showTip=false;
            element.find("input").focus(function(){
                scope.showTip=true;
                scope.$apply("showTip") ;
            });
            element.find("input").blur(function(){
                scope.showTip=false;
                scope.$apply("showTip") ;
            });
        }
    }});