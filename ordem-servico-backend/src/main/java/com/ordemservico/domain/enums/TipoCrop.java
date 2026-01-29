package com.ordemservico.domain.enums;

/**
 * Tipos de corte (crop) de imagem.
 * 
 * Corresponde aos botões de alinhamento do sistema legado:
 * - ESQUERDA: Corta mantendo o lado esquerdo da imagem
 * - DIREITA: Corta mantendo o lado direito da imagem
 * - COMPLETO: Mantém a imagem inteira sem corte
 */
public enum TipoCrop {
    
    /**
     * Corte mantendo lado esquerdo (padrão).
     * Se largura >= 2x altura, pega os primeiros 2/3 da largura.
     */
    ESQUERDA(0),
    
    /**
     * Corte mantendo lado direito.
     * Se largura >= 2x altura, pega os últimos 2/3 da largura.
     */
    DIREITA(1),
    
    /**
     * Sem corte - mantém imagem completa proporcional.
     */
    COMPLETO(2);
    
    private final int codigo;
    
    TipoCrop(int codigo) {
        this.codigo = codigo;
    }
    
    public int getCodigo() {
        return codigo;
    }
    
    /**
     * Converte código numérico para enum.
     * 
     * @param codigo 0 = ESQUERDA, 1 = DIREITA, 2 = COMPLETO
     * @return TipoCrop correspondente
     */
    public static TipoCrop fromCodigo(int codigo) {
        for (TipoCrop tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        return ESQUERDA; // padrão
    }
}
