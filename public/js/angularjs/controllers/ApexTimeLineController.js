var apexTimeLineControllers = angular.module('apexTimelineControllers', []);
apexTimeLineControllers.controller('ApexTimeLineController', 
							  ['$scope','$window','$http','ForceAuthService',
  function ($scope,$window,$http,ForceAuthService) {
	$scope.init = function(){
		$scope.$apply($scope.initializeData);
	}

    $scope.initializeData = function () {
		$scope.authorized=true;
        $scope.sessionToken = ForceAuthService.getSessionToken();
        $scope.minTimeToRun=100;
		$http({ method: 'POST', 
            url:'/logs',
            data:{
                'sessionId':$scope.sessionToken.access_token,
                'instanceUrl':$scope.sessionToken.instance_url
            },
            headers:{
            	'Accept':'application/json'
            }
        }).success(function(data, status) {
        	if(data.records){
        		$scope.logRecords=data.records;
        		$http({ method: 'POST', 
                    url:'/userinfo',
                    data:{
                        'sessionId':$scope.sessionToken.access_token,
                        'idUrl':$scope.sessionToken.id
                    },
                    headers:{
                    	'Accept':'application/json'
                    }
                }).success(function(data, status) {
                    $scope.userInfo=data;
                }).error(function(data, status) {
                });
        	}else{
            	$scope.authorized=false;
            	ForceAuthService.resetSessionToken();
            	return;
        	}
        }).error(function(data, status) {
        	$scope.authorized=false;
        	ForceAuthService.resetSessionToken();
        	
        });
		


    }
		
    $scope.initiateOAuth = function (){
    	ForceAuthService.ready($scope.config);
    }

    $scope.oauthCallback=function(popupWindow,tokenHash){
    	popupWindow.close();
    	ForceAuthService.oauthCallback(tokenHash);
    	$scope.init();
    }

    //Register the Oauth callback as a function on the main window
    $window.oauthCallback=$scope.oauthCallback;

	$scope.config= CONFIG ;
	try{
		$scope.sessionToken = ForceAuthService.getSessionToken();
		$scope.authorized=true;
		$scope.initializeData();
	}catch(err){
		$scope.authorized=false;
	}

    
  }
]);