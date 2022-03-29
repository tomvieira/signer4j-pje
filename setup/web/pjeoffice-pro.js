/********************************************************************************************************
/* A LIGHTWEIGHT XMLHttpRequest LIBRARY 
/*******************************************************************************************************/

const Twix=function(){function t(){}t.ajax=function(t){t=t||{url:""},t.type=t.type&&t.type.toUpperCase()||"GET",t.headers=t.headers||{},t.timeout=parseInt(t.timeout)||0,t.success=t.success||function(){},t.error=t.error||function(){},t.async="undefined"==typeof t.async?!0:t.async;var e=new XMLHttpRequest;t.timeout>0&&(e.timeout=t.timeout,e.ontimeout=function(){t.error("timeout","timeout",e)}),e.open(t.type,t.url,t.async);for(var s in t.headers)t.headers.hasOwnProperty(s)&&e.setRequestHeader(s,t.headers[s]);return e.send(t.data),e.onreadystatechange=function(){if(4==this.readyState&&(this.status>=200&&this.status<300||304==this.status)){var e=this.responseText,s=this.getResponseHeader("Content-Type");s&&s.match(/json/)&&(e=JSON.parse(this.responseText)),t.success(e,this.statusText,this)}else 4==this.readyState&&t.error(this.status,this.statusText,this)},0==t.async&&(4==e.readyState&&(e.status>=200&&e.status<300||304==e.status)?t.success(e.responseText,e):4==e.readyState&&t.error(e.status,e.statusText,e)),e};var e=function(e,s,n,r){return"function"==typeof n&&(r=n,n=void 0),t.ajax({url:s,data:n,type:e,success:r})};return t.get=function(t,s,n){return e("GET",t,s,n)},t.head=function(t,s,n){return e("HEAD",t,s,n)},t.post=function(t,s,n){return e("POST",t,s,n)},t.patch=function(t,s,n){return e("PATCH",t,s,n)},t.put=function(t,s,n){return e("PUT",t,s,n)},t["delete"]=function(t,s,n){return e("DELETE",t,s,n)},t.options=function(t,s,n){return e("OPTIONS",t,s,n)},t}();__=Twix;


/********************************************************************************************************
/* UTILITIES FUNCTIONS
/*******************************************************************************************************/

const readJson = function(json) {
  const jsonStringify = JSON.stringify;  
    const arrayToJson = Array.prototype.toJSON;
    delete Array.prototype.toJSON;
    const output = jsonStringify(json);
    Array.prototype.toJSON = arrayToJson;
    return output;
};

const readCookie = function (cookieName) {
  const cookiePrefix = cookieName + "=";
  const decodedCookies = decodeURIComponent(document.cookie);
  const cookieItems = decodedCookies.split(';');
  for(let i = 0; i < cookieItems.length; i++) {
    let cookie = cookieItems[i];
    while (cookie.charAt(0) == ' ') {
      cookie = cookie.substring(1);
    }
    if (cookie.indexOf(cookiePrefix) == 0) {
      return cookie;
    }
  }
  return "";
};

const readSession = function () {
  return document.cookie; // return readCookie('JSESSIONID'); 
};



/********************************************************************************************************
* This is your PjeOffice user instance representation. Alternativaly you can remove this code from here, 
* create this object dynamicaly in your application with all attributes and put it in global javascript
* scope
/*******************************************************************************************************/

const pjeofficeUser = {
  "APP_REQUISITANTE"	: "Pje",             		//(Aplicação que faz uso do PjeOffice)
  "CODIGO_SEGURANCA"	: "bypass",			 		//(código de segurança da aplicação - proteção CSRF)
  "WEB_ROOT"			: window.location.origin + "/pjeOffice",// + "insert your context web root path here",
  "WELCOME_MESSAGE"		: "helloworld",		 		//(mensagem para assinar durante autenticação)
  "PAGINA_LOGIN"		: "/pjefake",     			//(página para redirecionamento pós login)
  "PAGINA_ASSINATURA"	: "/pjefake",				//(página que receberá assinaturas)
  "PAGINA_UPLOAD"		: "/pjefake",				//(página quer recebe arquivos assinados em P7S)
  "PAGINA_DOWNLOAD"		: "/pjefake",				//(página que entrega os arquivos a serem assinados em P7S)
  "PARAMS_ENVIO"		: ["foo=bar", "what=ever"] 	//(parâmetros adicionais a serem enviados juntamente com arquivo remoto baixado e assinado em P7S)
};



/********************************************************************************************************
* PJEOFFICE INSTANCE AVAILABLE FOR ALL APPLICATION  
/*******************************************************************************************************/

const PjeOffice = (function () {
    
  function PjeOffice() {}

  const PJEOFFICE_PROTOCOL		   = "http";
  const PJEOFFICE_PORT			   = 8800;
  const PJEOFFICE_POST_TIMEOUT     = 15000; //millis
  const PJEOFFICE_BASE_END_POINT   = PJEOFFICE_PROTOCOL + "://127.0.0.1:" + PJEOFFICE_PORT;
  const PJEOFFICE_BASE_CONTEXT     = "/pjeOffice/";
  const PJEOFFICE_TASK_END_POINT   = PJEOFFICE_BASE_CONTEXT + "requisicao/"
  const PJEOFFICE_LOGOUT_END_POINT = PJEOFFICE_BASE_CONTEXT + "logout/"
     
  const noCache = function() {
    return '&u=' + new Date().getTime();
  };
  
  const createQueryParams = function(taskId, task) {
    return PJEOFFICE_TASK_END_POINT + "?r=" + encodeURIComponent(readJson({
      "sessao": readSession(),
      "aplicacao": pjeofficeUser.APP_REQUISITANTE,
      "servidor": pjeofficeUser.WEB_ROOT,
      "codigoSeguranca": pjeofficeUser.CODIGO_SEGURANCA,
      "tarefaId": taskId,
      "tarefa": readJson(task)  
    })) + noCache();
  };
    
  const post = function (endPoint, onSuccess, onFailed) {
    Twix.ajax({
      "url": PJEOFFICE_BASE_END_POINT + endPoint,
      "type": 'POST',
      "headers": { "Content-Type": 'application/x-www-form-urlencoded'},
      "timeout": PJEOFFICE_POST_TIMEOUT,
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
    post(PJEOFFICE_LOGOUT_END_POINT + '?' + noCache(), onSuccess, onFailed);
  };
  
  const parseFields = function(fields) {
    return fields.split(",").map(i => i.split("&")).map(itemFields =>
      itemFields.length == 4 ? {
        "id"	: itemFields[0].substr(itemFields[0].indexOf("=") + 1),
        "codIni": itemFields[1].substr(itemFields[1].indexOf("=") + 1),
        "hash"  : itemFields[2].substr(itemFields[2].indexOf("=") + 1),
        "isBin" : itemFields[3].substr(itemFields[3].indexOf("=") + 1)
      }:{
        "hash"  : itemFields[0].substr(itemFields[0].indexOf("=") + 1)
      }
    );
  };

/********************************************************************************************************
* PJEOFFICE API  
/*******************************************************************************************************/
  
  PjeOffice.login = function(onSuccess, onFailed) {
    runTask_cnj_autenticador({
      "enviarPara": pjeofficeUser.PAGINA_LOGIN,
      "mensagem": pjeofficeUser.WELCOME_MESSAGE,
    }, onSuccess, onFailed);
  };

  PjeOffice.loginSSO = function(token, onSuccess, onFailed) {
	runTask_sso_autenticador({
      "enviarPara": pjeofficeUser.PAGINA_LOGIN,
      "mensagem": pjeofficeUser.WELCOME_MESSAGE,
      "token": token
	}, onSuccess, onFailed);	
  };
    
  PjeOffice.logout = function(onSuccess, onFailed) {
    logout(onSuccess, onFailed);
  };
    
  PjeOffice.signHash = function(documents, onSuccess, onFailed) {
    runTask_cnj_assinadorHash({
      "algoritmoAssinatura": "ASN1MD5withRSA",
      "uploadUrl": pjeofficeUser.PAGINA_ASSINATURA,
      "modoTeste": false,
      "arquivos": parseFields(documents)
    }, onSuccess, onFailed);
  };
    
  PjeOffice.signP7s = function(onSuccess, onFailed) {
    runTask_cnj_assinador({
      "modo": "REMOTO",
      "tipoAssinatura": "ATTACHED",
      "enviarPara": pjeofficeUser.PAGINA_UPLOAD,
      "arquivos": [{
         "nome": "arquivo",
         "url": pjeofficeUser.PAGINA_DOWNLOAD,
         "paramsEnvio": pjeofficeUser.PARAMS_ENVIO
       }]
    }, onSuccess, onFailed);
  };
  
  return PjeOffice;
})();
