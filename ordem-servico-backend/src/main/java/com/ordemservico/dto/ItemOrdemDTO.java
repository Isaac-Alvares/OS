package com.ordemservico.dto;

import com.ordemservico.domain.enums.TipoCrop;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para transferência de dados de Item de Ordem.
 */
public class ItemOrdemDTO {

    private Long id;

    @NotNull(message = "Número da página é obrigatório")
    private Integer numeroPagina;

    @NotNull(message = "Número da linha é obrigatório")
    private Integer numeroLinha;

    private String ref;
    private String pasta;
    private String metragem;

    @NotNull
    private TipoCrop tipoCrop = TipoCrop.ESQUERDA;

    private String caminhoImagem;

    // Construtores
    public ItemOrdemDTO() {
    }

    public ItemOrdemDTO(Long id, Integer numeroPagina, Integer numeroLinha, 
                       String ref, String pasta, String metragem, 
                       TipoCrop tipoCrop, String caminhoImagem) {
        this.id = id;
        this.numeroPagina = numeroPagina;
        this.numeroLinha = numeroLinha;
        this.ref = ref;
        this.pasta = pasta;
        this.metragem = metragem;
        this.tipoCrop = tipoCrop != null ? tipoCrop : TipoCrop.ESQUERDA;
        this.caminhoImagem = caminhoImagem;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
