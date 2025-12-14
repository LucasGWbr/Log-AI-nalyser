# 🤖 Log AI Analyser

> **Análise inteligente de erros de software utilizando IA Generativa Local.**

O **Log AI Analyser** é uma ferramenta Full-Stack que ingere logs de erro (Stack Traces), limpa os dados irrelevantes, verifica recorrências e utiliza um LLM (Llama 3) rodando localmente para diagnosticar a causa raiz e sugerir correções de código.

---

## 🚀 Diferenciais Técnicos

* **🔒 Privacidade Total:** A IA roda 100% local (Ollama). Nenhum dado sensível sai da sua máquina.
* **⚡ Smart Caching:** Se o mesmo erro ocorrer novamente (mesmo com datas diferentes), a resposta é retornada do banco em milissegundos, sem gastar processamento da IA.
* **🧹 Filtro de Ruído Inteligente:** Algoritmo capaz de limpar Timestamps e IDs variáveis de logs em Java, Python, Go, Node.js e Logs de Sistema, garantindo precisão no cache.
* **🛡️ Segurança:** Otimizado para cortar logs gigantes e prevenir travamentos ou custos excessivos de tokens.

---

## 🛠️ Tech Stack

### Backend
* **Java 21**
* **Spring Boot 3** (Web, Data JPA)
* **Spring AI** (Integração com LLMs)
* **PostgreSQL** (Persistência)
* **Ollama** (Servidor de Inferência de IA)

### Frontend
* **React** (Vite)
* **Tailwind CSS** (Estilização Dark Mode)
* **Lucide React** (Ícones)
* **Syntax Highlighter** (Visualização de código)

### Infraestrutura
* **Docker** & **Docker Compose**

---

## 📋 Pré-requisitos

Você só precisa de **uma** ferramenta instalada:

* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

*(Não é necessário instalar Java, Node.js ou PostgreSQL localmente).*

---

## ⚡ Como Executar o Projeto

Siga os passos abaixo para subir o ambiente completo.

### 1. Clonar o repositório
```bash
git clone https://github.com/LucasGWbr/Log-AI-nalyser.git
cd log-ai-nalyser
```

### 2. Subir os containers
Este comando irá compilar o Backend e Frontend e iniciar o Banco de Dados.
```bash
docker compose up -d --build
```

### 3. Baixar o Modelo de IA (Apenas na 1ª vez)
O container do Ollama inicia vazio. Precisamos baixar o "cérebro" (Llama 3). Execute no terminal:
```bash
docker exec -it log-ai-ollama ollama run llama3
```
Aguarde o download (aprox. 4.7GB). Quando aparecer o prompt >>>, digite /bye para sair.

### 4. Acessar
    - Frontend: http://localhost:5173

    - Backend API: http://localhost:8080
