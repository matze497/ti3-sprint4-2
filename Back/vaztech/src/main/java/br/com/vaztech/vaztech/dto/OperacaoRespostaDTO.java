package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;

public record OperacaoRespostaDTO(
        Integer id,
        String numeroSerieProduto,
        BigDecimal valor,
        Integer idCliente,
        Integer idFuncionario,
        Integer tipo,
        String observacoes,
        String mensagem
) {}
