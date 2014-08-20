// we get all the test files automatically
var tests = [];
for (var file in window.__karma__.files) {
    if (window.__karma__.files.hasOwnProperty(file)) {
        if (/spec\.js$/i.test(file)) {
            tests.push(file);
        }
    }
}
require.config({
    baseUrl: '/base/static/js',
    paths: {
        angular:"vender/angular/angular" ,
        jquery:"vender/jquery/jquery-1.8.1"   ,
        ngAnimate: "vender/angular/angular-animate",
        angularRoute: "vender/angular/angular-route",
        angularMocks: "vender/angular/angular-mocks"
    },

    shim: {
        'angular' : {'exports' : 'angular'},
        'angularRoute': ['angular'],
        'angularMocks': {
            deps:['angular'],
            'exports':'angular.mock'
        }
    },
    deps: tests,
    callback: window.__karma__.start
});