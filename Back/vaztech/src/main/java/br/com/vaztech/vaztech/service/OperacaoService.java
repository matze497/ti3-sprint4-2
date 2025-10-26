package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.ValidarFuncionarioRequestDTO;
import br.com.vaztech.vaztech.dto.ValidarFuncionarioResponseDTO;
import br.com.vaztech.vaztech.dto.OperacaoInformativoResponseDTO;
import br.com.vaztech.vaztech.dto.OperacaoDetalheInformativoDTO;
import br.com.vaztech.vaztech.dto.OperacaoCriarAtualizarDTO;
import br.com.vaztech.vaztech.entity.Operacao;

public interface OperacaoService {
    ValidarFuncionarioResponseDTO validarFuncionarioParaEdicao(ValidarFuncionarioRequestDTO request);

    /**
     * Obtém informativos gerais sobre as operações com filtros opcionais
     * 
     * @param periodo pode ser: "diario", "semanal", "mensal" ou null para todas as operações
     * @param idFuncionario filtra por funcionário específico ou null para todas
     * @param tipo filtra por tipo de operação ou null para todas
     * @return Objeto com dados agregados e analíticos
     */
    OperacaoInformativoResponseDTO obterInformativos(String periodo, Integer idFuncionario, Integer tipo);

    /**
     * Obtém informações detalhadas de uma operação específica
     * 
     * @param idOperacao ID da operação
     * @return Detalhes completos da operação com informações relacionadas
     */
    OperacaoDetalheInformativoDTO obterDetalheOperacao(Integer idOperacao);

    /**
     * Cria uma nova operação
     * 
     * @param dto DTO com dados da operação
     * @return Operação criada
     */
    Operacao criarOperacao(OperacaoCriarAtualizarDTO dto);

    /**
     * Atualiza uma operação existente
     * 
     * @param id ID da operação
     * @param dto DTO com dados atualizados
     * @return Operação atualizada
     */
    Operacao atualizarOperacao(Integer id, OperacaoCriarAtualizarDTO dto);

    /**
     * Deleta uma operação
     * 
     * @param id ID da operação
     */
    void deletarOperacao(Integer id);
}
