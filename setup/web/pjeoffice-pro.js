/********************************************************************************************************
/* A LIGHTWEIGHT XMLHttpRequest LIBRARY 
/*******************************************************************************************************/

const PjeClient=function(){function t(){}t.ajax=function(t){t=t||{url:""},t.type=t.type&&t.type.toUpperCase()||"GET",t.headers=t.headers||{},t.timeout=parseInt(t.timeout)||0,t.success=t.success||function(){},t.error=t.error||function(){},t.async="undefined"==typeof t.async?!0:t.async;var e=new XMLHttpRequest;t.timeout>0&&(e.timeout=t.timeout,e.ontimeout=function(){t.error("timeout","timeout",e)}),e.open(t.type,t.url,t.async);for(var s in t.headers)t.headers.hasOwnProperty(s)&&e.setRequestHeader(s,t.headers[s]);return e.send(t.data),e.onreadystatechange=function(){if(4==this.readyState&&(this.status>=200&&this.status<300||304==this.status)){var e=this.responseText,s=this.getResponseHeader("Content-Type");s&&s.match(/json/)&&(e=JSON.parse(this.responseText)),t.success(e,this.statusText,this)}else 4==this.readyState&&t.error(this.status,this.statusText,this)},0==t.async&&(4==e.readyState&&(e.status>=200&&e.status<300||304==e.status)?t.success(e.responseText,e):4==e.readyState&&t.error(e.status,e.statusText,e)),e};var e=function(e,s,n,r){return"function"==typeof n&&(r=n,n=void 0),t.ajax({url:s,data:n,type:e,success:r})};return t.get=function(t,s,n){return e("GET",t,s,n)},t.head=function(t,s,n){return e("HEAD",t,s,n)},t.post=function(t,s,n){return e("POST",t,s,n)},t.patch=function(t,s,n){return e("PATCH",t,s,n)},t.put=function(t,s,n){return e("PUT",t,s,n)},t["delete"]=function(t,s,n){return e("DELETE",t,s,n)},t.options=function(t,s,n){return e("OPTIONS",t,s,n)},t}();__=PjeClient;


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
* A constante 'defaultSubject' é a configuração 'global' padrão usada pela instância PjeOffice. Alternativamente 
* você pode remover este código daqui e definir esta instância na sua aplicação com todos os atributos e colocá-la 
* no escopo global javascript. 
/*****************************************************************************************************************/

const defaultSubject = {
   //Aplicação que faz uso do PjeOffice
   //Este parâmetro é OBRIGATÓRIO e deveria ser entendido como uma constante por toda aplicação 
  "APP_REQUISITANTE"  : "Pje",

  //Código de segurança da aplicação - proteção CSRF fornecido pelo CNJ
  //Este parâmetro é OBRIGATÓRIO e deveria ser entendido como uma constante por toda aplicação 
  "CODIGO_SEGURANCA"  : "bypass",

  //Endpoint raiz da aplicação. Comporá o parâmetro 'servidor' da requisição principal (main) e usada
  //para validação conjunta com CODIGO_SEGURANÇA e cabeçalho 'ORIGIN' em proteções CSRF.
  //Este parâmetro é OBRIGATÓRIO e deveria ser entendido como uma constante por toda aplicação 
  
  //OBS: Troque este final /pjeOffice pelo contexto da aplicação do servidor pje. Aqui é informado /pjeOffice
  //porque o próprio PjeOffice simula em Mock a aplicação web para demonstração da api em http://127.0.0.1:8800/pjeOffice/api "
  "WEB_ROOT"          : window.location.origin + "/pjeOffice",

  //Para ambientes de testes, desenvolvimento, treinamento e cia informe MODO_TESTE=true. Informe false para produção
  //Este parâmetro evita enviar hash's assinados em ambientes de treinamento ou similares
  //Este parâmetro é OBRIGATÓRIO e deveria ser entendido como uma constante por toda aplicação 
  "MODO_TESTE"        : false,

  //O timeout das requisições POST entre o navegador e o PjeOffice PRO.
  //Este parâmetro é OBRIGATÓRIO e PODE SER SOBRESCRITO dinamicamente em tempo de chamada API  
  "POST_TIMEOUT"      : 600000, //milliseconds (10 minutes)
  
  //Página para redirecionamento pós login
  //Este parâmetro é OBRIGATÓRIO e PODE SER SOBRESCRITO dinamicamente em tempo de chamada API
  "PAGINA_LOGIN"      : "/pjefake",
        
  //Página que receberá assinaturas de hash's
  //Este parâmetro é OBRIGATÓRIO e PODE SER SOBRESCRITO dinamicamente em tempo de chamada API
  "PAGINA_ASSINATURA" : "/pjefake",
        
  //Página que recebe arquivos assinados em P7S
  //Este parâmetro é OBRIGATÓRIO e PODE SER SOBRESCRITO dinamicamente em tempo de chamada API.
  "PAGINA_UPLOAD"     : "/pjefake",
};


/********************************************************************************************************
* Instância PjeOffice disponível para toda a aplicação  
/*******************************************************************************************************/

const PjeOffice = (function () {
    
  function PjeOffice() {}

  //Please do NOT change this constants if you don't know what you really doing
  const PJEOFFICE_PROTOCOL         = "http";
  const PJEOFFICE_PORT             = 8800;
  const PJEOFFICE_BASE_ENDPOINT    = PJEOFFICE_PROTOCOL + "://127.0.0.1:" + PJEOFFICE_PORT;
  const PJEOFFICE_BASE_CONTEXT     = "/pjeOffice/";
  const PJEOFFICE_TASK_ENDPOINT    = PJEOFFICE_BASE_CONTEXT + "requisicao/";
  const PJEOFFICE_LOGOUT_ENDPOINT  = PJEOFFICE_BASE_CONTEXT + "logout/";
  const PJEOFFICE_VERSION_ENDPOINT = PJEOFFICE_BASE_CONTEXT + "versao/"
  const PJEOFFICE_TIMEOUT_ALERT    = 'Alcançado tempo máximo de espera por resposta do PjeOffice PRO (timeout)';     

  const noCache = function() {
    return '&u=' + new Date().getTime();
  };
  
  const createQueryParams = function(taskId, task) {
    return PJEOFFICE_TASK_ENDPOINT + "?r=" + encodeURIComponent(readJson({
      "sessao": readSession(),
      "aplicacao": defaultSubject.APP_REQUISITANTE,
      "servidor": defaultSubject.WEB_ROOT,
      "codigoSeguranca": defaultSubject.CODIGO_SEGURANCA,
      "tarefaId": taskId,
      "tarefa": readJson(task)  
    })) + noCache();
  };

  const post = function (endPoint, apiContext) {
    const subject       = apiContext?.subject;
    const onSuccess     = apiContext?.onSuccess;
    const onFailed      = apiContext?.onFailed;
    const onUnavailable = apiContext?.onUnavailable;
    let complete = false;
    PjeClient.ajax({
      "url": PJEOFFICE_BASE_ENDPOINT + endPoint,
      "type": 'POST',
      "headers": { "Content-Type": 'application/x-www-form-urlencoded'},
      "timeout": subject?.POST_TIMEOUT || defaultSubject.POST_TIMEOUT,
      "async": true,
      "error": function(status, statusText, response) {
        if ('timeout' === status)
          alert(PJEOFFICE_TIMEOUT_ALERT);    
        if (complete) 
          return;
        complete = true;
        if (onUnavailable)
          onUnavailable(statusText, response); 
      },
      "success": function(data, statusText, response) {
        if (complete)
          return;
        complete = true;
        if (data.success) { //json response must return's 'success' attribute, ever!
          if (onSuccess)
            onSuccess(data, response);
        } else {
          if (onFailed)
            onFailed(statusText, response);  
        }
      },
    });
  };
  
  const runTask = function(taskId, task, apiContext) {
    post(createQueryParams(taskId, task), apiContext);
  };
  
  PjeOffice.logout = function(apiContext) {
    post(PJEOFFICE_LOGOUT_ENDPOINT + '?' + noCache(), apiContext);
  };

  PjeOffice.ping = function(apiContext) {
	post(PJEOFFICE_BASE_CONTEXT + '?' + noCache(), apiContext);
  };
  
  PjeOffice.about = function(apiContext) {
	post(PJEOFFICE_VERSION_ENDPOINT + '?' + noCache(), apiContext);
  }

  PjeOffice.login = function(welcomeMessage, apiContext) {
    runTask('cnj.autenticador', {
      "enviarPara": apiContext?.subject?.PAGINA_LOGIN || defaultSubject.PAGINA_LOGIN,
      "mensagem": welcomeMessage,
    }, apiContext);
  };

  PjeOffice.loginSSO = function(welcomeMessage, token, apiContext) {
    runTask('sso.autenticador', {
      "enviarPara": apiContext?.subject?.PAGINA_LOGIN || defaultSubject.PAGINA_LOGIN,
      "mensagem": welcomeMessage,
      "token": token
    }, apiContext);  
  };
    
  PjeOffice.signHash = function(documents, apiContext) {
    runTask('cnj.assinadorHash', {
      "algoritmoAssinatura": "ASN1MD5withRSA",
      "uploadUrl": apiContext?.subject?.PAGINA_ASSINATURA || defaultSubject.PAGINA_ASSINATURA,
      "modoTeste": apiContext?.subject?.MODO_TESTE || defaultSubject.MODO_TESTE,
      "arquivos": documents
    }, apiContext);
  };

  PjeOffice.signRemote = function(documents, apiContext) {
    runTask('cnj.assinador', {
      "modo": "REMOTO",
      "tipoAssinatura": "ATTACHED",
      "enviarPara": apiContext?.subject?.PAGINA_UPLOAD || defaultSubject.PAGINA_UPLOAD,
      "arquivos": documents
    }, apiContext);
  };

  PjeOffice.signBase64 = function(documents, apiContext) {
    runTask('cnj.assinadorBase64', {
      "algoritmoAssinatura":"ASN1MD5withRSA",
      "uploadUrl": apiContext?.subject?.PAGINA_UPLOAD || defaultSubject.PAGINA_UPLOAD,
      "arquivos": documents
    }, apiContext);  
  };

  PjeOffice.printTag = function(printerPort, content, apiContext) {
    runTask('util.impressor', {
      "porta": printerPort,
      "conteudo": content
    }, apiContext);
  };

  return PjeOffice;
})();
