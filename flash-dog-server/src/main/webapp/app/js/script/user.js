'use strict';
/**
 * @author hill.hu
 */
angular.module('fd.user', ["fd.project"]).
    controller('UserListCtrl', function($scope,$http,Project,$location) {
        $scope.loadUsers=function(){
            $http.get("./user/list").success(function(webResult){
                $scope.webResult=webResult;
                $scope.selectedUser=null;
            });
        } ;
        $scope.loadUsers();
        $scope.createUser=function(){
            $scope.selectedUser={};
        } ;
        $scope.editUser=function(user){
            $scope.selectedUser=user;
        } ;
        $scope.updateUser=function(){
            $http.post("./user/update",$scope.selectedUser).success(function(webResult){
                $scope.renderResult(webResult);
                $scope.loadUsers();
            });
        };
        $scope.removeUser=function(user){
            if(confirm("您确定要删除此用户吗？"))  {
                $http.post("./user/destroy",user).success(function(result){
                    $scope.loadUsers();
                    $scope.renderResult(result);
                });
            }

        } ;
    }).factory('Auth', [function() {
        var user = {
            isLogged: false,
            username: '',
            authorize:function(userRole){

            }
        };
        return user;
    }]);