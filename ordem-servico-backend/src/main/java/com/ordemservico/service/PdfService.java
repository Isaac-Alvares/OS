package com.ordemservico.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.ordemservico.dto.ItemOrdemDTO;
import com.ordemservico.dto.OrdemServicoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para geração de PDF de Ordem de Serviço.
 * 
 * Replica o layout e formatação do PrintUtils do sistema legado.
 */
@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);
    
    private final ImagemService imagemService;
    
    public PdfService(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Gera PDF completo de uma ordem de serviço.
     * 
     * Replica a lógica do PrintUtils.createMultiPagePrintable() do sistema legado.
     * 
     * @param dto Dados da ordem de serviço
     * @return Bytes do PDF gerado
     * @throws Exception Se houver erro na geração
     */
    public byte[] gerarPdf(OrdemServicoDTO dto) throws Exception {
        log.info("Gerando PDF para ordem: {}", dto.getCliente());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Agrupar itens por página
            List<List<ItemOrdemDTO>> paginas = agruparItensPorPagina(dto.getItens());
            
            for (int i = 0; i < paginas.size(); i++) {
                if (i > 0) {
                    document.newPage();
                }
                gerarPagina(document, dto, paginas.get(i));
            }

            document.close();
            log.info("PDF gerado com sucesso: {} páginas", paginas.size());
            
        } catch (Exception e) {
            log.error("Erro ao gerar PDF", e);
            throw e;
        }

        return baos.toByteArray();
    }

    /**
     * Gera uma página do PDF.
     * 
     * Layout baseado no PrintUtils (linhas 1078-1298):
     * - Cabeçalho com título e informações
     * - Checkboxes
     * - Área de observações
     * - Blocos de imagem (até 6 por página)
     * - Rodapé com autorização
     */
    private void gerarPagina(Document document, OrdemServicoDTO ordem, 
                            List<ItemOrdemDTO> itens) throws Exception {
        
        // === CABEÇALHO ===
        adicionarCabecalho(document, ordem);
        
        // === CHECKBOXES E OBSERVAÇÕES ===
        adicionarCheckboxesEObservacoes(document, ordem);
        
        // === BLOCOS DE IMAGEM ===
        adicionarBlocosImagem(document, itens);
        
        // === RODAPÉ ===
        adicionarRodape(document);
    }

    /**
     * Adiciona cabeçalho do documento.
     */
    private void adicionarCabecalho(Document document, OrdemServicoDTO ordem) throws DocumentException {
        // Título
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph titulo = new Paragraph("ORDEM DE SERVIÇO", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Informações do cabeçalho
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        document.add(new Paragraph(
            String.format("Cliente: %s                    Larg.Tec: %s",
                ordem.getCliente(),
                ordem.getLarguraTecido() != null ? ordem.getLarguraTecido() : ""),
            normalFont
        ));
        
        document.add(new Paragraph(
            String.format("Papel: %s                    Larg. Impressão: %s",
                ordem.getPapel() != null ? ordem.getPapel() : "",
                ordem.getLarguraImpressao() != null ? ordem.getLarguraImpressao() : ""),
            normalFont
        ));
        
        document.add(new Paragraph(
            String.format("Tecido: %s", ordem.getTecido() != null ? ordem.getTecido() : ""),
            normalFont
        ));
        
        document.add(new Paragraph(
            String.format("Data: %s                    Hora: %s",
                ordem.getData().format(DATE_FORMATTER),
                ordem.getHora().format(TIME_FORMATTER)),
            normalFont
        ));
        
        document.add(new Paragraph(" ")); // Espaçamento
    }

    /**
     * Adiciona checkboxes e área de observações.
     */
    private void adicionarCheckboxesEObservacoes(Document document, OrdemServicoDTO ordem) 
            throws DocumentException {
        
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        // Checkboxes (linha 1130-1136 do PrintUtils)
        String checkboxes = String.format(
            "Tec Cliente: %s          Só Impressão: %s\n" +
            "Tec Sublimatec: %s          Calandra: %s",
            ordem.getTecCliente() ? "✔" : "☐",
            ordem.getSoImpressao() ? "✔" : "☐",
            ordem.getTecSublimatec() ? "✔" : "☐",
            ordem.getCalandra() ? "✔" : "☐"
        );
        
        Paragraph checkPara = new Paragraph(checkboxes, normalFont);
        document.add(checkPara);
        
        // Área de observações (placeholder)
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Observações: _____________________________________", normalFont));
        document.add(new Paragraph(" "));
    }

    /**
     * Adiciona blocos de imagem.
     * 
     * Cada bloco contém:
     * - Imagem processada (400x220px)
     * - REF em vermelho/negrito
     * - PASTA em maiúsculas
     * - Campos de controle (Ploteiro, Calandra, Conferente)
     */
    private void adicionarBlocosImagem(Document document, List<ItemOrdemDTO> itens) 
            throws Exception {
        
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font redBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        redBoldFont.setColor(java.awt.Color.RED);

        for (ItemOrdemDTO item : itens) {
            // Processar e adicionar imagem
            if (item.getCaminhoImagem() != null && !item.getCaminhoImagem().isEmpty()) {
                try {
                    BufferedImage img = imagemService.processarImagem(
                        item.getCaminhoImagem(),
                        item.getTipoCrop(),
                        400, 220
                    );
                    
                    // Converter BufferedImage para Image do OpenPDF
                    Image pdfImage = Image.getInstance(img, null);
                    pdfImage.scaleAbsolute(200, 110); // Escala para PDF
                    document.add(pdfImage);
                    
                } catch (IOException e) {
                    log.error("Erro ao processar imagem: {}", item.getCaminhoImagem(), e);
                    document.add(new Paragraph("[Imagem não disponível]", normalFont));
                }
            } else {
                document.add(new Paragraph("[Sem imagem]", normalFont));
            }

            // Informações do item
            String ref = item.getRef() != null ? item.getRef() : "__________";
            String pasta = item.getPasta() != null ? item.getPasta().toUpperCase() : "__________";
            String mts = item.getMetragem() != null && !item.getMetragem().isEmpty() 
                        ? item.getMetragem() : "_________";

            // REF (em vermelho/negrito no sistema legado)
            Paragraph refPara = new Paragraph();
            refPara.add(new Chunk("REF: ", normalFont));
            refPara.add(new Chunk(ref, redBoldFont));
            refPara.add(new Chunk("    MTS: " + mts, normalFont));
            document.add(refPara);

            // PASTA (em maiúsculas/negrito no sistema legado)
            Paragraph pastaPara = new Paragraph();
            pastaPara.add(new Chunk("PASTA: ", normalFont));
            pastaPara.add(new Chunk(pasta, redBoldFont));
            document.add(pastaPara);

            // Campos de controle (linhas 1276-1280 do PrintUtils)
            document.add(new Paragraph(
                "Ploteiro: ___________  Data:__/__/__  Máquina: ______", normalFont));
            document.add(new Paragraph(
                "Op. Calandra: _________  Data:__/__/__", normalFont));
            document.add(new Paragraph(
                "Conferente: ___________  Data:__/__/__  Revisão: ___", normalFont));
            
            document.add(new Paragraph(" ")); // Espaçamento entre blocos
        }
    }

    /**
     * Adiciona rodapé com autorização.
     */
    private void adicionarRodape(Document document) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        Paragraph rodape = new Paragraph(
            "Autorizado por: ___________________________________________", 
            normalFont
        );
        rodape.setSpacingBefore(20);
        document.add(rodape);
    }

    /**
     * Agrupa itens por número de página.
     * 
     * Cada página do sistema legado contém até 6 itens.
     */
    private List<List<ItemOrdemDTO>> agruparItensPorPagina(List<ItemOrdemDTO> itens) {
        return itens.stream()
                .collect(Collectors.groupingBy(ItemOrdemDTO::getNumeroPagina))
                .values()
                .stream()
                .collect(Collectors.toList());
    }
}
