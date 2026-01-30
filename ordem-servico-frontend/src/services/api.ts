import axios from 'axios';

/**
 * Instância configurada do Axios para comunicação com o backend.
 */
const api = axios.create({
  baseURL: 'https://back-production-18e4.up.railway.app/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 120000, // 120 segundos
});

// Interceptor para logging de requisições (desenvolvimento)
api.interceptors.request.use(
  (config) => {
    console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('[API] Erro na requisição:', error);
    return Promise.reject(error);
  }
);

// Interceptor para tratamento de erros
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      // Erro retornado pelo servidor
      console.error('[API] Erro do servidor:', error.response.status, error.response.data);
    } else if (error.request) {
      // Requisição foi feita mas não houve resposta
      console.error('[API] Sem resposta do servidor');
    } else {
      // Erro ao configurar a requisição
      console.error('[API] Erro:', error.message);
    }
    return Promise.reject(error);
  }
);

export default api;
