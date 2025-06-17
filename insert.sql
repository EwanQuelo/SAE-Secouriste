-- ==========================================================================
-- SCRIPT DE REMPLISSAGE FINAL
-- Version la plus robuste : laisse AUTO_INCREMENT gérer les IDs.
-- ==========================================================================

USE secours2030;

-- --------------------------------------------------------------------------
-- ÉTAPE 1 : VIDAGE DES TABLES EXISTANTES
-- --------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Affectation;
TRUNCATE TABLE ABesoin;
TRUNCATE TABLE EstDisponible;
TRUNCATE TABLE Possede;
TRUNCATE TABLE Necessite;
TRUNCATE TABLE CompteUtilisateur;
TRUNCATE TABLE DPS;
TRUNCATE TABLE Secouriste;
TRUNCATE TABLE Competence;
TRUNCATE TABLE Journee;
TRUNCATE TABLE Site;
TRUNCATE TABLE Sport;
SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------------------------
-- ÉTAPE 2 : REMPLISSAGE DES DONNÉES
-- --------------------------------------------------------------------------

-- 2.1 Données de base
INSERT INTO Competence (intitule) VALUES ('CO'), ('CP'), ('CE'), ('PBC'), ('PBF'), ('PSE1'), ('PSE2'), ('SSA'), ('VPSP');
INSERT INTO Necessite (intituleCompetence, competenceRequise) VALUES ('CO', 'CP'), ('CP', 'CE'), ('CE', 'PSE2'), ('PSE2', 'PSE1'), ('SSA', 'PSE1'), ('VPSP', 'PSE2'), ('PBF', 'PBC');
INSERT INTO Site (code, nom, longitude, latitude) VALUES ('CRCHV', 'Courchevel - Le Praz', 6.6335, 45.4153), ('MRBL', 'Méribel - Chaudanne', 6.5665, 45.3967), ('VALDI', 'Val d''Isère - La Daille', 6.9800, 45.4481), ('LPLGN', 'La Plagne - Piste de Bobsleigh', 6.6742, 45.5065);
INSERT INTO Sport (code, nom) VALUES ('SKI-ALP-DH', 'Ski Alpin - Descente'), ('SKI-ALP-SL', 'Ski Alpin - Slalom Géant'), ('SAUT-SKI', 'Saut à Ski - Grand Tremplin'), ('BOBSLEIGH', 'Bobsleigh à 4');
INSERT INTO Journee (jour) VALUES ('2030-02-10'), ('2030-02-11'), ('2030-02-12'), ('2030-02-13'), ('2030-02-14');

-- 2.2 Secouristes et Comptes Utilisateurs
-- CORRECTION : On ne fournit plus les IDs manuellement. On laisse AUTO_INCREMENT faire son travail.
INSERT INTO Secouriste (nom, prenom, dateNaissance, email, tel, adresse) VALUES
('Jean', 'Patrick', '1995-05-20', 'test@mail.com', '0612345678', '1 Rue de la Montagne, 73000 Chambéry'),
('Martin', 'Marie', '1998-11-12', 'marie.martin@secours.fr', '0687654321', '15 Avenue des Alpes, 38000 Grenoble'),
('Petit', 'Luc', '2001-02-28', 'luc.petit@secours.fr', '0611223344', '8 Boulevard de la Neige, 74000 Annecy'),
('Durand', 'Sophie', '1992-07-15', 'sophie.durand@secours.fr', '0655667788', '22 Place du Forum, 73200 Albertville');

-- Les idSecouriste 1, 2, 3, 4 correspondent bien aux secouristes insérés juste avant.
INSERT INTO CompteUtilisateur (login, motDePasseHash, role, idSecouriste) VALUES
('test@mail.com', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 1),
('marie.martin@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 2),
('luc.petit@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 3),
('sophie.durand@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 4),
('admin@jo2030.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'ADMINISTRATEUR', 5);

-- 2.3 Compétences et Disponibilités des Secouristes
INSERT INTO Possede (idSecouriste, intituleCompetence) VALUES (1, 'CE'), (1, 'VPSP'), (2, 'PSE2'), (3, 'PSE1'), (4, 'CP');
INSERT INTO EstDisponible (idSecouriste, jour) VALUES (1, '2030-02-10'), (1, '2030-02-11'), (1, '2030-02-12'), (2, '2030-02-10'), (2, '2030-02-13'), (3, '2030-02-12'), (4, '2030-02-10'), (4, '2030-02-11');

-- 2.4 Événements (DPS) et Leurs Besoins
INSERT INTO DPS (id, horaire_depart, horaire_fin, lieu, sport, jour) VALUES (1, 8, 17, 'CRCHV', 'SKI-ALP-DH', '2030-02-10'), (2, 9, 12, 'LPLGN', 'BOBSLEIGH', '2030-02-11'), (3, 10, 16, 'VALDI', 'SKI-ALP-SL', '2030-02-12');
INSERT INTO ABesoin (idDPS, intituleCompetence, nombre) VALUES (1, 'CP', 1), (1, 'CE', 1), (1, 'PSE2', 2), (2, 'VPSP', 1), (2, 'PSE2', 1), (3, 'CE', 1), (3, 'PSE1', 1);

-- 2.5 Affectations Finales
INSERT INTO Affectation (idSecouriste, intituleCompetence, idDPS) VALUES (4, 'CP', 1), (1, 'CE', 1), (2, 'PSE2', 1), (4, 'PSE2', 1), (1, 'VPSP', 2), (4, 'PSE2', 2), (1, 'CE', 3), (3, 'PSE1', 3);


SELECT 'Script de remplissage terminé avec succès.' AS message;