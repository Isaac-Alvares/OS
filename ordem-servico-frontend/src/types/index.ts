/**
 * Tipos TypeScript para o sistema de Ordem de Serviço.
 */

export const TipoCrop = {
  ESQUERDA: 'ESQUERDA',
  DIREITA: 'DIREITA',
  COMPLETO: 'COMPLETO'
} as const;

export type TipoCrop = typeof TipoCrop[keyof typeof TipoCrop];


export interface ItemOrdem {
  id?: number;
  numeroPagina: number;
  numeroLinha: number;
  ref?: string;
  pasta?: string;
  metragem?: string;
  tipoCrop: TipoCrop;
  caminhoImagem?: string;
}

export interface OrdemServico {
  id?: number;
  cliente: string;
  data: string; // ISO format: YYYY-MM-DD
  hora: string; // HH:mm
  papel?: string;
  tecido?: string;
  larguraTecido?: string;
  larguraImpressao?: string;
  tecCliente: boolean;
  tecSublimatec: boolean;
  soImpressao: boolean;
  calandra: boolean;
  itens: ItemOrdem[];
}

export interface UploadResponse {
  caminhoImagem: string;
  nomeOriginal: string;
  mensagem: string;
}

export interface ErrorResponse {
  erro: string;
}
