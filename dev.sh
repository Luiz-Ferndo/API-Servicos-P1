#!/bin/bash

get_script_path() {
    local SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do
        local DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
        SOURCE="$(readlink "$SOURCE")"
        [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
    done
    echo "$( cd -P "$( dirname "$SOURCE" )" && pwd )/$( basename "$SOURCE" )"
}

SCRIPT_PATH=$(get_script_path)

INSTALL_MARKER="# DEV_SCRIPT_COMMAND_AUTOMATION"

install_command() {
    local shell_config_file=""
    if [[ "$SHELL" == *"bash"* ]]; then
        shell_config_file="$HOME/.bashrc"
    elif [[ "$SHELL" == *"zsh"* ]]; then
        shell_config_file="$HOME/.zshrc"
    else
        echo "❌ Shell não suportado. Apenas Bash e Zsh são compatíveis."
        return 1
    fi

    echo "⚙️  Configurando para o arquivo: $shell_config_file"

    if grep -q "$INSTALL_MARKER" "$shell_config_file"; then
        echo "✅ O comando 'dev' já está instalado. Nada a fazer."
        return 0
    fi

    echo "📝 Adicionando o comando 'dev' ao seu shell..."
    {
        echo ""
        echo "$INSTALL_MARKER"
        echo "dev() {"
        echo "    \"$SCRIPT_PATH\" \"\$@\""
        echo "}"
    } >> "$shell_config_file"

    echo ""
    echo "✅ Instalação concluída com sucesso!"
    echo "👉 Para aplicar as mudanças, execute 'source $shell_config_file' ou reinicie seu terminal."
}

uninstall_command() {
    local shell_config_file=""
    if [[ "$SHELL" == *"bash"* ]]; then
        shell_config_file="$HOME/.bashrc"
    elif [[ "$SHELL" == *"zsh"* ]]; then
        shell_config_file="$HOME/.zshrc"
    else
        echo "❌ Shell não suportado."
        return 1
    fi

    if ! grep -q "$INSTALL_MARKER" "$shell_config_file"; then
        echo "ℹ️  O comando 'dev' não parece estar instalado. Nada a fazer."
        return 0
    fi

    echo "🗑️  Removendo o comando 'dev' do seu shell..."

    cp "$shell_config_file" "${shell_config_file}.bak"
    echo "📦 Backup do seu arquivo de configuração foi criado em: ${shell_config_file}.bak"

    sed -i.bak "/$INSTALL_MARKER/,/}/d" "$shell_config_file"

    echo "✅ Desinstalação concluída!"
    echo "👉 As mudanças serão aplicadas na próxima vez que você iniciar o terminal."
}

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
    "$0" down
    "$0" up -d
    "$0" logs
    ;;

  install)
    install_command
    ;;

  uninstall)
    uninstall_command
    ;;

  *)
    echo "Comandos disponíveis:"
    echo "  ./dev.sh build       # Compila o JAR"
    echo "  ./dev.sh up          # Sobe app + banco"
    echo "  ./dev.sh down        # Para os containers"
    echo "  ./dev.sh logs        # Ver logs em tempo real"
    echo "  ./dev.sh clean       # Remove tudo (containers e volumes)"
    echo "  ./dev.sh dev         # Sobe o ambiente de desenvolvimento completo"
    echo ""
    echo "Comandos de Automação:"
    echo "  ./dev.sh install     # Instala o comando 'dev' nativamente no seu terminal"
    echo "  ./dev.sh uninstall   # Remove o comando 'dev' do seu terminal"
    ;;
esac