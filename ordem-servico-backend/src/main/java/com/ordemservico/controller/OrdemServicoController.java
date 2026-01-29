package com.ordemservico.controller;

import com.ordemservico.dto.OrdemServicoDTO;
import com.ordemservico.service.OrdemServicoService;
import com.ordemservico.service.PdfService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações com Ordem de Serviço.
 */
@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoController.class);

    private final OrdemServicoService service;
    private final PdfService pdfService;

    public OrdemServicoController(OrdemServicoService service, PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    /**
     * Cria uma nova ordem de serviço.
     * 
     * POST /api/ordens
     */
    @PostMapping
    public ResponseEntity<OrdemServicoDTO> criar(@Valid @RequestBody OrdemServicoDTO dto) {
        log.info("POST /api/ordens - Criando ordem para cliente: {}", dto.getCliente());
        OrdemServicoDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    /**
     * Lista todas as ordens de serviço.
     * 
     * GET /api/ordens
     */
    @GetMapping
    public ResponseEntity<List<OrdemServicoDTO>> listarTodas() {
        log.info("GET /api/ordens - Listando todas as ordens");
        List<OrdemServicoDTO> ordens = service.listarTodas();
        return ResponseEntity.ok(ordens);
    }

    /**
     * Busca ordem por ID.
     * 
     * GET /api/ordens/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/ordens/{} - Buscando ordem", id);
        try {
            OrdemServicoDTO ordem = service.buscarPorId(id);
            return ResponseEntity.ok(ordem);
        } catch (RuntimeException e) {
            log.error("Ordem não encontrada: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Atualiza uma ordem existente.
     * 
     * PUT /api/ordens/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdemServicoDTO dto) {
        
        log.info("PUT /api/ordens/{} - Atualizando ordem", id);
        try {
            OrdemServicoDTO atualizada = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizada);
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar ordem: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta uma ordem.
     * 
     * DELETE /api/ordens/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/ordens/{} - Deletando ordem", id);
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro ao deletar ordem: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca ordens por nome do cliente.
     * 
     * GET /api/ordens/buscar?cliente=nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<OrdemServicoDTO>> buscarPorCliente(
            @RequestParam String cliente) {
        
        log.info("GET /api/ordens/buscar?cliente={}", cliente);
        List<OrdemServicoDTO> ordens = service.buscarPorCliente(cliente);
        return ResponseEntity.ok(ordens);
    }

    /**
     * Gera PDF de uma ordem de serviço.
     * 
     * POST /api/ordens/{id}/pdf
     * 
     * Retorna o PDF como byte array para download/preview no navegador.
     */
    @PostMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        log.info("POST /api/ordens/{}/pdf - Gerando PDF", id);
        
        try {
            OrdemServicoDTO ordem = service.buscarPorId(id);
            byte[] pdfBytes = pdfService.gerarPdf(ordem);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", 
                    "ordem_servico_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            log.info("PDF gerado com sucesso: {} bytes", pdfBytes.length);
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
            
        } catch (Exception e) {
            log.error("Erro ao gerar PDF da ordem: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gera PDF a partir de um DTO (sem salvar no banco).
     * 
     * POST /api/ordens/pdf/preview
     * 
     * Útil para preview antes de salvar a ordem.
     */
    @PostMapping("/pdf/preview")
    public ResponseEntity<byte[]> gerarPdfPreview(@Valid @RequestBody OrdemServicoDTO dto) {
        log.info("POST /api/ordens/pdf/preview - Gerando preview de PDF");
        
        try {
            byte[] pdfBytes = pdfService.gerarPdf(dto);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "preview_ordem_servico.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
            
        } catch (Exception e) {
            log.error("Erro ao gerar preview de PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
