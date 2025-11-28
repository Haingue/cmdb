param (
    [string]$Server = "localhost",
    [int]$Port = 601,
    [string]$Message = "receive_time=2025-10-30T12:34:56.789+02:00, serial=0123456789, type=TRAFFIC, subtype=start, time_generated=2025-10-30T12:34:55.789+02:00, src=192.168.1.100, dst=10.0.0.5, natsrc=192.168.1.100, natdst=10.0.0.5, rule=Allow-Web-Traffic, srcuser=jdoe, app=ssl, action=allow, bytes=1024"
)

$tcpClient = New-Object System.Net.Sockets.TcpClient
try {
    $tcpClient.Connect($Server, $Port)
    $stream = $tcpClient.GetStream()
    $writer = New-Object System.IO.StreamWriter($stream)
    $writer.WriteLine($Message)
    $writer.Flush()
    Write-Host "Message syslog envoyé avec succès !"
}
catch {
    Write-Host "Échec de l'envoi du message syslog"
    Write-Host "Erreur : $_"
}
finally {
    $tcpClient.Close()
}
