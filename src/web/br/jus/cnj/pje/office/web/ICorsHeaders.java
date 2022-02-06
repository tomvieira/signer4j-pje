package br.jus.cnj.pje.office.web;

import org.apache.hc.core5.http.HttpHeaders;

public interface ICorsHeaders {
  String ACCESS_CONTROL_ALLOW_CREDENTIALS       = HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
  String ACCESS_CONTROL_ALLOW_HEADERS           = HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
  String ACCESS_CONTROL_ALLOW_METHODS           = HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
  String ACCESS_CONTROL_ALLOW_ORIGIN            = HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
  String ACCESS_CONTROL_EXPOSE_HEADERS          = HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
  String ACCESS_CONTROL_MAX_AGE                 = HttpHeaders.ACCESS_CONTROL_MAX_AGE;
  String ACCESS_CONTROL_REQUEST_HEADERS         = HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
  String ACCESS_CONTROL_REQUEST_METHOD          = HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
  String ACCESS_CONTROL_REQUEST_PRIVATE_NETWORK = "Access-Control-Request-Private-Network";
  String ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK   = "Access-Control-Allow-Private-Network";
}
