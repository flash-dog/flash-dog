module.exports = function(config){
  config.set({

    basePath : '../',
      preprocessors: {
          '**/*.html': ['ng-html2js']
      },

      files : [
        ,"app/js/vender/jquery/jquery-1.7.2.min.js"
        ,"app/js/vender/angular/angular.js"
        , "app/js/vender/angular/angular-animate.js"
       , "app/js/vender/angular/angular-route.js"
        , "app/js/vender/angular/angular-mocks.js"

        , "test/unit/**/*.js"
    ],
      ngHtml2JsPreprocessor: {
          // strip this from the file path
//          stripPrefix: 'wms/',
          // prepend this to the
//          prependPrefix: 'base/',

          // or define a custom transform function
//          cacheIdFromPath: function(filepath) {
//              return cacheId;
//          },

          // setting this option will create only a single module that contains templates
          // from all the files, so you can load them all with module('foo')
//          moduleName: 'foo'
      },
    logLevel : 'LOG_DEBUG',
    autoWatch : true,

    frameworks: ['jasmine'],

    browsers : ['Chrome'],
      reporters: ['progress'],
    plugins : [
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-jasmine',
            'karma-junit-reporter'  ,
        "karma-ng-html2js-preprocessor"

            ],
      exclude: [

      ],
    junitReporter : {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    }

  });
};
