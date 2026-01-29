import api from './api';
import type { UploadResponse } from '../types/index';

/**
 * Serviço para operações com imagens.
 */
export const imagemService = {
  /**
   * Faz upload de uma imagem.
   */
  async upload(file: File): Promise<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post<UploadResponse>('/imagens/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },

  /**
   * Verifica se uma imagem existe.
   */
  async existe(filename: string): Promise<boolean> {
    try {
      const response = await api.get(`/imagens/existe/${filename}`);
      return response.data.existe;
    } catch {
      return false;
    }
  },

  /**
   * Deleta uma imagem.
   */
  async deletar(filename: string): Promise<void> {
    await api.delete(`/imagens/${filename}`);
  },

  /**
   * Retorna a URL completa de uma imagem.
   */
  getImagemUrl(caminhoImagem: string): string {
    return `http://localhost:8080/uploads/${caminhoImagem}`;
  },
};
