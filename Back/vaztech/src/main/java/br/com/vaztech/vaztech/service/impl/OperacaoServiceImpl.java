package br.com.vaztech.vaztech.service.impl;

import br.com.vaztech.vaztech.dto.ValidarFuncionarioRequestDTO;
import br.com.vaztech.vaztech.dto.ValidarFuncionarioResponseDTO;
import br.com.vaztech.vaztech.dto.OperacaoInformativoResponseDTO;
import br.com.vaztech.vaztech.dto.OperacaoDetalheInformativoDTO;
import br.com.vaztech.vaztech.dto.OperacaoInformativoResponseDTO.OperacaoResumoDTO;
import br.com.vaztech.vaztech.dto.OperacaoCriarAtualizarDTO;
import br.com.vaztech.vaztech.entity.Operacao;
import br.com.vaztech.vaztech.entity.Funcionario;
import br.com.vaztech.vaztech.entity.Produto;
import br.com.vaztech.vaztech.entity.Pessoa;
import br.com.vaztech.vaztech.repository.FuncionarioRepository;
import br.com.vaztech.vaztech.repository.OperacaoRepository;
import br.com.vaztech.vaztech.service.OperacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OperacaoServiceImpl implements OperacaoService {

    private static final Logger logger = LoggerFactory.getLogger(OperacaoServiceImpl.class);
    private static final int QUANTIDADE_ULTIMAS = 5;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private OperacaoRepository operacaoRepository;

    @Override
    public ValidarFuncionarioResponseDTO validarFuncionarioParaEdicao(ValidarFuncionarioRequestDTO request) {
        String codigoFuncionario = request.codigoFuncionario();
        
        logger.info("Iniciando validação de funcionário com código: {}", codigoFuncionario);
        
        // Validar se o código foi fornecido
        if (codigoFuncionario == null || codigoFuncionario.trim().isEmpty()) {
            logger.warn("Tentativa de validação com código de funcionário vazio");
            return new ValidarFuncionarioResponseDTO(
                false,
                "Código de funcionário não pode estar vazio",
                codigoFuncionario
            );
        }
        
        // Buscar funcionário ativo
        Optional<Funcionario> funcionario = funcionarioRepository.findByCodFuncionarioAndAtivo(codigoFuncionario.trim());
        
        if (funcionario.isEmpty()) {
            // Buscar sem verificar status para determinar o motivo
            Optional<Funcionario> funcionarioInativo = funcionarioRepository.findByCodFuncionario(codigoFuncionario.trim());
            
            if (funcionarioInativo.isEmpty()) {
                logger.warn("Funcionário não encontrado com código: {}", codigoFuncionario);
                return new ValidarFuncionarioResponseDTO(
                    false,
                    "Funcionário não encontrado no sistema",
                    codigoFuncionario
                );
            } else {
                logger.warn("Funcionário inativo tentou autorizar edição. Código: {}", codigoFuncionario);
                return new ValidarFuncionarioResponseDTO(
                    false,
                    "Funcionário inativo. Não tem permissão para autorizar edições",
                    codigoFuncionario
                );
            }
        }
        
        // Funcionário válido e ativo
        Funcionario func = funcionario.get();
        logger.info("Funcionário validado com sucesso. ID: {}, Nome: {}", func.getId(), func.getNome());
        
        return new ValidarFuncionarioResponseDTO(
            true,
            "Funcionário autorizado para editar operações",
            codigoFuncionario
        );
    }

    @Override
    public OperacaoInformativoResponseDTO obterInformativos(String periodo, Integer idFuncionario, Integer tipo) {
        logger.info("Obtendo informativos - periodo: {}, idFuncionario: {}, tipo: {}", periodo, idFuncionario, tipo);
        
        List<Operacao> operacoes = obterOperacoesFiltradas(periodo, idFuncionario, tipo);
        
        // Calcular métricas
        Integer totalOperacoes = operacoes.size();
        BigDecimal valorTotalMovimentado = operacoes.stream()
                .map(Operacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Agrupar por tipo
        Map<String, Integer> operacoesPorTipo = agruparPorTipo(operacoes);

        // Agrupar por funcionário
        Map<String, Integer> operacoesPorFuncionario = agruparPorFuncionario(operacoes);

        // Últimas operações
        List<OperacaoResumoDTO> ultimasOperacoes = operacoes.stream()
                .limit(QUANTIDADE_ULTIMAS)
                .map(this::converterParaResumo)
                .collect(Collectors.toList());

        // Produtos mais movimentados
        Map<String, Integer> produtosMaisMovimentados = agruparProdutosMaisMovimentados(operacoes);

        logger.info("Informativos obtidos com sucesso. Total: {}", totalOperacoes);

        return new OperacaoInformativoResponseDTO(
                totalOperacoes,
                valorTotalMovimentado,
                operacoesPorTipo,
                operacoesPorFuncionario,
                ultimasOperacoes,
                produtosMaisMovimentados
        );
    }

    @Override
    public OperacaoDetalheInformativoDTO obterDetalheOperacao(Integer idOperacao) {
        logger.info("Obtendo detalhe da operação com ID: {}", idOperacao);

        Optional<Operacao> operacao = operacaoRepository.findById(idOperacao);

        if (operacao.isEmpty()) {
            logger.warn("Operação não encontrada com ID: {}", idOperacao);
            throw new RuntimeException("Operação não encontrada");
        }

        Operacao op = operacao.get();

        OperacaoDetalheInformativoDTO dto = new OperacaoDetalheInformativoDTO(
                op.getId(),
                op.getProduto().getNumeroSerie(),
                op.getProduto().getAparelho(),
                op.getProduto().getModelo(),
                op.getProduto().getCor(),
                op.getProduto().getObservacoes(),
                op.getValor(),
                op.getCliente().getId(),
                op.getCliente().getNome(),
                op.getCliente().getCpfCnpj(),
                op.getCliente().getOrigem(),
                op.getFuncionario().getId(),
                op.getFuncionario().getNome(),
                op.getFuncionario().getCodFuncionario(),
                op.getFuncionario().getCpf(),
                op.getTipo(),
                op.getObservacoes()
        );

        logger.info("Detalhe da operação obtido com sucesso. ID: {}", idOperacao);

        return dto;
    }

    // ============= Métodos auxiliares privados =============

    /**
     * Obtém operações filtradas baseado nos critérios fornecidos
     */
    private List<Operacao> obterOperacoesFiltradas(String periodo, Integer idFuncionario, Integer tipo) {
        List<Operacao> operacoes;

        if (idFuncionario != null || tipo != null) {
            operacoes = operacaoRepository.findAll();

            if (idFuncionario != null) {
                operacoes = operacoes.stream()
                        .filter(o -> o.getFuncionario().getId().equals(idFuncionario))
                        .collect(Collectors.toList());
            }

            if (tipo != null) {
                operacoes = operacoes.stream()
                        .filter(o -> o.getTipo().equals(tipo))
                        .collect(Collectors.toList());
            }
        } else if ("diario".equalsIgnoreCase(periodo)) {
            operacoes = operacaoRepository.findUltimosPeriodo(100);
        } else if ("semanal".equalsIgnoreCase(periodo)) {
            operacoes = operacaoRepository.findUltimosPeriodo(500);
        } else if ("mensal".equalsIgnoreCase(periodo)) {
            operacoes = operacaoRepository.findUltimosPeriodo(2000);
        } else {
            operacoes = operacaoRepository.findAllOrderByMaisRecentes();
        }

        return operacoes;
    }

    /**
     * Converte uma Operacao para OperacaoResumoDTO
     */
    private OperacaoResumoDTO converterParaResumo(Operacao op) {
        return new OperacaoResumoDTO(
                op.getId(),
                op.getProduto().getNumeroSerie(),
                op.getProduto().getAparelho(),
                op.getProduto().getModelo(),
                op.getValor(),
                op.getCliente().getNome(),
                op.getFuncionario().getNome(),
                op.getTipo(),
                op.getObservacoes()
        );
    }

    /**
     * Agrupa operações por tipo
     */
    private Map<String, Integer> agruparPorTipo(List<Operacao> operacoes) {
        return operacoes.stream()
                .collect(Collectors.groupingBy(
                        o -> obterNomeTipo(o.getTipo()),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));
    }

    /**
     * Agrupa operações por funcionário
     */
    private Map<String, Integer> agruparPorFuncionario(List<Operacao> operacoes) {
        return operacoes.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getFuncionario().getNome(),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));
    }

    /**
     * Agrupa e retorna os produtos mais movimentados (limitado a 10)
     */
    private Map<String, Integer> agruparProdutosMaisMovimentados(List<Operacao> operacoes) {
        return operacoes.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getProduto().getAparelho() + " - " + 
                             (o.getProduto().getModelo() != null ? o.getProduto().getModelo() : "S/M"),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Retorna o nome descritivo do tipo de operação
     */
    private String obterNomeTipo(Integer tipo) {
        return switch (tipo) {
            case 1 -> "Entrada";
            case 2 -> "Saída";
            case 3 -> "Manutenção";
            case 4 -> "Devolução";
            default -> "Outro (" + tipo + ")";
        };
    }

    @Override
    public Operacao criarOperacao(OperacaoCriarAtualizarDTO dto) {
        logger.info("Criando nova operação - Produto: {}, Cliente: {}, Funcionário: {}", 
                dto.numeroSerieProduto(), dto.idCliente(), dto.idFuncionario());

        // Criar nova operação
        Operacao operacao = new Operacao();
        operacao.setProduto(new Produto());
        operacao.getProduto().setNumeroSerie(dto.numeroSerieProduto());
        operacao.setValor(dto.valor());
        operacao.setCliente(new Pessoa());
        operacao.getCliente().setId(dto.idCliente());
        operacao.setFuncionario(new Funcionario());
        operacao.getFuncionario().setId(dto.idFuncionario());
        operacao.setTipo(dto.tipo());
        operacao.setObservacoes(dto.observacoes());

        Operacao salva = operacaoRepository.save(operacao);
        logger.info("Operação criada com sucesso. ID: {}", salva.getId());
        return salva;
    }

    @Override
    public Operacao atualizarOperacao(Integer id, OperacaoCriarAtualizarDTO dto) {
        logger.info("Atualizando operação com ID: {}", id);

        // Buscar operação existente
        Optional<Operacao> operacaoOpt = operacaoRepository.findById(id);
        if (operacaoOpt.isEmpty()) {
            logger.warn("Operação não encontrada com ID: {}", id);
            throw new RuntimeException("Operação não encontrada com ID: " + id);
        }

        Operacao operacao = operacaoOpt.get();

        // Atualizar campos
        operacao.getProduto().setNumeroSerie(dto.numeroSerieProduto());
        operacao.setValor(dto.valor());
        operacao.getCliente().setId(dto.idCliente());
        operacao.getFuncionario().setId(dto.idFuncionario());
        operacao.setTipo(dto.tipo());
        operacao.setObservacoes(dto.observacoes());

        Operacao atualizada = operacaoRepository.save(operacao);
        logger.info("Operação atualizada com sucesso. ID: {}", id);
        return atualizada;
    }

    @Override
    public void deletarOperacao(Integer id) {
        logger.info("Deletando operação com ID: {}", id);

        // Buscar operação existente
        Optional<Operacao> operacaoOpt = operacaoRepository.findById(id);
        if (operacaoOpt.isEmpty()) {
            logger.warn("Operação não encontrada com ID: {}", id);
            throw new RuntimeException("Operação não encontrada com ID: " + id);
        }

        operacaoRepository.deleteById(id);
        logger.info("Operação deletada com sucesso. ID: {}", id);
    }
}
