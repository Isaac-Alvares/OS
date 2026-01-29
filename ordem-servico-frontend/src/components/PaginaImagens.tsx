import React from 'react';
import { LinhaImagem } from './LinhaImagem';
import type { ItemOrdem } from '../types/index';
import { TipoCrop } from '../types/index';

interface Props {
  numeroPagina: number;
  itens: ItemOrdem[];
  onChange: (itens: ItemOrdem[]) => void;
}

const MAX_LINHAS = 6;

/**
 * Componente para uma página de imagens (6 linhas).
 * Replica o sistema de abas do legado.
 */
export const PaginaImagens: React.FC<Props> = ({ numeroPagina, itens, onChange }) => {
  // Garantir que sempre temos 6 linhas
  const linhasCompletas: ItemOrdem[] = Array.from({ length: MAX_LINHAS }, (_, index) => {
    const itemExistente = itens.find((item) => item.numeroLinha === index);
    return (
      itemExistente || {
        numeroPagina,
        numeroLinha: index,
        tipoCrop: TipoCrop.ESQUERDA,
      }
    );
  });

  const handleItemChange = (index: number, item: ItemOrdem) => {
    const novosItens = [...linhasCompletas];
    novosItens[index] = item;
    onChange(novosItens);
  };

  return (
    <div className="space-y-4">
      {linhasCompletas.map((item, index) => (
        <LinhaImagem
          key={index}
          item={item}
          numeroLinha={index}
          onChange={(novoItem) => handleItemChange(index, novoItem)}
        />
      ))}
    </div>
  );
};
