package com.example.eleicao_service.core.handler.exception;

public class NaoAutenticadoException extends RuntimeException {
    public NaoAutenticadoException(String message) { super(message); }
}