#!/bin/bash

case "$1" in
  build)
    echo "🔨 Compilando o projeto..."
    ./mvnw clean package -DskipTests
    ;;

  up)
    echo "🚀 Subindo containers..."
    docker compose up -d --build
    ;;

  down)
    echo "🛑 Derrubando containers..."
    docker compose down
    ;;

  logs)
    docker compose logs -f
    ;;

  clean)
    echo "🧹 Limpando volumes e containers..."
    docker compose down -v
    docker system prune -f
    ;;

  dev)
    echo "🔄 Executando o modo de desenvolvimento..."
    docker compose down -v
    docker system prune -f
    docker compose --env-file .env up -d --build
    echo "Acesse o app em http://localhost:8080"
    ;;

  *)
    echo "Comandos disponíveis:"
    echo "  ./dev.sh build     # Compila o JAR"
    echo "  ./dev.sh up        # Sobe app + banco"
    echo "  ./dev.sh down      # Para os containers"
    echo "  ./dev.sh logs      # Ver logs em tempo real"
    echo "  ./dev.sh clean     # Remove tudo"
    echo "  ./dev.sh dev       # Executa o modo de desenvolvimento"
    ;;
esac
