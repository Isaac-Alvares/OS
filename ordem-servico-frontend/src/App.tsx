import React, { useState, useEffect } from 'react';
import { FormularioCabecalho } from './components/FormularioCabecalho';
import { PaginaImagens } from './components/PaginaImagens';
import type { OrdemServico, ItemOrdem } from './types/index';
import { TipoCrop } from './types/index';
import { ordemServicoService } from './services/ordemServicoService';

function App() {
  const [ordem, setOrdem] = useState<OrdemServico>({
    cliente: '',
    data: new Date().toISOString().split('T')[0],
    hora: new Date().toTimeString().slice(0, 5),
    tecCliente: false,
    tecSublimatec: false,
    soImpressao: false,
    calandra: false,
    itens: [],
  });

  const [paginaAtual, setPaginaAtual] = useState(1);
  const [totalPaginas, setTotalPaginas] = useState(1);
  const [salvando, setSalvando] = useState(false);
  const [gerandoPdf, setGerandoPdf] = useState(false);

  // Inicializar primeira página
  useEffect(() => {
    const itensIniciais: ItemOrdem[] = Array.from({ length: 6 }, (_, index) => ({
      numeroPagina: 1,
      numeroLinha: index,
      tipoCrop: TipoCrop.ESQUERDA,
    }));
    setOrdem((prev) => ({ ...prev, itens: itensIniciais }));
  }, []);

  const itensPaginaAtual = ordem.itens.filter(
    (item) => item.numeroPagina === paginaAtual
  );

  const handleItensChange = (novosItens: ItemOrdem[]) => {
    const outrosItens = ordem.itens.filter(
      (item) => item.numeroPagina !== paginaAtual
    );
    setOrdem({ ...ordem, itens: [...outrosItens, ...novosItens] });
  };

  const adicionarPagina = () => {
    const novaPagina = totalPaginas + 1;
    const novosItens: ItemOrdem[] = Array.from({ length: 6 }, (_, index) => ({
      numeroPagina: novaPagina,
      numeroLinha: index,
      tipoCrop: TipoCrop.ESQUERDA,
    }));
    setOrdem({ ...ordem, itens: [...ordem.itens, ...novosItens] });
    setTotalPaginas(novaPagina);
    setPaginaAtual(novaPagina);
  };

  const removerPagina = (numeroPagina: number) => {
    if (totalPaginas === 1) {
      alert('Não é possível remover a única página');
      return;
    }

    const itensRestantes = ordem.itens.filter(
      (item) => item.numeroPagina !== numeroPagina
    );

    // Reindexar páginas
    const itensReindexados = itensRestantes.map((item) => ({
      ...item,
      numeroPagina: item.numeroPagina > numeroPagina 
        ? item.numeroPagina - 1 
        : item.numeroPagina,
    }));

    setOrdem({ ...ordem, itens: itensReindexados });
    setTotalPaginas(totalPaginas - 1);
    
    if (paginaAtual > totalPaginas - 1) {
      setPaginaAtual(totalPaginas - 1);
    }
  };

  const handleSalvar = async () => {
    if (!ordem.cliente.trim()) {
      alert('Por favor, preencha o nome do cliente');
      return;
    }

    setSalvando(true);
    try {
      const ordemSalva = await ordemServicoService.criar(ordem);
      alert(`Ordem salva com sucesso! ID: ${ordemSalva.id}`);
      setOrdem(ordemSalva);
    } catch (error) {
      console.error('Erro ao salvar ordem:', error);
      alert('Erro ao salvar ordem de serviço');
    } finally {
      setSalvando(false);
    }
  };

  const handleGerarPdf = async () => {
    if (!ordem.cliente.trim()) {
      alert('Por favor, preencha o nome do cliente');
      return;
    }

    setGerandoPdf(true);
    try {
      const pdfBlob = await ordemServicoService.gerarPdfPreview(ordem);
      
      // Criar URL temporária e abrir em nova aba
      const url = URL.createObjectURL(pdfBlob);
      window.open(url, '_blank');
      
      // Limpar URL após alguns segundos
      setTimeout(() => URL.revokeObjectURL(url), 10000);
    } catch (error) {
      console.error('Erro ao gerar PDF:', error);
      alert('Erro ao gerar PDF');
    } finally {
      setGerandoPdf(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-blue-600 text-white shadow-lg">
        <div className="container mx-auto px-4 py-4">
          <h1 className="text-2xl font-bold">Sistema de Ordem de Serviço</h1>
          <p className="text-sm text-blue-100">Impressão Têxtil</p>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6">
        {/* Formulário de Cabeçalho */}
        <FormularioCabecalho ordem={ordem} onChange={setOrdem} />

        {/* Abas de Páginas */}
        <div className="bg-white rounded-lg shadow-md mb-6">
          <div className="border-b border-gray-200">
            <div className="flex items-center overflow-x-auto">
              {Array.from({ length: totalPaginas }, (_, index) => index + 1).map(
                (numPagina) => (
                  <div
                    key={numPagina}
                    className={`flex items-center border-r border-gray-200 ${
                      paginaAtual === numPagina ? 'bg-blue-50' : ''
                    }`}
                  >
                    <button
                      onClick={() => setPaginaAtual(numPagina)}
                      className={`px-6 py-3 text-sm font-medium transition-colors ${
                        paginaAtual === numPagina
                          ? 'text-blue-600 border-b-2 border-blue-600'
                          : 'text-gray-600 hover:text-gray-900'
                      }`}
                    >
                      Página {numPagina}
                    </button>
                    {totalPaginas > 1 && (
                      <button
                        onClick={() => removerPagina(numPagina)}
                        className="px-2 text-red-500 hover:text-red-700 text-lg font-bold"
                        title="Remover página"
                      >
                        ×
                      </button>
                    )}
                  </div>
                )
              )}
              <button
                onClick={adicionarPagina}
                className="px-6 py-3 text-sm font-medium text-blue-600 hover:bg-blue-50 transition-colors"
              >
                + Página
              </button>
            </div>
          </div>

          {/* Conteúdo da Página Atual */}
          <div className="p-6">
            <PaginaImagens
              numeroPagina={paginaAtual}
              itens={itensPaginaAtual}
              onChange={handleItensChange}
            />
          </div>
        </div>

        {/* Botões de Ação */}
        <div className="flex gap-4 justify-end">
          <button
            onClick={handleSalvar}
            disabled={salvando}
            className="px-6 py-3 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          >
            {salvando ? 'Salvando...' : 'Salvar Ordem'}
          </button>
          
          <button
            onClick={handleGerarPdf}
            disabled={gerandoPdf}
            className="px-6 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          >
            {gerandoPdf ? 'Gerando...' : 'Gerar PDF'}
          </button>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-gray-800 text-white mt-12">
        <div className="container mx-auto px-4 py-4 text-center text-sm">
          <p>Sistema de Ordem de Serviço - Migrado de Java Swing para Web</p>
          <p className="text-gray-400 mt-1">
            Spring Boot 3 + React + TypeScript + Tailwind CSS
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
