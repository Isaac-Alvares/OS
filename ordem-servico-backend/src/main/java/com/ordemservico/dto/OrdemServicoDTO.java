package com.ordemservico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para transferência de dados de Ordem de Serviço.
 */
public class OrdemServicoDTO {

    private Long id;

    @NotBlank(message = "Cliente é obrigatório")
    private String cliente;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Hora é obrigatória")
    private LocalTime hora;

    private String papel;
    private String tecido;
    private String larguraTecido;
    private String larguraImpressao;

    @NotNull
    private Boolean tecCliente = false;

    @NotNull
    private Boolean tecSublimatec = false;

    @NotNull
    private Boolean soImpressao = false;

    @NotNull
    private Boolean calandra = false;

    @Valid
    private List<ItemOrdemDTO> itens = new ArrayList<>();

    // Construtores
    public OrdemServicoDTO() {
    }

    public OrdemServicoDTO(Long id, String cliente, LocalDate data, LocalTime hora, 
                          String papel, String tecido, String larguraTecido, 
                          String larguraImpressao, Boolean tecCliente, 
                          Boolean tecSublimatec, Boolean soImpressao, 
                          Boolean calandra, List<ItemOrdemDTO> itens) {
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

    // Getters e Setters
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

    public List<ItemOrdemDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrdemDTO> itens) {
        this.itens = itens;
    }
}
