package com.ordemservico.service;

import com.ordemservico.domain.entity.ItemOrdem;
import com.ordemservico.domain.entity.OrdemServico;
import com.ordemservico.dto.ItemOrdemDTO;
import com.ordemservico.dto.OrdemServicoDTO;
import com.ordemservico.repository.OrdemServicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de negócio para Ordem de Serviço.
 */
@Service
public class OrdemServicoService {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoService.class);
    
    private final OrdemServicoRepository repository;
    private final ImagemService imagemService;
    
    public OrdemServicoService(OrdemServicoRepository repository, ImagemService imagemService) {
        this.repository = repository;
        this.imagemService = imagemService;
    }

    /**
     * Cria uma nova ordem de serviço.
     */
    @Transactional
    public OrdemServicoDTO criar(OrdemServicoDTO dto) {
        log.info("Criando ordem de serviço para cliente: {}", dto.getCliente());
        
        OrdemServico ordem = converterParaEntidade(dto);
        ordem = repository.save(ordem);
        
        log.info("Ordem criada com ID: {}", ordem.getId());
        return converterParaDTO(ordem);
    }

    /**
     * Busca ordem por ID.
     */
    @Transactional(readOnly = true)
    public OrdemServicoDTO buscarPorId(Long id) {
        OrdemServico ordem = repository.findByIdWithItens(id);
        if (ordem == null) {
            throw new RuntimeException("Ordem não encontrada: " + id);
        }
        return converterParaDTO(ordem);
    }

    /**
     * Lista todas as ordens.
     */
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> listarTodas() {
        return repository.findAllByOrderByDataDescHoraDesc()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma ordem existente.
     */
    @Transactional
    public OrdemServicoDTO atualizar(Long id, OrdemServicoDTO dto) {
        log.info("Atualizando ordem: {}", id);
        
        OrdemServico ordem = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem não encontrada: " + id));
        
        // Atualizar campos
        ordem.setCliente(dto.getCliente());
        ordem.setData(dto.getData());
        ordem.setHora(dto.getHora());
        ordem.setPapel(dto.getPapel());
        ordem.setTecido(dto.getTecido());
        ordem.setLarguraTecido(dto.getLarguraTecido());
        ordem.setLarguraImpressao(dto.getLarguraImpressao());
        ordem.setTecCliente(dto.getTecCliente());
        ordem.setTecSublimatec(dto.getTecSublimatec());
        ordem.setSoImpressao(dto.getSoImpressao());
        ordem.setCalandra(dto.getCalandra());
        
        // Atualizar itens
        // Limpar e adicionar novos itens para garantir o relacionamento bidirecional
        ordem.getItens().clear();
        
        // Usar uma lista temporária para evitar problemas com a lambda
        List<ItemOrdem> novosItens = dto.getItens().stream()
            .map(this::converterItemParaEntidade)
            .collect(Collectors.toList());
            
        novosItens.forEach(ordem::adicionarItem);
        
        ordem = repository.save(ordem);
        log.info("Ordem atualizada: {}", id);
        
        return converterParaDTO(ordem);
    }

    /**
     * Deleta uma ordem.
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando ordem: {}", id);
        
        OrdemServico ordem = repository.findByIdWithItens(id);
        if (ordem == null) {
            throw new RuntimeException("Ordem não encontrada: " + id);
        }
        
        // Deletar imagens associadas
        ordem.getItens().forEach(item -> {
            if (item.getCaminhoImagem() != null) {
                imagemService.deletarImagem(item.getCaminhoImagem());
            }
        });
        
        repository.delete(ordem);
        log.info("Ordem deletada: {}", id);
    }

    /**
     * Busca ordens por cliente.
     */
    @Transactional(readOnly = true)
    public List<OrdemServicoDTO> buscarPorCliente(String cliente) {
        return repository.findByClienteContainingIgnoreCase(cliente)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // === Métodos de Conversão ===

    private OrdemServico converterParaEntidade(OrdemServicoDTO dto) {
        OrdemServico ordem = new OrdemServico();
        ordem.setId(dto.getId());
        ordem.setCliente(dto.getCliente());
        ordem.setData(dto.getData());
        ordem.setHora(dto.getHora());
        ordem.setPapel(dto.getPapel());
        ordem.setTecido(dto.getTecido());
        ordem.setLarguraTecido(dto.getLarguraTecido());
        ordem.setLarguraImpressao(dto.getLarguraImpressao());
        ordem.setTecCliente(dto.getTecCliente());
        ordem.setTecSublimatec(dto.getTecSublimatec());
        ordem.setSoImpressao(dto.getSoImpressao());
        ordem.setCalandra(dto.getCalandra());

        // Converter itens
        dto.getItens().forEach(itemDTO -> {
            ItemOrdem item = converterItemParaEntidade(itemDTO);
            ordem.adicionarItem(item);
        });

        return ordem;
    }

    private ItemOrdem converterItemParaEntidade(ItemOrdemDTO dto) {
        ItemOrdem item = new ItemOrdem();
        item.setId(dto.getId());
        item.setNumeroPagina(dto.getNumeroPagina());
        item.setNumeroLinha(dto.getNumeroLinha());
        item.setRef(dto.getRef());
        item.setPasta(dto.getPasta());
        item.setMetragem(dto.getMetragem());
        item.setTipoCrop(dto.getTipoCrop());
        item.setCaminhoImagem(dto.getCaminhoImagem());
        return item;
    }

    private OrdemServicoDTO converterParaDTO(OrdemServico ordem) {
        OrdemServicoDTO dto = new OrdemServicoDTO();
        dto.setId(ordem.getId());
        dto.setCliente(ordem.getCliente());
        dto.setData(ordem.getData());
        dto.setHora(ordem.getHora());
        dto.setPapel(ordem.getPapel());
        dto.setTecido(ordem.getTecido());
        dto.setLarguraTecido(ordem.getLarguraTecido());
        dto.setLarguraImpressao(ordem.getLarguraImpressao());
        dto.setTecCliente(ordem.getTecCliente());
        dto.setTecSublimatec(ordem.getTecSublimatec());
        dto.setSoImpressao(ordem.getSoImpressao());
        dto.setCalandra(ordem.getCalandra());
        dto.setItens(ordem.getItens().stream()
                .map(this::converterItemParaDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private ItemOrdemDTO converterItemParaDTO(ItemOrdem item) {
        ItemOrdemDTO dto = new ItemOrdemDTO();
        dto.setId(item.getId());
        dto.setNumeroPagina(item.getNumeroPagina());
        dto.setNumeroLinha(item.getNumeroLinha());
        dto.setRef(item.getRef());
        dto.setPasta(item.getPasta());
        dto.setMetragem(item.getMetragem());
        dto.setTipoCrop(item.getTipoCrop());
        dto.setCaminhoImagem(item.getCaminhoImagem());
        return dto;
    }
}
