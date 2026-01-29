import api from './api';
import type { OrdemServico } from '../types/index';

/**
 * Serviço para operações com Ordem de Serviço.
 */
export const ordemServicoService = {
  /**
   * Cria uma nova ordem de serviço.
   */
  async criar(ordem: OrdemServico): Promise<OrdemServico> {
    const response = await api.post<OrdemServico>('/ordens', ordem);
    return response.data;
  },

  /**
   * Lista todas as ordens de serviço.
   */
  async listarTodas(): Promise<OrdemServico[]> {
    const response = await api.get<OrdemServico[]>('/ordens');
    return response.data;
  },

  /**
   * Busca uma ordem por ID.
   */
  async buscarPorId(id: number): Promise<OrdemServico> {
    const response = await api.get<OrdemServico>(`/ordens/${id}`);
    return response.data;
  },

  /**
   * Atualiza uma ordem existente.
   */
  async atualizar(id: number, ordem: OrdemServico): Promise<OrdemServico> {
    const response = await api.put<OrdemServico>(`/ordens/${id}`, ordem);
    return response.data;
  },

  /**
   * Deleta uma ordem.
   */
  async deletar(id: number): Promise<void> {
    await api.delete(`/ordens/${id}`);
  },

  /**
   * Busca ordens por cliente.
   */
  async buscarPorCliente(cliente: string): Promise<OrdemServico[]> {
    const response = await api.get<OrdemServico[]>('/ordens/buscar', {
      params: { cliente },
    });
    return response.data;
  },

  /**
   * Gera PDF de uma ordem.
   */
  async gerarPdf(id: number): Promise<Blob> {
    const response = await api.post(`/ordens/${id}/pdf`, null, {
      responseType: 'blob',
    });
    return response.data;
  },

  /**
   * Gera preview de PDF sem salvar.
   */
  async gerarPdfPreview(ordem: OrdemServico): Promise<Blob> {
    const response = await api.post('/ordens/pdf/preview', ordem, {
      responseType: 'blob',
    });
    return response.data;
  },
};
