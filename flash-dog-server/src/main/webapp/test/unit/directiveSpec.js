'use strict';
/**
 * @author hushan.
 */

describe('fd.directiveSpec', function() {
    var scope, $compile;
    var element;
    var log;
    beforeEach(module('fd.directives'));
    beforeEach(inject(function ($rootScope, _$compile_,$log) {

        scope = $rootScope;
        $compile = _$compile_;
        log=$log;
        element = angular.element(
             '<div input-tip="this a tip"><input  /></div>');


    }  ));
    function createTips() {
        $compile(element)(scope);
        scope.$digest();
        return element;
    }
    it('should generate tip', function () {
        var tipEl = createTips();
        expect('<span ng-transclude=""><input class="ng-scope"></span>' +
            '<span class="text-info ng-binding ng-hide" ng-show="showTip">this a tip</span>')
            .toEqual(tipEl.html());
        expect(tipEl.find(".text-info").hasClass("ng-hide")).toEqual(true);
        tipEl.find("input"). triggerHandler('focus');
        expect(tipEl.find(".text-info").hasClass("ng-hide")).toEqual(false);
        tipEl.find("input"). triggerHandler('blur');
        expect(tipEl.find(".text-info").hasClass("ng-hide")).toEqual(true);

    });

});
