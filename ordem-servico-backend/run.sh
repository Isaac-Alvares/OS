#!/bin/bash

echo "========================================="
echo "  Ordem de Serviço - Backend"
echo "  Spring Boot 3 + Java 17"
echo "========================================="
echo ""

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Por favor, instale o Maven 3.8+"
    exit 1
fi

# Verificar versão do Java
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ é necessário. Versão atual: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detectado"
echo "✅ Maven detectado"
echo ""

# Compilar e executar
echo "🔨 Compilando o projeto..."
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "🚀 Iniciando aplicação..."
    echo "📡 Backend disponível em: http://localhost:8080"
    echo "🗄️  Console H2: http://localhost:8080/h2-console"
    echo ""
    mvn spring-boot:run
else
    echo ""
    echo "❌ Erro na compilação"
    exit 1
fi
