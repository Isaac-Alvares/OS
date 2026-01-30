import React, { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import type { ItemOrdem } from '../types/index';
import { TipoCrop } from '../types/index';
import { imagemService } from '../services/imagemService';

// URL base da API (a mesma do api.ts)
const API_URL = 'https://back-production-18e4.up.railway.app';

interface Props {
  item: ItemOrdem;
  numeroLinha: number;
  onChange: (item: ItemOrdem) => void;
}

/**
 * Componente para uma linha de imagem.
 * Replica o LinhaPanel do sistema legado.
 */
export const LinhaImagem: React.FC<Props> = ({ item, numeroLinha, onChange }) => {
  const onDrop = useCallback(
    async (acceptedFiles: File[]) => {
      if (acceptedFiles.length === 0) return;

      const file = acceptedFiles[0];

      try {
        // Upload da imagem
        const response = await imagemService.upload(file);

        // Extrair apenas o nome do arquivo (sem o prefixo ./)
        const nomeArquivo = file.name;

        // Atualizar item
        onChange({
          ...item,
          ref: nomeArquivo,
          pasta: nomeArquivo, // CORREÇÃO: Usar apenas o nome, sem o "./
          caminhoImagem: response.caminhoImagem,
        });
      } catch (error) {
        console.error('Erro no upload:', error);
        alert('Erro ao fazer upload da imagem');
      }
    },
    [item, onChange]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/jpeg': ['.jpg', '.jpeg'],
      'image/png': ['.png'],
    },
    maxFiles: 1,
    maxSize: 60 * 1024 * 1024, // 60MB
  });

  const handleCropChange = (tipoCrop: TipoCrop) => {
    onChange({ ...item, tipoCrop });
  };

  const handleInputChange = (field: keyof ItemOrdem, value: string) => {
    onChange({ ...item, [field]: value });
  };

  return (
    <div className="bg-gray-50 rounded-lg p-4 mb-4 border border-gray-200">
      <div className="flex flex-col lg:flex-row gap-4">
        {/* Área de Drop/Preview da Imagem */}
        <div
          {...getRootProps()}
          className={`w-full lg:w-64 h-32 border-2 border-dashed rounded-lg flex items-center justify-center cursor-pointer transition-colors ${
            isDragActive
              ? 'border-blue-500 bg-blue-50'
              : 'border-gray-300 bg-white hover:border-gray-400'
          }`}
        >
          <input {...getInputProps()} />

          {item.caminhoImagem ? (
            <img
              src={`${API_URL}/uploads/${item.caminhoImagem}`}
              alt={`Imagem ${numeroLinha + 1}`}
              className="h-16 w-auto object-contain mx-auto"
            />
          ) : (
            <div className="text-center text-gray-500 text-sm px-4">
              {isDragActive ? (
                <p>Solte a imagem aqui...</p>
              ) : (
                <p>
                  Arraste uma imagem ou clique para selecionar
                  <br />
                  <span className="text-xs">(JPG, PNG - máx 60MB)</span>
                </p>
              )}
            </div>
          )}
        </div>

        {/* Campos de Informação */}
        <div className="flex-1 space-y-3">
          <div className="flex items-center gap-2">
            <label className="text-sm font-medium text-gray-700 w-16">
              REF:
            </label>
            <input
              type="text"
              value={item.ref || ''}
              onChange={(e) => handleInputChange('ref', e.target.value)}
              className="flex-1 px-2 py-1 border border-gray-300 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Nome do arquivo"
            />
          </div>

          <div className="flex items-center gap-2">
            <label className="text-sm font-medium text-gray-700 w-16">
              PASTA:
            </label>
            <input
              type="text"
              value={item.pasta || ''}
              onChange={(e) => handleInputChange('pasta', e.target.value)}
              className="flex-1 px-2 py-1 border border-gray-300 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Caminho da pasta"
            />
          </div>

          <div className="flex items-center gap-4">
            {/* Botões de Crop */}
            <div className="flex items-center gap-2">
              <label className="text-sm font-medium text-gray-700">
                Crop:
              </label>

              <button
                type="button"
                onClick={() => handleCropChange(TipoCrop.ESQUERDA)}
                className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
                  item.tipoCrop === TipoCrop.ESQUERDA
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
                title="Cortar lado esquerdo"
              >
                ⬅
              </button>

              <button
                type="button"
                onClick={() => handleCropChange(TipoCrop.COMPLETO)}
                className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
                  item.tipoCrop === TipoCrop.COMPLETO
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
                title="Imagem completa"
              >
                ⬌
              </button>

              <button
                type="button"
                onClick={() => handleCropChange(TipoCrop.DIREITA)}
                className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
                  item.tipoCrop === TipoCrop.DIREITA
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
                title="Cortar lado direito"
              >
                ➡
              </button>
            </div>

            {/* Campo MTS */}
            <div className="flex items-center gap-2">
              <label className="text-sm font-medium text-gray-700">
                MTS:
              </label>
              <input
                type="text"
                value={item.metragem || ''}
                onChange={(e) =>
                  handleInputChange('metragem', e.target.value)
                }
                className="w-20 px-2 py-1 border border-gray-300 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500"
                placeholder="0.00"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
