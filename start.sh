#!/bin/bash
set -e

# Vérifier que le JAR existe
if [ ! -f "target/backend-spring-0.0.1-SNAPSHOT.jar" ]; then
    echo "Erreur: JAR non trouvé dans target/"
    ls -la target/
    exit 1
fi

# Démarrer l'application avec les bonnes options
exec java \
    -Dserver.port=${PORT:-8080} \
    -Dspring.profiles.active=railway \
    -Xmx512m \
    -Xms256m \
    -jar target/backend-spring-0.0.1-SNAPSHOT.jar
