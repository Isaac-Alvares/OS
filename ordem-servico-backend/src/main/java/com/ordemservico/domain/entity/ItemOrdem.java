package com.ordemservico.domain.entity;

import com.ordemservico.domain.enums.TipoCrop;
import jakarta.persistence.*;

/**
 * Entidade que representa um item (linha de imagem) de uma Ordem de Serviço.
 * 
 * Corresponde a uma linha do LinhaPanel do sistema legado.
 * Cada página contém até 6 itens.
 */
@Entity
@Table(name = "item_ordem")
public class ItemOrdem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Relacionamento com Ordem ===
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    // === Posicionamento ===
    
    /**
     * Número da página (1-based).
     * Corresponde às abas do sistema legado.
     */
    @Column(nullable = false)
    private Integer numeroPagina;

    /**
     * Número da linha dentro da página (0-5).
     * Corresponde ao índice no array de 6 linhas.
     */
    @Column(nullable = false)
    private Integer numeroLinha;

    // === Informações da Imagem ===
    
    /**
     * Referência (nome do arquivo original).
     * Corresponde ao campo "REF" do sistema legado.
     */
    @Column(length = 255)
    private String ref;

    /**
     * Pasta/caminho do arquivo original (sem hostname).
     * Corresponde ao campo "PASTA" do sistema legado.
     */
    @Column(length = 500)
    private String pasta;

    /**
     * Metragem do tecido.
     * Corresponde ao campo "MTS" do sistema legado.
     */
    @Column(length = 50)
    private String metragem;

    /**
     * Tipo de corte da imagem (ESQUERDA, DIREITA, COMPLETO).
     * Corresponde aos botões de alinhamento do sistema legado.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCrop tipoCrop = TipoCrop.ESQUERDA;

    /**
     * Caminho relativo da imagem no servidor.
     * Exemplo: "abc123-def456.jpg"
     */
    @Column(length = 500)
    private String caminhoImagem;

    // === Construtores ===
    
    public ItemOrdem() {
    }

    public ItemOrdem(Long id, OrdemServico ordemServico, Integer numeroPagina, 
                    Integer numeroLinha, String ref, String pasta, 
                    String metragem, TipoCrop tipoCrop, String caminhoImagem) {
        this.id = id;
        this.ordemServico = ordemServico;
        this.numeroPagina = numeroPagina;
        this.numeroLinha = numeroLinha;
        this.ref = ref;
        this.pasta = pasta;
        this.metragem = metragem;
        this.tipoCrop = tipoCrop != null ? tipoCrop : TipoCrop.ESQUERDA;
        this.caminhoImagem = caminhoImagem;
    }

    // === Getters e Setters ===
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public Integer getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(Integer numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPasta() {
        return pasta;
    }

    public void setPasta(String pasta) {
        this.pasta = pasta;
    }

    public String getMetragem() {
        return metragem;
    }

    public void setMetragem(String metragem) {
        this.metragem = metragem;
    }

    public TipoCrop getTipoCrop() {
        return tipoCrop;
    }

    public void setTipoCrop(TipoCrop tipoCrop) {
        this.tipoCrop = tipoCrop;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    // === Métodos auxiliares ===
    
    /**
     * Verifica se o item possui imagem.
     */
    public boolean possuiImagem() {
        return caminhoImagem != null && !caminhoImagem.isEmpty();
    }

    /**
     * Retorna o nome do arquivo da imagem (sem path).
     */
    public String getNomeArquivoImagem() {
        if (caminhoImagem == null) return null;
        int lastSlash = Math.max(
            caminhoImagem.lastIndexOf('/'),
            caminhoImagem.lastIndexOf('\\')
        );
        return lastSlash >= 0 ? caminhoImagem.substring(lastSlash + 1) : caminhoImagem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemOrdem)) return false;
        ItemOrdem that = (ItemOrdem) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
