package org.tcontrol.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.tcontrol.service.SensorServiceException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"org.tcontrol"})
public class ControllerAdvice implements ResponseBodyAdvice<Object> {

    public static final String OK = "200";
    public static final String CREATED = "201";
    public static final String NO_CONTENT = "204";
    public static final String NOT_FOUND = "404";
    public static final String CLIENT_ERROR = "403";
    public static final String BAD_REQUEST = "400";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String UNPROCESSABLE_ENTITY = "422";
    public static final String CONFLICT = "409";
    public static final String SERVICE_UNAVAILABLE = "503";

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        MDC.put("messageType", "RESPONSE");
        if (serverHttpResponse instanceof ServletServerHttpResponse) {
            int status = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse().getStatus();
            log.info("Ответ на запрос status={}", status);


        }
        MDC.put("messageType", "MESSAGE");
        return body;
    }

    /**
     * Документирование ответа NOT_FOUND
     *
     * @param ex - ошибка
     * @return - возвращаемый ответ
     */
    @ExceptionHandler({SensorServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleNotFoundException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleIllegalArgumentException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

}