const Twix=function(){function t(){}t.ajax=function(t){t=t||{url:""},t.type=t.type&&t.type.toUpperCase()||"GET",t.headers=t.headers||{},t.timeout=parseInt(t.timeout)||0,t.success=t.success||function(){},t.error=t.error||function(){},t.async="undefined"==typeof t.async?!0:t.async;var e=new XMLHttpRequest;t.timeout>0&&(e.timeout=t.timeout,e.ontimeout=function(){t.error("timeout","timeout",e)}),e.open(t.type,t.url,t.async);for(var s in t.headers)t.headers.hasOwnProperty(s)&&e.setRequestHeader(s,t.headers[s]);return e.send(t.data),e.onreadystatechange=function(){if(4==this.readyState&&(this.status>=200&&this.status<300||304==this.status)){var e=this.responseText,s=this.getResponseHeader("Content-Type");s&&s.match(/json/)&&(e=JSON.parse(this.responseText)),t.success(e,this.statusText,this)}else 4==this.readyState&&t.error(this.status,this.statusText,this)},0==t.async&&(4==e.readyState&&(e.status>=200&&e.status<300||304==e.status)?t.success(e.responseText,e):4==e.readyState&&t.error(e.status,e.statusText,e)),e};var e=function(e,s,n,r){return"function"==typeof n&&(r=n,n=void 0),t.ajax({url:s,data:n,type:e,success:r})};return t.get=function(t,s,n){return e("GET",t,s,n)},t.head=function(t,s,n){return e("HEAD",t,s,n)},t.post=function(t,s,n){return e("POST",t,s,n)},t.patch=function(t,s,n){return e("PATCH",t,s,n)},t.put=function(t,s,n){return e("PUT",t,s,n)},t["delete"]=function(t,s,n){return e("DELETE",t,s,n)},t.options=function(t,s,n){return e("OPTIONS",t,s,n)},t}();__=Twix;

const jsonToString = function(json) {
	const jsonStringify = JSON.stringify;	
    const arrayToJson = Array.prototype.toJSON;
    delete Array.prototype.toJSON;
    const output = jsonStringify(json);
    Array.prototype.toJSON = arrayToJson;
    return output;
};

const readCookie = function (cname) {
  const name = cname + "=";
  const decodedCookie = decodeURIComponent(document.cookie);
  const ca = decodedCookie.split(';');
  for(let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c;
    }
  }
  return "";
}

const PjeOffice = (function () {
  	
    function PjeOffice() { }

    const _PROTOCOL 		 = "http";
  	const _PORT 			 = 8800;
  	const _POST_TIMEOUT      = 15000; //millis
    const _APP_REQUISITANTE  = ""; //insert your const here
    const _WEB_ROOT  		 = ""; //insert your const here
    const _CODIGO_SEGURANCA  = ""; //insert your const here
    const _PAGINA_LOGIN		 = ""; //insert your const here
    const _PAGINA_ASSINATURA = ""; //insert your const here
    const _PAGINA_DOWNLOAD   = ""; //insert your const here
    const _PARAM_DEJTACTION  = ""; //insert your const here
    const _PARAM_MO		 	 = ""; //insert your const here
    const _PARAM_CID		 = ""; //insert your const here

	//please do NOT change constants below
    const _BASE_END_POINT 	 = _PROTOCOL + "://127.0.0.1:" + _PORT;
  	const _BASE_CONTEXT 	 = "/pjeOffice/";
  	const _TASK_END_POINT	 = _BASE_CONTEXT + "requisicao/"
    const _LOGOUT_END_POINT  = _BASE_CONTEXT + "logout/"
     
    const noCache = function() {
		return '&u=' + new Date().getTime();
	};
	
    const createQueryParams = function(taskId, task) {
		return _TASK_END_POINT + "?r=" + encodeURIComponent(jsonToString({
			"sessao": readCookie('JSESSIONID'),
			"aplicacao": _APP_REQUISITANTE,
			"servidor": _WEB_ROOT,
			"codigoSeguranca": _CODIGO_SEGURANCA,
			"tarefaId": taskId,
			"tarefa": jsonToString(task)	
		})) + noCache();
	};
	
	const post = function (data, onSuccess, onFailed) {
		Twix.ajax({
			"url": _BASE_END_POINT + data,
			"type": 'POST',
			"headers": { "Content-Type": 'application/x-www-form-urlencoded'},
			"timeout": _POST_TIMEOUT,
			"success": onSuccess,
			"error": onFailed,
			"async": true
		});
	};
	
	const runTask = function(taskId, task, onSuccess, onFailed) {
		post(createQueryParams(taskId, task), onSuccess, onFailed);
	};
	
    const runTask_cnj_assinador = function(task, onSuccess, onFailed) {
		runTask('cnj.assinador', task, onSuccess, onFailed);
	};
	
	const runTask_cnj_assinadorHash = function(task, onSuccess, onFailed) {
		runTask('cnj.assinadorHash', task, onSuccess, onFailed);
	};
	
	const runTask_cnj_autenticador = function(task, onSuccess, onFailed) {
		runTask('cnj.autenticador', task, onSuccess, onFailed);
	};
	
	const runTask_cnj_assinadorBase64 = function(task, onSuccess, onFailed) {
		runTask('cnj.assinadorBase64', task, onSuccess, onFailed);
	};
	
	const runTask_sso_autenticador = function(task, onSuccess, onFailed) {
		runTask('sso.autenticador', task, onSuccess, onFailed);
	};
	
	const runTask_util_impressor = function(task, onSuccess, onFailed) {
		runTask('util.impressor', task, onSuccess, onFailed);
	};
	
	const runTask_util_downloader = function(task, onSuccess, onFailed) {
		runTask('util.downloader', task, onSuccess, onFailed);
	};
	
	const runTask_pdf_join = function(task, onSuccess, onFailed) {
		runTask('pdf.join', task, onSuccess, onFailed);
	};
	
	const runTask_pdf_split_by_size= function(task, onSuccess, onFailed) {
		runTask('pdf.split_by_size', task, onSuccess, onFailed);
	};
	
	const runTask_pdf_split_by_parity = function(task, onSuccess, onFailed) {
		runTask('pdf.split_by_parity', task, onSuccess, onFailed);
	};
	
	const runTask_pdf_split_by_count = function(task, onSuccess, onFailed) {
		runTask('pdf.split_by_count', task, onSuccess, onFailed);
	};
	
	const runTask_pdf_split_by_pages = function(task, onSuccess, onFailed) {
		runTask('pdf.split_by_pages', task, onSuccess, onFailed);
	};
	
	const runTask_video_split_by_duration = function(task, onSuccess, onFailed) {
		runTask('video.split_by_duration', task, onSuccess, onFailed);
	};
	
	const runTask_video_split_by_size = function(task, onSuccess, onFailed) {
		runTask('video.split_by_size', task, onSuccess, onFailed);
	};
	
	const runTask_video_split_by_slice = function(task, onSuccess, onFailed) {
		runTask('video.split_by_slice', task, onSuccess, onFailed);
	};
	
	const logout = function(onSuccess, onFailed) {
		post(_LOGOUT_END_POINT + '?' + noCache(), onSuccess, onFailed);
	};
	
	const parseFields = function(fields) {
		return fields.split(",").map(i => i.split("&")).map(itemFields =>
			itemFields.length == 4 ? {
				"id"   	: itemFields[0].substr(itemFields[0].indexOf("=") + 1),
				"codIni": itemFields[1].substr(itemFields[1].indexOf("=") + 1),
				"hash" 	: itemFields[2].substr(itemFields[2].indexOf("=") + 1),
				"isBin" : itemFields[3].substr(itemFields[3].indexOf("=") + 1)
			}:{
				"hash"  : itemFields[0].substr(itemFields[0].indexOf("=") + 1)
			}
		);
	};
	
	PjeOffice.login = function(onSuccess, onFailed) {
    	runTask_cnj_autenticador({
    		"enviarPara": _PAGINA_LOGIN,
    		"mensagem": "TCZ3ZIsyOPf8WUPc9nfzQkXdrHoEteV1"
    	}, onSuccess, onFailed);
    };
    
    PjeOffice.logout = function(onSuccess, onFailed) {
    	logout(onSuccess, onFailed);
    };
    
    PjeOffice.signHash = function(documents, onSuccess, onFailed) {
    	runTask_cnj_assinadorHash({
    		"algoritmoAssinatura": "ASN1MD5withRSA",
    		"uploadUrl": _PAGINA_ASSINATURA,
    		"modoTeste": false,
    		"arquivos": parseFields(documents)
    	}, onSuccess, onFailed);
    };
    
    PjeOffice.signP7s = function(onSuccess, onFailed) {
    	runTask_cnj_assinador({
    		"modo": "REMOTO",
    		"tipoAssinatura": "ATTACHED",
    		"enviarPara": _PAGINA_UPLOAD,
    		"arquivos": [{
   				"nome": "arquivo",
   				"url": _PAGINA_DOWNLOAD,
   				"paramsEnvio": [_PARAM_DEJTACTION, _PARAM_MO, _PARAM_CID]
   			}]
    	}, onSuccess, onFailed);
    };
	
    return PjeOffice;
})();


