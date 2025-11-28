<#
.SYNOPSIS
    Envoie un message Syslog en UDP sur le port 514.
.DESCRIPTION
    Ce script envoie un message Syslog brut à un serveur Syslog en UDP.
    Vous pouvez personnaliser le message et l'adresse IP/port du serveur.
.PARAMETER Server
    Adresse IP ou nom d'hôte du serveur Syslog (par défaut : localhost).
.PARAMETER Port
    Port UDP du serveur Syslog (par défaut : 514).
.PARAMETER Message
    Message Syslog brut à envoyer (par défaut : un exemple de message TRAFFIC).
.EXAMPLE
    .\Send-SyslogMessage.ps1 -Server "192.168.1.100" -Port 514
#>

param (
    [string]$Server = "localhost",
    [int]$Port = 514,
    [string]$Message = "receive_time=2025-10-30T12:34:56.789+02:00, serial=0123456789, type=TRAFFIC, subtype=start, time_generated=2025-10-30T12:34:55.789+02:00, src=192.168.1.100, dst=10.0.0.5, natsrc=192.168.1.100, natdst=10.0.0.5, rule=Allow-Web-Traffic, srcuser=jdoe, app=ssl, action=allow, bytes=1024"
)

# Vérifier que le port est valide
if ($Port -lt 1 -or $Port -gt 65535) {
    Write-Error "Le port doit être compris entre 1 et 65535."
    exit 1
}

# Créer un endpoint UDP
$Endpoint = New-Object System.Net.IPEndPoint([System.Net.IPAddress]::Parse($Server), $Port)

# Créer un client UDP
$UdpClient = New-Object System.Net.Sockets.UdpClient

try {
    # Convertir le message en bytes
    $Bytes = [System.Text.Encoding]::UTF8.GetBytes($Message)

    # Envoyer le message
    $UdpClient.Send($Bytes, $Bytes.Length, $Endpoint)
    Write-Host "Message Syslog envoyé à ${Server}:${Port} :`n$Message" -ForegroundColor Green
}
catch {
    Write-Error "Erreur lors de l'envoi du message Syslog : $_"
}
finally {
    # Fermer le client UDP
    $UdpClient.Close()
}
