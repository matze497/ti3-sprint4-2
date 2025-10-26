package br.com.vaztech.vaztech.repository;

import br.com.vaztech.vaztech.entity.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperacaoRepository extends JpaRepository<Operacao, Integer> {

    /**
     * Encontra todas as operações ordenadas por data de criação (mais recentes primeiro).
     * Para isso, precisamos adicionar data_criacao na tabela.
     */
    @Query("SELECT o FROM Operacao o ORDER BY o.id DESC")
    List<Operacao> findAllOrderByMaisRecentes();

    /**
     * Encontra as últimas N operações
     */
    @Query(value = "SELECT TOP :limit * FROM Operacoes ORDER BY id DESC", nativeQuery = true)
    List<Operacao> findUltimas(@Param("limit") int limit);

    /**
     * Conta operações por tipo
     */
    @Query("SELECT COUNT(o) FROM Operacao o WHERE o.tipo = :tipo")
    long countByTipo(@Param("tipo") Integer tipo);

    /**
     * Encontra operações por funcionário
     */
    @Query("SELECT o FROM Operacao o WHERE o.funcionario.id = :idFuncionario ORDER BY o.id DESC")
    List<Operacao> findByFuncionario(@Param("idFuncionario") Integer idFuncionario);

    /**
     * Encontra operações por tipo
     */
    @Query("SELECT o FROM Operacao o WHERE o.tipo = :tipo ORDER BY o.id DESC")
    List<Operacao> findByTipo(@Param("tipo") Integer tipo);

    /**
     * Encontra operações por cliente
     */
    @Query("SELECT o FROM Operacao o WHERE o.cliente.id = :idCliente ORDER BY o.id DESC")
    List<Operacao> findByCliente(@Param("idCliente") Integer idCliente);

    /**
     * Encontra operações por período (últimos N registros por ID decrescente)
     * Como a tabela não tem data de criação, usamos os IDs mais altos como mais recentes
     */
    @Query(value = "SELECT TOP :dias * FROM Operacoes ORDER BY id DESC", nativeQuery = true)
    List<Operacao> findUltimosPeriodo(@Param("dias") int dias);
}