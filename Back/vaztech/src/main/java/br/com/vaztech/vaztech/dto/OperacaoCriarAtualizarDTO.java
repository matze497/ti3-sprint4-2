package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;

public record OperacaoCriarAtualizarDTO(
        String numeroSerieProduto,
        BigDecimal valor,
        Integer idCliente,
        Integer idFuncionario,
        Integer tipo,
        String observacoes
) {}
