package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.ValidarFuncionarioRequestDTO;
import br.com.vaztech.vaztech.dto.ValidarFuncionarioResponseDTO;
import br.com.vaztech.vaztech.dto.OperacaoInformativoResponseDTO;
import br.com.vaztech.vaztech.dto.OperacaoDetalheInformativoDTO;
import br.com.vaztech.vaztech.dto.OperacaoCriarAtualizarDTO;
import br.com.vaztech.vaztech.dto.OperacaoRespostaDTO;
import br.com.vaztech.vaztech.dto.ErroResponseDTO;
import br.com.vaztech.vaztech.entity.Operacao;
import br.com.vaztech.vaztech.service.OperacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operacoes")
@CrossOrigin(origins = "*")
public class OperacaoController {

    private static final Logger logger = LoggerFactory.getLogger(OperacaoController.class);

    @Autowired
    private OperacaoService operacaoService;

    @PostMapping("/validar-funcionario")
    public ResponseEntity<ValidarFuncionarioResponseDTO> validarFuncionario(
            @RequestBody ValidarFuncionarioRequestDTO request) {
        
        logger.info("Recebido request para validar funcionário com código: {}", request.codigoFuncionario());
        
        try {
            ValidarFuncionarioResponseDTO response = operacaoService.validarFuncionarioParaEdicao(request);
            
            if (response.isAutorizado()) {
                logger.info("Validação bem-sucedida para funcionário: {}", request.codigoFuncionario());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Validação falhou para funcionário: {}. Motivo: {}", 
                    request.codigoFuncionario(), response.getMensagem());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Erro ao validar funcionário: {}", request.codigoFuncionario(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ValidarFuncionarioResponseDTO(
                            false,
                            "Erro interno do servidor ao validar funcionário",
                            request.codigoFuncionario()
                    ));
        }
    }

    /**
     * GET /api/operacoes/informativos
     * Retorna dados consolidados e analíticos sobre as operações
     * 
     * @param periodo Filtro opcional: "diario", "semanal", "mensal"
     * @param funcionarioId Filtro opcional: ID do funcionário
     * @param tipo Filtro opcional: tipo de operação
     * @return Informações agregadas sobre operações
     */
    @GetMapping("/informativos")
    public ResponseEntity<OperacaoInformativoResponseDTO> obterInformativos(
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "funcionarioId", required = false) Integer funcionarioId,
            @RequestParam(value = "tipo", required = false) Integer tipo) {
        
        logger.info("Recebido request para informativos - periodo: {}, funcionarioId: {}, tipo: {}", 
                periodo, funcionarioId, tipo);
        
        try {
            OperacaoInformativoResponseDTO informativos = operacaoService.obterInformativos(periodo, funcionarioId, tipo);
            logger.info("Informativos obtidos com sucesso");
            return ResponseEntity.ok(informativos);
        } catch (Exception e) {
            logger.error("Erro ao obter informativos: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/operacoes/{id}/informativo
     * Retorna informações detalhadas de uma operação específica
     * 
     * @param id ID da operação
     * @return Detalhes completos da operação com informações relacionadas
     */
    @GetMapping("/{id}/informativo")
    public ResponseEntity<OperacaoDetalheInformativoDTO> obterDetalheOperacao(@PathVariable Integer id) {
        
        logger.info("Recebido request para obter detalhe da operação com ID: {}", id);
        
        try {
            OperacaoDetalheInformativoDTO detalhe = operacaoService.obterDetalheOperacao(id);
            logger.info("Detalhe da operação obtido com sucesso");
            return ResponseEntity.ok(detalhe);
        } catch (Exception e) {
            logger.error("Erro ao obter detalhe da operação: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * POST /api/operacoes
     * Cria uma nova operação
     * 
     * @param dto DTO com dados da operação
     * @return ResponseEntity com a operação criada
     */
    @PostMapping
    public ResponseEntity<?> criarOperacao(@RequestBody OperacaoCriarAtualizarDTO dto) {
        logger.info("Recebido request para criar operação - Produto: {}, Funcionário: {}", 
                dto.numeroSerieProduto(), dto.idFuncionario());
        
        try {
            Operacao operacao = operacaoService.criarOperacao(dto);
            
            OperacaoRespostaDTO response = new OperacaoRespostaDTO(
                    operacao.getId(),
                    operacao.getProduto().getNumeroSerie(),
                    operacao.getValor(),
                    operacao.getCliente().getId(),
                    operacao.getFuncionario().getId(),
                    operacao.getTipo(),
                    operacao.getObservacoes(),
                    "Operação criada com sucesso"
            );
            
            logger.info("Operação criada com sucesso. ID: {}", operacao.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Erro ao criar operação: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponseDTO("Erro ao criar operação", e.getMessage(), 500));
        }
    }

    /**
     * PUT /api/operacoes/{id}
     * Atualiza uma operação existente
     * 
     * @param id ID da operação
     * @param dto DTO com dados atualizados
     * @return ResponseEntity com a operação atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarOperacao(@PathVariable Integer id, 
                                               @RequestBody OperacaoCriarAtualizarDTO dto) {
        logger.info("Recebido request para atualizar operação com ID: {}", id);
        
        try {
            Operacao operacao = operacaoService.atualizarOperacao(id, dto);
            
            OperacaoRespostaDTO response = new OperacaoRespostaDTO(
                    operacao.getId(),
                    operacao.getProduto().getNumeroSerie(),
                    operacao.getValor(),
                    operacao.getCliente().getId(),
                    operacao.getFuncionario().getId(),
                    operacao.getTipo(),
                    operacao.getObservacoes(),
                    "Operação atualizada com sucesso"
            );
            
            logger.info("Operação atualizada com sucesso. ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao atualizar operação: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroResponseDTO("Operação não encontrada", e.getMessage(), 404));
        }
    }

    /**
     * DELETE /api/operacoes/{id}
     * Deleta uma operação
     * 
     * @param id ID da operação
     * @return ResponseEntity com mensagem de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarOperacao(@PathVariable Integer id) {
        logger.info("Recebido request para deletar operação com ID: {}", id);
        
        try {
            operacaoService.deletarOperacao(id);
            logger.info("Operação deletada com sucesso. ID: {}", id);
            return ResponseEntity.ok(new ErroResponseDTO("Sucesso", "Operação deletada com sucesso", 200));
        } catch (Exception e) {
            logger.error("Erro ao deletar operação: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroResponseDTO("Operação não encontrada", e.getMessage(), 404));
        }
    }
}
