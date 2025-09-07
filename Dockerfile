# Utiliser OpenJDK 21 slim pour réduire la taille
FROM openjdk:21-jdk-slim

# Métadonnées
LABEL maintainer="mywebcompanion"
LABEL description="My Web Companion Backend - TFE"

# Variables d'environnement pour optimisation
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Répertoire de travail
WORKDIR /app

# Copier le JAR compilé
COPY target/backend-spring-0.0.1-SNAPSHOT.jar app.jar

# Changer le propriétaire
RUN chown appuser:appuser app.jar

# Utiliser l'utilisateur non-root
USER appuser

# Exposer le port
EXPOSE 8080

# Healthcheck pour Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Point d'entrée avec optimisations JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
