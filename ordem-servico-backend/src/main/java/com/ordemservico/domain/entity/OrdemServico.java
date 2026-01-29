package com.ordemservico.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma Ordem de Serviço de impressão têxtil.
 * 
 * Migrado da classe OrdemServico do sistema legado Swing.
 * Contém informações de cabeçalho e lista de itens (imagens).
 */
@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Informações do Cliente ===
    
    @Column(nullable = false, length = 200)
    private String cliente;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime hora;

    // === Informações de Produção ===
    
    @Column(length = 100)
    private String papel;

    @Column(length = 100)
    private String tecido;

    @Column(length = 50)
    private String larguraTecido;

    @Column(length = 50)
    private String larguraImpressao;

    // === Checkboxes (mutuamente exclusivos) ===
    
    /**
     * Tecido do Cliente (exclusivo com tecSublimatec).
     */
    @Column(nullable = false)
    private Boolean tecCliente = false;

    /**
     * Tecido Sublimatec (exclusivo com tecCliente).
     */
    @Column(nullable = false)
    private Boolean tecSublimatec = false;

    /**
     * Apenas Impressão (exclusivo com calandra).
     */
    @Column(nullable = false)
    private Boolean soImpressao = false;

    /**
     * Calandra (exclusivo com soImpressao).
     */
    @Column(nullable = false)
    private Boolean calandra = false;

    // === Relacionamento com Itens ===
    
    /**
     * Lista de itens (imagens) da ordem.
     * Cada página do sistema legado contém até 6 itens.
     */
    @OneToMany(
        mappedBy = "ordemServico",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<ItemOrdem> itens = new ArrayList<>();

    // === Construtores ===
    
    public OrdemServico() {
    }

    public OrdemServico(Long id, String cliente, LocalDate data, LocalTime hora, 
                       String papel, String tecido, String larguraTecido, 
                       String larguraImpressao, Boolean tecCliente, 
                       Boolean tecSublimatec, Boolean soImpressao, 
                       Boolean calandra, List<ItemOrdem> itens) {
        this.id = id;
        this.cliente = cliente;
        this.data = data;
        this.hora = hora;
        this.papel = papel;
        this.tecido = tecido;
        this.larguraTecido = larguraTecido;
        this.larguraImpressao = larguraImpressao;
        this.tecCliente = tecCliente;
        this.tecSublimatec = tecSublimatec;
        this.soImpressao = soImpressao;
        this.calandra = calandra;
        this.itens = itens != null ? itens : new ArrayList<>();
    }

    // === Getters e Setters ===
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getTecido() {
        return tecido;
    }

    public void setTecido(String tecido) {
        this.tecido = tecido;
    }

    public String getLarguraTecido() {
        return larguraTecido;
    }

    public void setLarguraTecido(String larguraTecido) {
        this.larguraTecido = larguraTecido;
    }

    public String getLarguraImpressao() {
        return larguraImpressao;
    }

    public void setLarguraImpressao(String larguraImpressao) {
        this.larguraImpressao = larguraImpressao;
    }

    public Boolean getTecCliente() {
        return tecCliente;
    }

    public void setTecCliente(Boolean tecCliente) {
        this.tecCliente = tecCliente;
    }

    public Boolean getTecSublimatec() {
        return tecSublimatec;
    }

    public void setTecSublimatec(Boolean tecSublimatec) {
        this.tecSublimatec = tecSublimatec;
    }

    public Boolean getSoImpressao() {
        return soImpressao;
    }

    public void setSoImpressao(Boolean soImpressao) {
        this.soImpressao = soImpressao;
    }

    public Boolean getCalandra() {
        return calandra;
    }

    public void setCalandra(Boolean calandra) {
        this.calandra = calandra;
    }

    public List<ItemOrdem> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrdem> itens) {
        this.itens = itens;
    }

    // === Métodos auxiliares ===
    
    /**
     * Adiciona um item à ordem e configura o relacionamento bidirecional.
     */
    public void adicionarItem(ItemOrdem item) {
        itens.add(item);
        item.setOrdemServico(this);
    }

    /**
     * Remove um item da ordem.
     */
    public void removerItem(ItemOrdem item) {
        itens.remove(item);
        item.setOrdemServico(null);
    }

    /**
     * Valida regras de negócio dos checkboxes mutuamente exclusivos.
     * 
     * @throws IllegalStateException se houver conflito
     */
    public void validarCheckboxes() {
        if (tecCliente && tecSublimatec) {
            throw new IllegalStateException(
                "Não é possível selecionar 'Tec Cliente' e 'Tec Sublimatec' simultaneamente"
            );
        }
        
        if (soImpressao && calandra) {
            throw new IllegalStateException(
                "Não é possível selecionar 'Só Impressão' e 'Calandra' simultaneamente"
            );
        }
    }

    @PrePersist
    @PreUpdate
    private void validarAntesDeSalvar() {
        validarCheckboxes();
    }
}
