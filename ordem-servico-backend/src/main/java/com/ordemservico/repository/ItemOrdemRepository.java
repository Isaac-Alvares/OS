package com.ordemservico.repository;

import com.ordemservico.domain.entity.ItemOrdem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de persistência da entidade ItemOrdem.
 */
@Repository
public interface ItemOrdemRepository extends JpaRepository<ItemOrdem, Long> {

    /**
     * Busca todos os itens de uma ordem específica.
     */
    List<ItemOrdem> findByOrdemServicoId(Long ordemServicoId);

    /**
     * Busca itens de uma ordem ordenados por página e linha.
     */
    List<ItemOrdem> findByOrdemServicoIdOrderByNumeroPaginaAscNumeroLinhaAsc(Long ordemServicoId);

    /**
     * Busca itens de uma página específica de uma ordem.
     */
    List<ItemOrdem> findByOrdemServicoIdAndNumeroPagina(Long ordemServicoId, Integer numeroPagina);

    /**
     * Deleta todos os itens de uma ordem.
     */
    void deleteByOrdemServicoId(Long ordemServicoId);
}
