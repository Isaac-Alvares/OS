package com.ordemservico.service;

import com.ordemservico.domain.enums.TipoCrop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Serviço para processamento e armazenamento de imagens.
 * 
 * Replica a lógica de crop e redimensionamento do sistema legado.
 */
@Service
public class ImagemService {

    private static final Logger log = LoggerFactory.getLogger(ImagemService.class);

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> EXTENSOES_PERMITIDAS = Arrays.asList("jpg", "jpeg", "png");
    private static final long TAMANHO_MAXIMO = 60 * 1024 * 1024; // 60MB

    /**
     * Faz upload de uma imagem e retorna o caminho relativo.
     * 
     * @param file Arquivo enviado
     * @return Caminho relativo da imagem salva
     * @throws IOException Se houver erro no upload
     */
    public String uploadImagem(MultipartFile file) throws IOException {
        validarArquivo(file);

        // Gerar nome único
        String extensao = obterExtensao(file.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID().toString() + "." + extensao;
        
        // Salvar arquivo
        Path caminhoCompleto = Paths.get(uploadDir, nomeArquivo);
        Files.createDirectories(caminhoCompleto.getParent());
        file.transferTo(caminhoCompleto.toFile());
        
        log.info("Imagem salva: {}", nomeArquivo);
        return nomeArquivo;
    }

    /**
     * Processa imagem aplicando crop e redimensionamento.
     * 
     * Replica a lógica do LinhaPanel.drop() do sistema legado:
     * - Se largura >= 2x altura: aplica crop de 2/3 da largura
     * - Esquerda: pega início da imagem
     * - Direita: pega final da imagem
     * - Completo: mantém imagem inteira
     * 
     * @param caminhoImagem Caminho da imagem original
     * @param tipoCrop Tipo de corte
     * @param largura Largura final desejada
     * @param altura Altura final desejada
     * @return Imagem processada
     * @throws IOException Se houver erro ao ler a imagem
     */
    public BufferedImage processarImagem(String caminhoImagem, TipoCrop tipoCrop, 
                                         int largura, int altura) throws IOException {
        
        File arquivo = new File(uploadDir, caminhoImagem);
        BufferedImage imagemOriginal = ImageIO.read(arquivo);
        
        if (imagemOriginal == null) {
            throw new IOException("Não foi possível ler a imagem: " + caminhoImagem);
        }

        BufferedImage imagemCortada = aplicarCrop(imagemOriginal, tipoCrop);
        return redimensionar(imagemCortada, largura, altura);
    }

    /**
     * Aplica crop na imagem conforme tipo especificado.
     * 
     * Lógica do sistema legado (linhas 274-292 do LinhaPanel):
     * - Se modo COMPLETO: retorna imagem inteira
     * - Se largura >= 2x altura: corta para proporção 2:1
     *   - ESQUERDA: x=0
     *   - DIREITA: x=(largura - cropWidth)
     * - Caso contrário: retorna imagem sem corte
     */
    private BufferedImage aplicarCrop(BufferedImage imagem, TipoCrop tipoCrop) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        // Modo completo: sem corte
        if (tipoCrop == TipoCrop.COMPLETO) {
            return imagem;
        }

        // Verifica se precisa cortar (largura >= 2x altura)
        if (largura >= altura * 2) {
            int larguraCorte = altura * 2; // Proporção 2:1
            int x = (tipoCrop == TipoCrop.ESQUERDA) ? 0 : (largura - larguraCorte);
            
            log.debug("Aplicando crop {} - Original: {}x{}, Cortado: {}x{}, X: {}", 
                     tipoCrop, largura, altura, larguraCorte, altura, x);
            
            return imagem.getSubimage(x, 0, larguraCorte, altura);
        }

        // Não precisa cortar
        return imagem;
    }

    /**
     * Redimensiona imagem mantendo qualidade.
     * 
     * Replica a lógica das linhas 318-327 do LinhaPanel.
     */
    private BufferedImage redimensionar(BufferedImage original, int largura, int altura) {
        Image imagemEscalada = original.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        
        BufferedImage resultado = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resultado.createGraphics();
        g2d.drawImage(imagemEscalada, 0, 0, null);
        g2d.dispose();
        
        return resultado;
    }

    /**
     * Valida arquivo de upload.
     */
    private void validarArquivo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio");
        }

        if (file.getSize() > TAMANHO_MAXIMO) {
            throw new IOException("Arquivo excede o tamanho máximo de 60MB");
        }

        String extensao = obterExtensao(file.getOriginalFilename());
        if (!EXTENSOES_PERMITIDAS.contains(extensao.toLowerCase())) {
            throw new IOException("Formato não permitido. Use: " + EXTENSOES_PERMITIDAS);
        }
    }

    /**
     * Obtém extensão do arquivo.
     */
    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
    }

    /**
     * Deleta uma imagem do disco.
     */
    public void deletarImagem(String caminhoImagem) {
        try {
            Path caminho = Paths.get(uploadDir, caminhoImagem);
            Files.deleteIfExists(caminho);
            log.info("Imagem deletada: {}", caminhoImagem);
        } catch (IOException e) {
            log.error("Erro ao deletar imagem: {}", caminhoImagem, e);
        }
    }

    /**
     * Verifica se uma imagem existe.
     */
    public boolean imagemExiste(String caminhoImagem) {
        Path caminho = Paths.get(uploadDir, caminhoImagem);
        return Files.exists(caminho);
    }
}
