# 🚀 Guia de Início Rápido

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- ✅ **Java 17+** - [Download](https://adoptium.net/)
- ✅ **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- ✅ **Node.js 18+** - [Download](https://nodejs.org/)
- ✅ **pnpm** - Instale com: `npm install -g pnpm`

---

## 🏃 Executar a Aplicação

### Opção 1: Execução Automática (Recomendado)

#### 1. Backend

```bash
cd ordem-servico-backend
./run.sh
```

O backend estará rodando em: **http://localhost:8080**

#### 2. Frontend (em outro terminal)

```bash
cd ordem-servico-frontend
pnpm install
pnpm dev
```

O frontend estará rodando em: **http://localhost:5173**

### Opção 2: Execução Manual

#### Backend

```bash
cd ordem-servico-backend

# Compilar
mvn clean install -DskipTests

# Executar
mvn spring-boot:run
```

#### Frontend

```bash
cd ordem-servico-frontend

# Instalar dependências (primeira vez)
pnpm install

# Executar em modo desenvolvimento
pnpm dev
```

---

## 📝 Testar a Aplicação

### 1. Acessar o Frontend

Abra o navegador em: **http://localhost:5173**

### 2. Criar uma Ordem de Serviço

1. Preencha o campo **Cliente** (obrigatório)
2. Ajuste **Data** e **Hora** se necessário
3. Preencha campos opcionais: Papel, Tecido, Larguras
4. Selecione os checkboxes desejados

### 3. Adicionar Imagens

Para cada linha:

1. **Arraste uma imagem** (JPG ou PNG) para a área de drop
2. Ou **clique** na área para selecionar um arquivo
3. Preencha **REF**, **PASTA** e **MTS** (opcional)
4. Escolha o modo de **Crop**:
   - **⬅**: Cortar lado esquerdo
   - **⬌**: Imagem completa
   - **➡**: Cortar lado direito

### 4. Adicionar Mais Páginas

- Clique em **+ Página** para adicionar mais 6 linhas de imagem
- Navegue entre páginas usando as abas
- Remova páginas clicando no **×** (mínimo 1 página)

### 5. Salvar e Gerar PDF

- Clique em **Salvar Ordem** para persistir no banco de dados
- Clique em **Gerar PDF** para criar o documento formatado
- O PDF será aberto em uma nova aba do navegador

---

## 🗄️ Acessar o Banco de Dados (H2 Console)

Durante o desenvolvimento, você pode acessar o console H2:

**URL:** http://localhost:8080/h2-console

**Credenciais:**
- JDBC URL: `jdbc:h2:mem:ordemservicodb`
- Username: `sa`
- Password: (deixe em branco)

**Tabelas disponíveis:**
- `ORDEM_SERVICO`
- `ITEM_ORDEM`

---

## 🔍 Testar a API REST

### Usando cURL

#### Listar todas as ordens

```bash
curl http://localhost:8080/api/ordens
```

#### Criar uma ordem

```bash
curl -X POST http://localhost:8080/api/ordens \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Empresa XYZ",
    "data": "2024-01-15",
    "hora": "14:30",
    "papel": "Papel A",
    "tecido": "Poliéster",
    "larguraTecido": "1.60m",
    "larguraImpressao": "1.50m",
    "tecCliente": true,
    "tecSublimatec": false,
    "soImpressao": false,
    "calandra": true,
    "itens": []
  }'
```

#### Upload de imagem

```bash
curl -X POST http://localhost:8080/api/imagens/upload \
  -F "file=@/caminho/para/imagem.jpg"
```

### Usando Postman/Insomnia

Importe a coleção de endpoints:

**Base URL:** `http://localhost:8080/api`

**Endpoints principais:**
- `GET /ordens` - Listar ordens
- `POST /ordens` - Criar ordem
- `GET /ordens/{id}` - Buscar ordem
- `POST /ordens/pdf/preview` - Gerar PDF
- `POST /imagens/upload` - Upload de imagem

---

## 🐛 Troubleshooting

### Erro: "Port 8080 already in use"

**Solução:** Mude a porta no `application.yml`:

```yaml
server:
  port: 8081
```

E atualize a URL base no frontend (`src/services/api.ts`):

```typescript
baseURL: 'http://localhost:8081/api'
```

### Erro: "Cannot connect to backend"

**Verificações:**

1. Backend está rodando? Verifique: `curl http://localhost:8080/api/ordens`
2. CORS configurado? Verifique `CorsConfig.java`
3. Firewall bloqueando? Desative temporariamente

### Erro: "Failed to upload image"

**Verificações:**

1. Tamanho do arquivo < 60MB?
2. Formato JPG ou PNG?
3. Diretório de uploads existe? Verifique `application.yml`

### Erro de compilação do backend

```bash
# Limpar cache do Maven
mvn clean

# Recompilar
mvn install -DskipTests
```

### Erro de dependências do frontend

```bash
# Limpar node_modules e reinstalar
rm -rf node_modules
pnpm install
```

---

## 📊 Estrutura de Diretórios

```
ordem-servico-web/
├── ordem-servico-backend/       # Backend Spring Boot
│   ├── src/
│   │   ├── main/java/           # Código Java
│   │   └── main/resources/      # Configurações e uploads
│   ├── pom.xml                  # Dependências Maven
│   └── run.sh                   # Script de execução
│
├── ordem-servico-frontend/      # Frontend React
│   ├── src/
│   │   ├── components/          # Componentes React
│   │   ├── services/            # Serviços de API
│   │   └── types/               # Tipos TypeScript
│   ├── package.json             # Dependências npm
│   └── vite.config.ts           # Configuração Vite
│
├── ARQUITETURA.md               # Documentação da arquitetura
├── README.md                    # Documentação completa
└── QUICKSTART.md                # Este arquivo
```

---

## 🎯 Próximos Passos

Após executar a aplicação com sucesso:

1. ✅ Explore a interface e teste todas as funcionalidades
2. ✅ Leia a documentação completa em `README.md`
3. ✅ Revise a arquitetura em `ARQUITETURA.md`
4. ✅ Customize conforme suas necessidades
5. ✅ Configure para produção (PostgreSQL, deploy)

---

## 📚 Recursos Adicionais

- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **React Docs:** https://react.dev/
- **TypeScript Docs:** https://www.typescriptlang.org/docs/
- **Tailwind CSS:** https://tailwindcss.com/docs

---

## 💡 Dicas

- Use **Ctrl+C** para parar o backend/frontend
- Logs do backend aparecem no terminal
- Hot reload está ativado no frontend (mudanças refletem automaticamente)
- Use o console H2 para inspecionar o banco de dados
- Abra as DevTools do navegador (F12) para debug do frontend

---

**Pronto! Sua aplicação está rodando! 🎉**

Se encontrar problemas, consulte a seção de Troubleshooting ou a documentação completa.
