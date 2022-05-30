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


/*****************************************************************************************************************
* Esta é a representação  da sua instância PjeOffice. Alternativamente você pode remover este código daqui,
* criar este objeto dinamicamente na sua aplicação com todos os atributos e colocá-la no escopo global javascript. 
/*****************************************************************************************************************/

const defaultSubject = {
   //Aplicação que faz uso do PjeOffice
   //Este parâmetro é OBRIGATÓRIO e NÃO DEVERIA ser sobrescrito dinamicamente em tempo de chamada API
  "APP_REQUISITANTE"  : "Pje",

  //Código de segurança da aplicação - proteção CSRF
  //Este parâmetro é OBRIGATÓRIO e NÃO DEVERIA ser sobrescrito dinamicamente em tempo de chamada API
  "CODIGO_SEGURANCA"  : "bypass",

  //Endpoint raiz da aplicação. Comporá o parâmetro 'servidor' da requisição principal (main) e usada
  //para validação conjunta com CODIGO_SEGURANÇA e cabeçalho 'ORIGIN' em proteções CSRF.
  //Este parâmetro é OBRIGATÓRIO e NÃO DEVERIA ser sobrescrito dinamicamente em tempo de chamada API
  //OBS: Troque este final /pjeOffice pelo contexto da aplicação do servidor pje. Aqui é informado /pjeOffice
  //porque o próprio PjeOffice simula em Mock a aplicação web para demonstração da api em http://127.0.0.1:8800/pjeOffice/api ")
  "WEB_ROOT"          : window.location.origin + "/pjeOffice",

  //Para ambientes de testes, desenvolvimento, treinamento e cia informe MODO_TESTE=true. Informe false para produção
  //Este parâmetro é OBRIGATÓRIO e NÃO DEVERIA ser sobrescrito dinamicamente em tempo de chamada API.
  "MODO_TESTE"        : false,             

  //O timeout das requisições POST entre o navegador e o PjeOffice. 
  //Este parâmetro é OBRIGATÓRIO e PODE ser sobrescrito dinamicamente em tempo de chamada API. 
  "POST_TIMEOUT"    : 600000, //milliseconds (10 minutes)
  
  //Página para redirecionamento pós login
  //Este parâmetro é OBRIGATÓRIO e PODE ser sobrescrito dinamicamente em tempo de chamada API.
  "PAGINA_LOGIN"      : "/pjefake",
        
  //Página que receberá assinaturas
  //Este parâmetro é OBRIGATÓRIO e PODE ser sobrescrito dinamicamente em tempo de chamada API.
  "PAGINA_ASSINATURA" : "/pjefake",
        
  //Página que recebe arquivos assinados em P7S
  //Este parâmetro é OBRIGATÓRIO e PODE ser sobrescrito dinamicamente em tempo de chamada API.
  "PAGINA_UPLOAD"     : "/pjefake",        

  //Página que entrega os arquivos a serem assinados em P7S
  //Este parâmetro é OBRIGATÓRIO e PODE ser sobrescrito dinamicamente em tempo de chamada API.  
  "PAGINA_DOWNLOAD"   : "/pjefake",

  //Parâmetros adicionais a serem enviados juntamente com arquivo remoto baixado e assinado em P7S
  //Este parâmetro é OBRIGATÓRIO e PODE ser sobrescrito dinamicamente em tempo de chamada API.
  "PARAMS_ENVIO"      : ["foo=bar", "what=ever"]   
};


/********************************************************************************************************
* PJEOFFICE INSTANCE AVAILABLE FOR ALL APPLICATION  
/*******************************************************************************************************/

const PjeOffice = (function () {
    
  function PjeOffice() {}

  const PJEOFFICE_PROTOCOL         = "http";
  const PJEOFFICE_PORT             = 8800;

  //Please do NOT change this constants if you don't know what you really doing
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
      "aplicacao": defaultSubject.APP_REQUISITANTE,
      "servidor": defaultSubject.WEB_ROOT,
      "codigoSeguranca": defaultSubject.CODIGO_SEGURANCA,
      "tarefaId": taskId,
      "tarefa": readJson(task)  
    })) + noCache();
  };
  
  const post = function (subject, endPoint, onSuccess, onFailed) {
  let complete = false;
    Twix.ajax({
    "url": PJEOFFICE_BASE_END_POINT + endPoint,
        "type": 'POST',
        "headers": { "Content-Type": 'application/x-www-form-urlencoded'},
        "timeout": subject?.POST_TIMEOUT || defaultSubject.POST_TIMEOUT,
        "async": true,
        "error": function(status, statusText, response) {
      if ('timeout' === status)
        alert('Alcançado tempo máximo de espera por resposta do PjeOffice (timeout)');    
       if (complete) 
        return;  
       if (onFailed)
        onFailed(statusText, response); 
       complete = true;
    },
        "success": function(data, statusText, response) {
        if (complete)
        return;
         if (data.success) { //json response must return 'success' attribute!
        if (onSuccess)
          onSuccess(data, response);
       } else {
           if (onFailed)
          onFailed(statusText, response);  
       }
       complete = true;
        },
  });
  };
  
  const runTask = function(subject, taskId, task, onSuccess, onFailed) {
    post(subject, createQueryParams(taskId, task), onSuccess, onFailed);
  };
  
  const logout = function(subject, onSuccess, onFailed) {
    post(subject, PJEOFFICE_LOGOUT_END_POINT + '?' + noCache(), onSuccess, onFailed);
  };
  
  const parseFields = function(fields) {
    return fields.split(",").map(i => i.split("&")).map(itemFields =>
      itemFields.length == 4 ? {
        "id"  : itemFields[0].substr(itemFields[0].indexOf("=") + 1),
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
  /*
  Estrutura da instância apiContext
  apiContext = {
  "subject": {
    "MODO_TESTE"        : false,               //Opcional: se não informado será considerado defaultSubject.MODO_TESTE             
    "POST_TIMEOUT"    : 300000,                //Opcional: se não informado será considerado defaultSubject.POST_TIMEOUT   
    "PAGINA_LOGIN"      : "/pjefake",          //Opcional: se não informado será considerado defaultSubject.PAGINA_LOGIN  
    "PAGINA_ASSINATURA" : "/pjefake",          //Opcional: se não informado será considerado defaultSubject.PAGINA_ASSINATURA
    "PAGINA_UPLOAD"     : "/pjefake",          //Opcional: se não informado será considerado defaultSubject.PAGINA_UPLOAD
    "PAGINA_DOWNLOAD"   : "/pjefake",          //Opcional: se não informado será considerado defaultSubject.PAGINA_DOWNLOAD
    "PARAMS_ENVIO"      : ["foo=bar", "what=ever"]  //Opcional: se não informado será considerado defaultSubject.PARAMS_ENVIO
  },
  "onSuccess": function(data, response) {},      //Opcional: se não informada a notificação é ignorada
  "onFailed": function(statusText, response) {}       //Opcional: se não informada a notificação é ignorada
  };
  */
  
  PjeOffice.login = function(welcomeMessage, apiContext) {
    runTask(apiContext?.subject, 'cnj.autenticador', {
      "enviarPara": apiContext?.subject?.PAGINA_LOGIN || defaultSubject.PAGINA_LOGIN,
      "mensagem": welcomeMessage,
    }, apiContext?.onSuccess, apiContext?.onFailed);
  };

  PjeOffice.loginSSO = function(welcomeMessage, token, apiContext) {
    runTask(apiContext?.subject, 'sso.autenticador', {
      "enviarPara": apiContext?.subject?.PAGINA_LOGIN || defaultSubject.PAGINA_LOGIN,
      "mensagem": welcomeMessage,
      "token": token
    }, apiContext?.onSuccess, apiContext?.onFailed);  
  };
    
  PjeOffice.logout = function(apiContext) {
    logout(apiContext?.subject, apiContext?.onSuccess, apiContext?.onFailed);
  };
    
  PjeOffice.signHash = function(documents, apiContext) {
    runTask(apiContext?.subject, 'cnj.assinadorHash', {
      "algoritmoAssinatura": "ASN1MD5withRSA",
      "uploadUrl": apiContext?.subject?.PAGINA_ASSINATURA || defaultSubject.PAGINA_ASSINATURA,
      "modoTeste": apiContext?.subject?.MODO_TESTE || defaultSubject.MODO_TESTE,
      "arquivos": parseFields(documents)
    }, apiContext?.onSuccess, apiContext?.onFailed);
  };
    
  PjeOffice.signRemoteP7s = function(apiContext) {
    runTask(apiContext?.subject, 'cnj.assinador', {
      "modo": "REMOTO",
      "tipoAssinatura": "ATTACHED",
      "enviarPara": apiContext?.subject?.PAGINA_UPLOAD || defaultSubject.PAGINA_UPLOAD,
      "arquivos": [{
         "nome": "arquivo",
         "url": apiContext?.subject?.PAGINA_DOWNLOAD || defaultSubject.PAGINA_DOWNLOAD,
         "paramsEnvio": apiContext?.subject?.PARAMS_ENVIO || defaultSubject.PARAMS_ENVIO
       }]
    }, apiContext?.onSuccess, apiContext?.onFailed);
  };

  /*
  PjeOffice.signBase64 = function(documents, apiContext) {
    runTask(apiContext?.subject, 'cnj.assinadorBase64', {
      "algoritmoAssinatura":"ASN1MD5withRSA",
      "uploadUrl": apiContext?.subject?.PAGINA_UPLOAD || defaultSubject.PAGINA_UPLOAD,
        "arquivos": [{
         "hashDoc": "",
         "conteudoBase64": ""
      }]
    }, apiContext?.onSuccess, apiContext?.onFailed);  
  };
  */

  return PjeOffice;
})();
