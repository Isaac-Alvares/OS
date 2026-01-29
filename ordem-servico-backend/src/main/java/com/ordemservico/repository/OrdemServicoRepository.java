package com.ordemservico.repository;

import com.ordemservico.domain.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório para operações de persistência da entidade OrdemServico.
 */
@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    /**
     * Busca ordens de serviço por cliente (case-insensitive).
     */
    List<OrdemServico> findByClienteContainingIgnoreCase(String cliente);

    /**
     * Busca ordens de serviço por data.
     */
    List<OrdemServico> findByData(LocalDate data);

    /**
     * Busca ordens de serviço em um intervalo de datas.
     */
    List<OrdemServico> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca ordens de serviço ordenadas por data decrescente.
     */
    List<OrdemServico> findAllByOrderByDataDescHoraDesc();

    /**
     * Busca ordens com seus itens (fetch join para evitar N+1).
     */
    @Query("SELECT DISTINCT o FROM OrdemServico o LEFT JOIN FETCH o.itens WHERE o.id = :id")
    OrdemServico findByIdWithItens(@Param("id") Long id);
}
