# Sistema de Ordem de Serviço - Impressão Têxtil

Sistema web moderno para gerenciamento de ordens de serviço de impressão têxtil, migrado de uma aplicação legado Java Swing para arquitetura web com **Spring Boot 3** e **React/TypeScript**.

---

## 📋 Visão Geral

Este projeto replica todas as funcionalidades do sistema legado desktop, incluindo:

- ✅ Gerenciamento de ordens de serviço com informações de cliente, papel, tecido
- ✅ Upload de imagens via drag & drop
- ✅ Processamento de corte de imagens (esquerda, direita, completo)
- ✅ Sistema de múltiplas páginas (6 imagens por página)
- ✅ Geração de PDF formatado para impressão
- ✅ Checkboxes mutuamente exclusivos (Tec Cliente/Sublimatec, Só Impressão/Calandra)
- ✅ Interface responsiva e moderna com Tailwind CSS

---

## 🏗️ Arquitetura

### Backend (Spring Boot 3)

**Tecnologias:**
- Java 17+
- Spring Boot 3.2.1
- Spring Data JPA
- H2 Database (desenvolvimento) / PostgreSQL (produção)
- OpenPDF para geração de PDF
- Lombok

**Estrutura:**
```
ordem-servico-backend/
├── src/main/java/com/ordemservico/
│   ├── OrdemServicoApplication.java
│   ├── config/          # Configurações (CORS, Upload)
│   ├── domain/
│   │   ├── entity/      # Entidades JPA
│   │   └── enums/       # Enumerações
│   ├── repository/      # Repositórios Spring Data
│   ├── service/         # Lógica de negócio
│   ├── controller/      # Controllers REST
│   └── dto/             # Data Transfer Objects
└── src/main/resources/
    ├── application.yml
    └── uploads/         # Armazenamento de imagens
```

### Frontend (React + TypeScript)

**Tecnologias:**
- React 18
- TypeScript
- Vite
- Tailwind CSS
- Axios
- React Dropzone

**Estrutura:**
```
ordem-servico-frontend/
├── src/
│   ├── components/      # Componentes React
│   ├── services/        # Serviços de API
│   ├── types/           # Tipos TypeScript
│   ├── App.tsx          # Componente principal
│   └── main.tsx         # Entry point
└── package.json
```

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 17+** instalado
- **Maven 3.8+** instalado
- **Node.js 18+** e **pnpm** instalados

### 1. Backend (Spring Boot)

```bash
cd ordem-servico-backend

# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

O backend estará disponível em: **http://localhost:8080**

**Console H2** (desenvolvimento): http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:ordemservicodb`
- Username: `sa`
- Password: (vazio)

### 2. Frontend (React)

```bash
cd ordem-servico-frontend

# Instalar dependências
pnpm install

# Executar em modo desenvolvimento
pnpm dev
```

O frontend estará disponível em: **http://localhost:5173**

---

## 📡 API Endpoints

### Ordem de Serviço

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/ordens` | Criar nova ordem |
| GET | `/api/ordens` | Listar todas as ordens |
| GET | `/api/ordens/{id}` | Buscar ordem por ID |
| PUT | `/api/ordens/{id}` | Atualizar ordem |
| DELETE | `/api/ordens/{id}` | Deletar ordem |
| GET | `/api/ordens/buscar?cliente=nome` | Buscar por cliente |
| POST | `/api/ordens/{id}/pdf` | Gerar PDF da ordem |
| POST | `/api/ordens/pdf/preview` | Gerar preview de PDF |

### Imagens

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/imagens/upload` | Upload de imagem |
| GET | `/api/imagens/existe/{filename}` | Verificar existência |
| DELETE | `/api/imagens/{filename}` | Deletar imagem |
| GET | `/uploads/{filename}` | Servir imagem estática |

---

## 🎨 Funcionalidades

### 1. Formulário de Cabeçalho

Campos para informações da ordem:
- Cliente (obrigatório)
- Data e Hora (preenchidas automaticamente)
- Papel, Tecido, Largura do Tecido, Largura de Impressão
- Checkboxes mutuamente exclusivos:
  - **Tec Cliente** ⟷ **Tec Sublimatec**
  - **Só Impressão** ⟷ **Calandra**

### 2. Sistema de Páginas

- Múltiplas páginas com **6 linhas de imagem** cada
- Adicionar/remover páginas dinamicamente
- Navegação por abas

### 3. Upload e Processamento de Imagens

- **Drag & Drop** ou clique para selecionar
- Formatos aceitos: JPG, PNG (máx 60MB)
- Campos: REF, PASTA, MTS (metragem)
- **Modos de Crop**:
  - **⬅ Esquerda**: Corta mantendo lado esquerdo
  - **⬌ Completo**: Mantém imagem inteira
  - **➡ Direita**: Corta mantendo lado direito

### 4. Geração de PDF

- Layout formatado replicando o sistema legado
- Imagens processadas com crop aplicado
- Campos de controle: Ploteiro, Calandra, Conferente
- Download/preview direto no navegador

---

## 🔄 Migração do Sistema Legado

### Mapeamento de Funcionalidades

| Sistema Legado (Swing) | Sistema Web |
|------------------------|-------------|
| `TextEditor` (JFrame) | `App.tsx` (React) |
| `LinhaPanel` (JPanel) | `LinhaImagem.tsx` + `PaginaImagens.tsx` |
| `OrdemServico` (classe) | `OrdemServico` (entidade JPA + DTO) |
| `PrintUtils` (impressão) | `PdfService` (geração de PDF) |
| Drag & Drop (AWT) | React Dropzone |
| `BufferedImage.getSubimage()` | `ImagemService.aplicarCrop()` |
| Arrays locais | Entidades relacionais JPA |

### Lógica de Crop Preservada

O algoritmo de corte foi fielmente replicado:

```java
// Sistema Legado (LinhaPanel, linhas 274-292)
if (alinhamentos[currentIndex] == 2) {
    cropped = img; // Completo
} else if (cropX >= cropY * 2) {
    int cropWidth = cropY * 2;
    int x = (alinhamentos[currentIndex] == 0) ? 0 : (cropX - cropWidth);
    cropped = img.getSubimage(x, 0, cropWidth, cropY);
}

// Sistema Web (ImagemService.java)
if (tipoCrop == TipoCrop.COMPLETO) {
    return imagem;
}
if (largura >= altura * 2) {
    int larguraCorte = altura * 2;
    int x = (tipoCrop == TipoCrop.ESQUERDA) ? 0 : (largura - larguraCorte);
    return imagem.getSubimage(x, 0, larguraCorte, altura);
}
```

---

## 🗄️ Modelo de Dados

### Entidade: OrdemServico

```java
@Entity
public class OrdemServico {
    @Id @GeneratedValue
    private Long id;
    
    private String cliente;
    private LocalDate data;
    private LocalTime hora;
    private String papel, tecido;
    private String larguraTecido, larguraImpressao;
    
    private Boolean tecCliente, tecSublimatec;
    private Boolean soImpressao, calandra;
    
    @OneToMany(mappedBy = "ordemServico")
    private List<ItemOrdem> itens;
}
```

### Entidade: ItemOrdem

```java
@Entity
public class ItemOrdem {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne
    private OrdemServico ordemServico;
    
    private Integer numeroPagina;
    private Integer numeroLinha;
    
    private String ref, pasta, metragem;
    
    @Enumerated(STRING)
    private TipoCrop tipoCrop;
    
    private String caminhoImagem;
}
```

---

## 🔧 Configuração

### Backend - application.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ordemservicodb
    username: sa
    password: 
  
  servlet:
    multipart:
      max-file-size: 60MB
      max-request-size: 60MB

app:
  upload:
    dir: ${user.home}/ordem-servico-web/ordem-servico-backend/src/main/resources/uploads

server:
  port: 8080
```

### Frontend - Configuração de API

Arquivo: `src/services/api.ts`

```typescript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 120000,
});
```

---

## 📦 Build para Produção

### Backend

```bash
cd ordem-servico-backend
mvn clean package -DskipTests

# Executar JAR gerado
java -jar target/ordem-servico-backend-1.0.0.jar --spring.profiles.active=prod
```

### Frontend

```bash
cd ordem-servico-frontend
pnpm build

# Arquivos gerados em: dist/
```

---

## 🧪 Testes

### Backend

```bash
cd ordem-servico-backend
mvn test
```

### Frontend

```bash
cd ordem-servico-frontend
pnpm test
```

---

## 📝 Melhorias Futuras

- [ ] Autenticação e autorização (Spring Security + JWT)
- [ ] Migração de armazenamento de imagens para S3/Cloud Storage
- [ ] Busca avançada e filtros
- [ ] Dashboard com estatísticas
- [ ] Notificações em tempo real (WebSocket)
- [ ] Exportação para Excel
- [ ] Histórico de alterações (audit log)
- [ ] Testes E2E com Cypress
- [ ] CI/CD com GitHub Actions
- [ ] Containerização com Docker

---

## 👨‍💻 Desenvolvimento

### Estrutura de Branches

- `main`: Produção
- `develop`: Desenvolvimento
- `feature/*`: Novas funcionalidades
- `bugfix/*`: Correções de bugs

### Padrões de Código

- **Backend**: Google Java Style Guide
- **Frontend**: ESLint + Prettier
- **Commits**: Conventional Commits

---

## 📄 Licença

Este projeto é proprietário e confidencial.

---

## 🤝 Contribuição

Para contribuir com o projeto:

1. Crie uma branch a partir de `develop`
2. Implemente as alterações
3. Execute os testes
4. Crie um Pull Request

---

## 📧 Contato

Para dúvidas ou suporte, entre em contato com a equipe de desenvolvimento.

---

**Desenvolvido com ❤️ usando Spring Boot 3 + React + TypeScript**
