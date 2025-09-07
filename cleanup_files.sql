-- Script pour supprimer la table files et nettoyer les références
-- À exécuter sur la base de données mywebcompanion

-- Supprimer la table files si elle existe
DROP TABLE IF EXISTS files;

-- Vérifier que toutes les références ont été supprimées
-- (Cette requête ne devrait retourner aucun résultat)
SELECT 
    TABLE_NAME, 
    COLUMN_NAME,
    CONSTRAINT_NAME
FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE 
    REFERENCED_TABLE_NAME = 'files' 
    AND TABLE_SCHEMA = 'mywebcompanion';
