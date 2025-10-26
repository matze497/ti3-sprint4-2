package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record OperacaoInformativoResponseDTO(
        Integer totalOperacoes,
        BigDecimal valorTotalMovimentado,
        Map<String, Integer> operacoesPorTipo,
        Map<String, Integer> operacoesPorFuncionario,
        List<OperacaoResumoDTO> ultimasOperacoes,
        Map<String, Integer> produtosMaisMovimentados
) {
    public record OperacaoResumoDTO(
            Integer id,
            String numeroSerieProduto,
            String aparelhoProduto,
            String modeloProduto,
            BigDecimal valor,
            String nomeCliente,
            String nomeFuncionario,
            Integer tipo,
            String observacoes
    ) {}
}
