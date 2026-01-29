import React from 'react';
import type { OrdemServico } from '../types/index';

interface Props {
  ordem: OrdemServico;
  onChange: (ordem: OrdemServico) => void;
}

/**
 * Componente para formulário de cabeçalho da ordem.
 * Replica o painel de informações do sistema legado.
 */
export const FormularioCabecalho: React.FC<Props> = ({ ordem, onChange }) => {
  const handleInputChange = (field: keyof OrdemServico, value: any) => {
    onChange({ ...ordem, [field]: value });
  };

  const handleCheckboxChange = (field: keyof OrdemServico, checked: boolean) => {
    const updates: Partial<OrdemServico> = { [field]: checked };

    // Regras de exclusividade (sistema legado)
    if (field === 'tecCliente' && checked) {
      updates.tecSublimatec = false;
    } else if (field === 'tecSublimatec' && checked) {
      updates.tecCliente = false;
    } else if (field === 'soImpressao' && checked) {
      updates.calandra = false;
    } else if (field === 'calandra' && checked) {
      updates.soImpressao = false;
    }

    onChange({ ...ordem, ...updates });
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-6">
      <h2 className="text-xl font-bold mb-4 text-gray-800">Informações da Ordem</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Cliente */}
        <div className="md:col-span-1">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Cliente *
          </label>
          <input
            type="text"
            value={ordem.cliente}
            onChange={(e) => handleInputChange('cliente', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        {/* Largura Tecido */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Largura Tecido
          </label>
          <input
            type="text"
            value={ordem.larguraTecido || ''}
            onChange={(e) => handleInputChange('larguraTecido', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Papel */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Papel
          </label>
          <input
            type="text"
            value={ordem.papel || ''}
            onChange={(e) => handleInputChange('papel', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Largura Impressão */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Largura Impressão
          </label>
          <input
            type="text"
            value={ordem.larguraImpressao || ''}
            onChange={(e) => handleInputChange('larguraImpressao', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Tecido */}
        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tecido
          </label>
          <input
            type="text"
            value={ordem.tecido || ''}
            onChange={(e) => handleInputChange('tecido', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Data */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Data *
          </label>
          <input
            type="date"
            value={ordem.data}
            onChange={(e) => handleInputChange('data', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        {/* Hora */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Hora *
          </label>
          <input
            type="time"
            value={ordem.hora}
            onChange={(e) => handleInputChange('hora', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
      </div>

      {/* Checkboxes */}
      <div className="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4">
        <label className="flex items-center space-x-2 cursor-pointer">
          <input
            type="checkbox"
            checked={ordem.tecCliente}
            onChange={(e) => handleCheckboxChange('tecCliente', e.target.checked)}
            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <span className="text-sm text-gray-700">Tec Cliente</span>
        </label>

        <label className="flex items-center space-x-2 cursor-pointer">
          <input
            type="checkbox"
            checked={ordem.tecSublimatec}
            onChange={(e) => handleCheckboxChange('tecSublimatec', e.target.checked)}
            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <span className="text-sm text-gray-700">Tec Sublimatec</span>
        </label>

        <label className="flex items-center space-x-2 cursor-pointer">
          <input
            type="checkbox"
            checked={ordem.soImpressao}
            onChange={(e) => handleCheckboxChange('soImpressao', e.target.checked)}
            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <span className="text-sm text-gray-700">Só Impressão</span>
        </label>

        <label className="flex items-center space-x-2 cursor-pointer">
          <input
            type="checkbox"
            checked={ordem.calandra}
            onChange={(e) => handleCheckboxChange('calandra', e.target.checked)}
            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <span className="text-sm text-gray-700">Calandra</span>
        </label>
      </div>
    </div>
  );
};
