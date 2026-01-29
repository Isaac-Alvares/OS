package com.ordemservico.controller;

import com.ordemservico.service.ImagemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para operações com imagens.
 */
@RestController
@RequestMapping("/api/imagens")
public class ImagemController {

    private static final Logger log = LoggerFactory.getLogger(ImagemController.class);

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    /**
     * Endpoint para upload de imagem.
     * 
     * Recebe MultipartFile e retorna o caminho relativo da imagem salva.
     * 
     * @param file Arquivo de imagem (jpg, png)
     * @return JSON com caminho da imagem
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImagem(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Recebendo upload de imagem: {} ({}KB)", 
                    file.getOriginalFilename(), 
                    file.getSize() / 1024);
            
            String caminhoImagem = imagemService.uploadImagem(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("caminhoImagem", caminhoImagem);
            response.put("nomeOriginal", file.getOriginalFilename());
            response.put("mensagem", "Upload realizado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Erro no upload de imagem", e);
            
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Endpoint para verificar se uma imagem existe.
     * 
     * @param filename Nome do arquivo
     * @return Status 200 se existe, 404 caso contrário
     */
    @GetMapping("/existe/{filename}")
    public ResponseEntity<?> verificarImagem(@PathVariable String filename) {
        boolean existe = imagemService.imagemExiste(filename);
        
        if (existe) {
            return ResponseEntity.ok(Map.of("existe", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("existe", false, "mensagem", "Imagem não encontrada"));
        }
    }

    /**
     * Endpoint para deletar uma imagem.
     * 
     * @param filename Nome do arquivo
     * @return Status 204 se deletado com sucesso
     */
    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deletarImagem(@PathVariable String filename) {
        try {
            imagemService.deletarImagem(filename);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar imagem: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao deletar imagem"));
        }
    }
}
