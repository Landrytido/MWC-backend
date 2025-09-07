# Script pour nettoyer les commentaires inutiles dans les fichiers Java

$projectPath = "e:\CODE\StagE\my-web-companion\backend-spring\src\main\java"

# Patterns de commentaires à supprimer
$patternsToRemove = @(
    "// src/main/java/.*",
    "// Version mise à jour",
    "// Méthodes existantes mises à jour",
    "// Nouvelles méthodes pour.*",
    "// Nouvelle méthode.*",
    "// NOUVEAUX ENDPOINTS.*",
    "// CHANGÉ:.*",
    "// Maintenant les types correspondent.*",
    "// Méthodes de mapping depuis.*",
    "// Location.*",
    "// Current weather.*",
    "// Forecast.*",
    "// Si.*est null.*",
    "// Vérifier que.*",
    "// Optimisation:.*",
    "// Définir l'ordre.*",
    "// Statut calculé.*",
    "^\s*//\s*$"  # Lignes avec seulement des commentaires vides
)

# Trouver tous les fichiers Java
$javaFiles = Get-ChildItem -Path $projectPath -Recurse -Filter "*.java"

foreach ($file in $javaFiles) {
    Write-Host "Nettoyage de $($file.FullName)"
    
    $content = Get-Content $file.FullName -Encoding UTF8
    $originalLineCount = $content.Count
    
    # Supprimer les lignes qui correspondent aux patterns
    foreach ($pattern in $patternsToRemove) {
        $content = $content | Where-Object { $_ -notmatch $pattern }
    }
    
    # Supprimer les lignes vides multiples (garder max 1 ligne vide consécutive)
    $cleanedContent = @()
    $previousLineEmpty = $false
    
    foreach ($line in $content) {
        $isCurrentLineEmpty = [string]::IsNullOrWhiteSpace($line)
        
        if (-not ($isCurrentLineEmpty -and $previousLineEmpty)) {
            $cleanedContent += $line
        }
        
        $previousLineEmpty = $isCurrentLineEmpty
    }
    
    # Sauvegarder seulement si des changements ont été effectués
    if ($cleanedContent.Count -ne $originalLineCount) {
        $cleanedContent | Set-Content $file.FullName -Encoding UTF8
        Write-Host "  → $($originalLineCount - $cleanedContent.Count) lignes supprimées"
    } else {
        Write-Host "  → Aucun changement"
    }
}

Write-Host "Nettoyage terminé!"
