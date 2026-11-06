#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
echo "Criando evento..."
curl -fsS -X POST "http://localhost:8080/events" -H "Content-Type: application/json" --data-binary @exemplos/criar-evento.json
echo
echo "Copie o 'id' retornado e use-o nas próximas chamadas:"
echo "curl -X POST http://localhost:8080/events/ID_DO_EVENTO -H 'Content-Type: application/json' --data-binary @exemplos/inscrever-participante.json"
echo "curl http://localhost:8090/api/email/history"
