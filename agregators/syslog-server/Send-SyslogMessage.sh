#!/bin/bash

# En-tête du script
echo "
Envoie un message Syslog en UDP sur le port 514.

Ce script envoie un message Syslog brut à un serveur Syslog en UDP.
Vous pouvez personnaliser le message et l'adresse IP/port du serveur.

Utilisation : $0 [-s serveur] [-p port] [-m message]

Exemple : $0 -s 192.168.1.100 -p 514
"

# Valeurs par défaut
SERVER="localhost"
PORT=514
MESSAGE="<190>Nov 27 17:37:08 frvalfw11 1,2025/11/27 17:37:07,013201030824,TRAFFIC,end,2817,2025/11/27 17:37:07,192.168.0.1,10.114.214.8,0.0.0.0,0.0.0.0,VLAN801_TO_ANY,,,incomplete,vsys1,Zone_OT_DMZ_801,Zone_OT_DMZ,ae1.801,ae1.51,default,2025/11/27 17:37:07,2780227,1,34252,4662,0,0,0x1b,tcp,allow,142,78,64,2,2025/11/27 17:37:01,0,any,,7536179828408605381,0x8000000000000000,10.0.0.0-10.255.255.255,10.0.0.0-10.255.255.255,,1,1,tcp-rst-from-server,15,16,0,0,vsys1,frvalfw11,from-policy,,,0,,0,,N/A,0,0,0,0,37a6ae07-09a2-4c1d-8641-a18c1c610803,0,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,2025-11-27T17:37:08.128+01:00,,,unknown,unknown,unknown,1,,,incomplete,no,no,0,NonProxyTraffic,"


# Traitement des arguments
while getopts "s:p:m:" opt; do
  case $opt in
    s)
      SERVER="$OPTARG"
      ;;
    p)
      PORT="$OPTARG"
      ;;
    m)
      MESSAGE="$OPTARG"
      ;;
    \?)
      echo "Option invalide: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG nécessite un argument." >&2
      exit 1
      ;;
  esac
done

# Vérifier que le port est valide
if [ "$PORT" -lt 1 ] || [ "$PORT" -gt 65535 ]; then
    echo "Erreur : Le port doit être compris entre 1 et 65535." >&2
    exit 1
fi

# Afficher les paramètres utilisés
echo "Envoi d'un message Syslog à $SERVER:$PORT"
echo "Message : $MESSAGE"

# Envoyer le message Syslog en UDP
echo -ne "$MESSAGE" | nc -w1 -u "$SERVER" "$PORT"

# Vérifier si le message a été envoyé avec succès
if [ $? -eq 0 ]; then
    echo -e "\nMessage Syslog envoyé avec succès à ${SERVER}:${PORT} :\n$MESSAGE" | sed 's/^/  /'
else
    echo "Erreur lors de l'envoi du message Syslog." >&2
    exit 1
fi
