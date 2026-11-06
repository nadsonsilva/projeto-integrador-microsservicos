# Execute na raiz da pasta, com ambos os microsserviços iniciados.
$ErrorActionPreference = "Stop"
$evento = Get-Content "exemplos/criar-evento.json" -Raw
$criado = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/events" -ContentType "application/json" -Body $evento
Write-Host "Evento criado, ID: $($criado.id)"
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/events" | ConvertTo-Json -Depth 5
$inscricao = Get-Content "exemplos/inscrever-participante.json" -Raw
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/events/$($criado.id)" -ContentType "application/json" -Body $inscricao | ConvertTo-Json
Write-Host "Notificacoes registradas no serviço de email:"
Invoke-RestMethod -Method Get -Uri "http://localhost:8090/api/email/history" | ConvertTo-Json -Depth 5
