# Arquitetura da Migração - Sistema de Ordem de Serviço

## Análise do Sistema Legado

### Funcionalidades Identificadas

O sistema legado Java Swing implementa um **sistema de ordem de serviço para impressão têxtil** com as seguintes características:

1. **Cabeçalho da Ordem**
   - Cliente, Data, Hora (preenchida automaticamente)
   - Papel, Tecido, Largura do Tecido, Largura de Impressão
   - Checkboxes mutuamente exclusivos: Tec Cliente/Tec Sublimatec e Só Impressão/Calandra

2. **Sistema de Abas (Páginas)**
   - Múltiplas páginas com 6 linhas de imagens cada
   - Possibilidade de adicionar/remover páginas dinamicamente

3. **Linhas de Imagem (6 por página)**
   - Upload via Drag & Drop de arquivos locais
   - Campos: REF (nome do arquivo), PASTA (caminho), MTS (metragem)
   - **Crop de imagem**: 3 modos (Esquerda, Direita, Completo)
   - Preview da imagem com miniatura

4. **Processamento de Imagem**
   - Detecção automática de proporção (se largura >= 2x altura, aplica crop)
   - Crop esquerdo: pega os primeiros 2/3 da largura
   - Crop direito: pega os últimos 2/3 da largura
   - Modo completo: mantém imagem inteira proporcional
   - Redimensionamento para 400x220px (impressão) e 200x110px (preview)

5. **Geração de PDF/Impressão**
   - Layout formatado com cabeçalho, checkboxes, observações
   - Blocos de imagem + informações (REF em vermelho/negrito, PASTA em maiúsculas)
   - Campos de controle: Ploteiro, Op. Calandra, Conferente
   - Rodapé com autorização

---

## Arquitetura da Nova Solução

### Decisões Arquiteturais

**Padrão**: **Monolito Modular** (mais adequado para o escopo do projeto)

**Justificativa**:
- Sistema relativamente simples com domínio coeso
- Não há necessidade de escalabilidade independente de módulos
- Facilita desenvolvimento, deploy e manutenção
- Possibilidade futura de migração para microsserviços se necessário

### Stack Tecnológica

#### Backend
- **Java 17+** com **Spring Boot 3.2+**
- **Spring Data JPA** para persistência
- **H2 Database** (desenvolvimento) / **PostgreSQL** (produção)
- **OpenPDF** para geração de PDF
- **Spring Web** para REST API
- **Lombok** para redução de boilerplate

#### Frontend
- **React 18** com **Vite**
- **TypeScript** para type safety
- **Tailwind CSS** para estilização
- **Axios** para comunicação HTTP
- **React Hook Form** para formulários
- **React Dropzone** para upload de imagens

---

## Estrutura do Projeto

### Backend (Spring Boot)

```
ordem-servico-backend/
├── src/main/java/com/ordemservico/
│   ├── OrdemServicoApplication.java
│   ├── config/
│   │   └── CorsConfig.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── OrdemServico.java
│   │   │   └── ItemOrdem.java
│   │   └── enums/
│   │       └── TipoCrop.java
│   ├── repository/
│   │   ├── OrdemServicoRepository.java
│   │   └── ItemOrdemRepository.java
│   ├── service/
│   │   ├── OrdemServicoService.java
│   │   ├── ImagemService.java
│   │   └── PdfService.java
│   ├── controller/
│   │   ├── OrdemServicoController.java
│   │   └── ImagemController.java
│   └── dto/
│       ├── OrdemServicoDTO.java
│       ├── ItemOrdemDTO.java
│       └── PdfRequestDTO.java
├── src/main/resources/
│   ├── application.yml
│   └── uploads/ (imagens armazenadas)
└── pom.xml
```

### Frontend (React + Vite)

```
ordem-servico-frontend/
├── src/
│   ├── components/
│   │   ├── Header.tsx
│   │   ├── FormularioCabecalho.tsx
│   │   ├── LinhaImagem.tsx
│   │   ├── PaginaImagens.tsx
│   │   └── BotaoGerarPDF.tsx
│   ├── services/
│   │   ├── api.ts
│   │   ├── ordemServicoService.ts
│   │   └── imagemService.ts
│   ├── types/
│   │   └── index.ts
│   ├── App.tsx
│   └── main.tsx
├── package.json
├── vite.config.ts
└── tailwind.config.js
```

---

## Modelo de Dados

### Entidade: OrdemServico

```java
@Entity
public class OrdemServico {
    @Id @GeneratedValue
    private Long id;
    
    private String cliente;
    private LocalDate data;
    private LocalTime hora;
    private String papel;
    private String tecido;
    private String larguraTecido;
    private String larguraImpressao;
    
    private Boolean tecCliente;
    private Boolean tecSublimatec;
    private Boolean soImpressao;
    private Boolean calandra;
    
    @OneToMany(mappedBy = "ordemServico", cascade = ALL)
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
    
    private String ref;
    private String pasta;
    private String metragem;
    
    @Enumerated(STRING)
    private TipoCrop tipoCrop; // ESQUERDA, DIREITA, COMPLETO
    
    private String caminhoImagem; // path no servidor
}
```

---

## Fluxo de Dados

### 1. Upload de Imagem

**Frontend**:
```
Usuário arrasta imagem → React Dropzone captura → 
Envia FormData (MultipartFile) → Backend
```

**Backend**:
```
Controller recebe MultipartFile → 
ImagemService processa:
  - Valida formato (jpg, png)
  - Salva em /uploads/{uuid}.ext
  - Retorna caminho relativo
```

### 2. Processamento de Crop

**Frontend**: Usuário seleciona botão (Esquerda/Direita/Completo) → Envia apenas `tipoCrop: 0|1|2`

**Backend** (ao gerar PDF):
```
PdfService.gerarPdf():
  - Carrega imagem original do disco
  - Aplica BufferedImage.getSubimage() conforme tipoCrop
  - Redimensiona para 400x220px
  - Desenha no PDF
```

### 3. Geração de PDF

**Frontend**: Clica "Gerar PDF" → Envia JSON com OrdemServico completa

**Backend**:
```
PdfService.gerarPdf(OrdemServicoDTO):
  - Cria documento OpenPDF
  - Desenha cabeçalho com dados da ordem
  - Itera sobre itens:
    - Processa imagem com crop
    - Desenha bloco com imagem + informações
  - Retorna byte[] do PDF
```

**Frontend**: Recebe blob → Cria URL temporária → Abre em nova aba / Download

---

## Endpoints REST

### OrdemServicoController

```
POST   /api/ordens              - Criar nova ordem
GET    /api/ordens              - Listar ordens
GET    /api/ordens/{id}         - Buscar ordem por ID
PUT    /api/ordens/{id}         - Atualizar ordem
DELETE /api/ordens/{id}         - Deletar ordem
POST   /api/ordens/{id}/pdf     - Gerar PDF da ordem
```

### ImagemController

```
POST   /api/imagens/upload      - Upload de imagem (retorna path)
GET    /api/imagens/{filename}  - Servir imagem estática
```

---

## Estratégia de Armazenamento de Imagens

**Opção Escolhida**: **Armazenamento em disco local**

**Justificativa**:
- Simplicidade de implementação
- Melhor performance (sem overhead de Base64)
- Facilita backup e migração
- Possibilidade futura de migração para S3/Cloud Storage

**Estrutura**:
```
/uploads/
  ├── {uuid-1}.jpg
  ├── {uuid-2}.png
  └── ...
```

**Alternativa (Base64)**: Descartada devido a:
- Aumento de 33% no tamanho dos dados
- Overhead no banco de dados
- Dificuldade de cache e CDN

---

## Considerações de Segurança

1. **Validação de Upload**:
   - Limitar tipos MIME (image/jpeg, image/png)
   - Limitar tamanho máximo (60MB)
   - Sanitizar nomes de arquivo

2. **CORS**:
   - Configurar origens permitidas
   - Métodos HTTP específicos

3. **Validação de Entrada**:
   - Bean Validation no backend
   - Validação de formulário no frontend

---

## Próximos Passos

1. ✅ Análise do código legado
2. ⏳ Criar estrutura do projeto backend
3. ⏳ Implementar entidades e repositórios
4. ⏳ Desenvolver serviços de negócio
5. ⏳ Criar controllers REST
6. ⏳ Implementar geração de PDF
7. ⏳ Criar projeto frontend
8. ⏳ Desenvolver componentes React
9. ⏳ Integrar frontend com backend
10. ⏳ Testes e documentação
