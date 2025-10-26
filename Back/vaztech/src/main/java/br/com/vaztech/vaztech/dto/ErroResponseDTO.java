package br.com.vaztech.vaztech.dto;

public record ErroResponseDTO(
        String titulo,
        String mensagem,
        Integer status
) {}
