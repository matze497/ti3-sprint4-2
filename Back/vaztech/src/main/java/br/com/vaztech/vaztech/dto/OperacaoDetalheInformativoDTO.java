package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;

public record OperacaoDetalheInformativoDTO(
        Integer id,
        String numeroSerieProduto,
        String aparelhoProduto,
        String modeloProduto,
        String corProduto,
        String observacoesProduto,
        BigDecimal valor,
        Integer idCliente,
        String nomeCliente,
        String cpfCnpjCliente,
        String origemCliente,
        Integer idFuncionario,
        String nomeFuncionario,
        String codFuncionario,
        String cpfFuncionario,
        Integer tipo,
        String observacoes
) {}
