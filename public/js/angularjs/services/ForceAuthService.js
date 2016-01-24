myApp.factory(
	'ForceAuthService',
	function($window, $rootScope,$log) {
		return {
			defaults : {
				scope : 'id',
				loginUrl : 'https://login.salesforce.com',
				callbackPath : undefined,
				popup : true,
				cacheTokenInSessionStorage : false,
				unauthorized : undefined,
				error : undefined
			},
	
			configure : function(config) {
				for ( var prop in this.defaults)
					this[prop] = this.defaults[prop];
				for ( var prop in config)
					this[prop] = config[prop];
			},
	
			ready : function(config) {
				if (!config)
					throw 'No config!';
				if (!config.clientId)
					throw 'No clientId property in config!';
				if (!config.popup
						&& !config.cacheTokenInSessionStorage)
					throw 'popup must be enabled if cacheTokenInSessionStorage is enabled';
	
				this.configure(config);
	
				if (this.hasAuthorizationResponse())
					this.callback();
				else if (this.hasSessionToken())
					this.authorized(this.getSessionToken());
				else if (this.unauthorized)
					this.unauthorized();
				else
					this.authorize();
			},
	
			authorize : function() {
				var theUrl = this.getAuthorizeUrl('popup');
				if (this.popup)
					this.openPopup(this.getAuthorizeUrl('popup'));
				else
					this.setWindowLocationHref(this
							.getAuthorizeUrl('page'));
			},
	
			callback : function(config) {
				if (config)
					this.configure(config);
				if (opener)
					opener.force.oauth._callback(window);
				else
					this._callback();
			},
	
			oauthCallback : function(tokenHash) {
				authorizationResponse = this.parseAuthorizationResponse(tokenHash);
				if (authorizationResponse.error) {
					if (this.error)
						this.error(authorizationResponse);
					else
						throw authorizationResponse;
				}
				this.setSessionToken(authorizationResponse);
			},
	
			clearSessionToken : function(token) {
				sessionStorage.setItem('token', undefined);
			},
	
			setSessionToken : function(token) {
				if (this.cacheTokenInSessionStorage){
					sessionStorage.setItem('token', JSON
							.stringify(token));
				}else{
					this.setCookie("token",JSON.stringify(token));
				}
			},
	
			getSessionToken : function() {
				var token = undefined;
				try {
					token = JSON.parse(this.getCookie('token'));
				} catch (err) {
					throw err;
				}
				$log.debug(token);
				return token;
			},
			resetSessionToken : function() {
				this.deleteCookie('token');
			},
	
			hasSessionToken : function() {
				try{
					return this.getSessionToken()
						&& this.getSessionToken() != null;
				} catch (err) {
					return false;
				}
			},
	
			parseAuthorizationResponse : function(hashFragment) {
				var authorizationResponse = {};
				if (hashFragment) {
					if (hashFragment[0] === '#')
						hashFragment = hashFragment.substr(1);
					var nvps = hashFragment.split('&');
					for ( var nvp in nvps) {
						var parts = nvps[nvp].split('=');
						authorizationResponse[parts[0]] = unescape(parts[1]);
					}
				}
				if (!authorizationResponse.access_token
						&& !authorizationResponse.error)
					authorizationResponse = undefined;
				return authorizationResponse;
			},
	
			hasAuthorizationResponse : function(hashFragment) {
				if (!hashFragment)
					hashFragment = this.getWindowLocationHash();
				if (hashFragment) {
					if (hashFragment[0] === '#')
						hashFragment = hashFragment.substr(1);
					var nvps = hashFragment.split('&');
					for ( var nvp in nvps) {
						var part = nvps[nvp].split('=');
						if (part)
							part = part[0];
						if (part
								&& (part === 'access_token' || part === 'error'))
							return true;
					}
				}
				return false;
			},
	
			getAuthorizeUrl : function(display) {
				var returnValue = this.loginUrl
						+ '/services/oauth2/authorize?response_type=token'
						+ '&display=' + escape(display) + '&scope='
						+ escape(this.scope) + '&client_id='
						+ escape(this.clientId) + '&redirect_uri='
						+ escape(this.getRedirectUrl());
				return returnValue;
			},
	
			openPopup : function(url) {
				$window.open(
								url,
								'Connect',
								'height=524,width=675,toolbar=0,scrollbars=0'
										+ ',status=0,resizable=0,location=0,menuBar=0,left='
										+ window.screenX
										+ (((window.outerWidth / 2) - (675 / 2)))
										+ ',top='
										+ window.screenY
										+ (((window.outerHeight / 2) - (524 / 2))))
						.focus();
			},
	
			getRedirectUrl : function() {
				return (this.callbackPath ? this.callbackPath
						: window.location.protocol
								+ window.location.pathname);
			},
	
			getCurrentUrl : function() {
				return window.location.protocol + '//'
						+ window.location.host
						+ window.location.pathname;
			},
			setWindowLocationHref : function(url) {
				window.location.href = url;
			},
			getWindowLocationHref : function() {
				return window.location.href;
			},
			replaceWindowLocation : function(url) {
				window.location.replace(url);
			},
			getWindowLocationHash : function() {
				return window.location.hash;
			},
			
			getCookie :  function (c_name) {
		        if (document.cookie.length > 0) {
		            c_start = document.cookie.indexOf(c_name + "=");
		            if (c_start != -1) {
		                c_start = c_start + c_name.length + 1;
		                c_end = document.cookie.indexOf(";", c_start);
		                if (c_end == -1) {
		                    c_end = document.cookie.length;
		                }
		                return unescape(document.cookie.substring(c_start, c_end));
		            }
		        }
		        return "";
		    },
		    setCookie : function (name, value, days) {
		        if (days) {
		            var date = new Date();
		            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		            var expires = "; expires=" + date.toGMTString();
		        }
		        else var expires = "";
		        document.cookie = name + "=" + value + expires + "; path=/";
		    },
		    
		    deleteCookie : function ( name ) {
		    	document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
		    }

		}
	}
);