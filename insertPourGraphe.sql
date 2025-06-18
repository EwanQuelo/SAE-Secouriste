-- ==========================================================================
-- SCRIPT DE REMPLISSAGE - SCÉNARIO "SOLUTION PARFAITE" (12/12)
-- Version conçue pour permettre une affectation complète du DPS de test.
--
-- MODIFICATIONS CLÉS :
-- - Maintien du DPS complexe (ID 6) avec 12 postes à pourvoir pour le 17/06/2025.
-- - AUGMENTATION DU POOL DE CANDIDATS : 12 secouristes spécifiques sont
--   rendus disponibles le 17/06/2025.
-- - AJUSTEMENT DES COMPÉTENCES : Les compétences des 12 secouristes
--   disponibles sont définies de manière à ce qu'une solution 12/12 soit possible.
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
-- ÉTAPE 2 : REMPLISSAGE DES DONNÉES DE BASE (inchangé)
-- --------------------------------------------------------------------------
INSERT INTO Competence (intitule) VALUES ('CO'), ('CP'), ('CE'), ('PBC'), ('PBF'), ('PSE1'), ('PSE2'), ('SSA'), ('VPSP');
INSERT INTO Necessite (intituleCompetence, competenceRequise) VALUES ('CO', 'CP'), ('CP', 'CE'), ('CE', 'PSE2'), ('PSE2', 'PSE1'), ('SSA', 'PSE1'), ('VPSP', 'PSE2'), ('PBF', 'PBC');
INSERT INTO Site (code, nom, longitude, latitude) VALUES ('CRCHV', 'Courchevel - Le Praz', 6.6335, 45.4153), ('MRBL', 'Méribel - Chaudanne', 6.5665, 45.3967), ('VALDI', 'Val d''Isère - La Daille', 6.9800, 45.4481), ('LPLGN', 'La Plagne - Piste de Bobsleigh', 6.6742, 45.5065);
INSERT INTO Sport (code, nom) VALUES ('SKI-ALP-DH', 'Ski Alpin - Descente'), ('SKI-ALP-SL', 'Ski Alpin - Slalom Géant'), ('SAUT-SKI', 'Saut à Ski - Grand Tremplin'), ('BOBSLEIGH', 'Bobsleigh à 4'), ('BIATHLON', 'Biathlon - Sprint');
INSERT INTO Journee (jour) VALUES ('2025-06-16'), ('2025-06-17'), ('2025-06-18'), ('2025-06-19'), ('2025-06-20');

-- --------------------------------------------------------------------------
-- ÉTAPE 3 : CRÉATION DES SECOURISTES ET COMPTES (inchangé)
-- --------------------------------------------------------------------------
INSERT INTO Secouriste (nom, prenom, dateNaissance, email, tel, adresse) VALUES
('Jean', 'Patrick', '1995-05-20', 'test@mail.com', '0612345678', '1 Rue de la Montagne, 73000 Chambéry'),
('Martin', 'Marie', '1998-11-12', 'marie.martin@secours.fr', '0687654321', '15 Avenue des Alpes, 38000 Grenoble'),
('Petit', 'Luc', '2001-02-28', 'luc.petit@secours.fr', '0611223344', '8 Boulevard de la Neige, 74000 Annecy'),
('Durand', 'Sophie', '1992-07-15', 'sophie.durand@secours.fr', '0655667788', '22 Place du Forum, 73200 Albertville'),
('Bernard', 'Thomas', '1999-08-10', 'thomas.bernard@secours.fr', '0601020304', '5 Rue des Ecrins, 38000 Grenoble'),
('Dubois', 'Camille', '2000-01-15', 'camille.dubois@secours.fr', '0601020305', '12 Avenue du Mont Blanc, 74000 Annecy'),
('Moreau', 'Léa', '1997-06-25', 'lea.moreau@secours.fr', '0601020306', '3 Place de la Mairie, 73000 Chambéry'),
('Laurent', 'Hugo', '1994-03-30', 'hugo.laurent@secours.fr', '0601020307', '40 Rue de la République, 69002 Lyon'),
('Simon', 'Manon', '2002-12-05', 'manon.simon@secours.fr', '0601020308', '18 Quai des Allobroges, 38000 Grenoble'),
('Michel', 'Alexandre', '1996-09-18', 'alex.michel@secours.fr', '0601020309', '29 Rue Sommeiller, 74000 Annecy'),
('Lefevre', 'Chloé', '1993-11-22', 'chloe.lefevre@secours.fr', '0601020310', '7 Avenue de la Maveria, 74940 Annecy-le-Vieux'),
('Leroy', 'Enzo', '2003-04-14', 'enzo.leroy@secours.fr', '0601020311', '101 Cours Jean Jaurès, 38000 Grenoble'),
('Roux', 'Juliette', '1998-07-07', 'juliette.roux@secours.fr', '0601020312', '33 Boulevard du Théâtre, 73000 Chambéry'),
('David', 'Lucas', '1995-10-01', 'lucas.david@secours.fr', '0601020313', '55 Avenue de Genève, 74000 Annecy'),
('Bertrand', 'Emma', '1999-05-19', 'emma.bertrand@secours.fr', '0601020314', '2 Rue Fantin Latour, 38000 Grenoble');

INSERT INTO CompteUtilisateur (login, motDePasseHash, role, idSecouriste) VALUES
('test@mail.com', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 1), ('marie.martin@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 2), ('luc.petit@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 3), ('sophie.durand@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 4), ('thomas.bernard@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 5), ('camille.dubois@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 6), ('lea.moreau@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 7), ('hugo.laurent@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 8), ('manon.simon@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 9), ('alex.michel@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 10),
('chloe.lefevre@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 11), ('enzo.leroy@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 12), ('juliette.roux@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 13), ('lucas.david@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 14), ('emma.bertrand@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 15),
('admin@jo2030.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'ADMINISTRATEUR', NULL);

-- --------------------------------------------------------------------------
-- ÉTAPE 4 : DÉFINITION DES COMPÉTENCES ET DISPONIBILITÉS
-- --------------------------------------------------------------------------
-- *** AJUSTEMENT DES COMPÉTENCES POUR PERMETTRE UNE SOLUTION 12/12 ***
INSERT INTO Possede (idSecouriste, intituleCompetence) VALUES 
-- Spécialistes pour les postes uniques
(1, 'PSE1'), (1, 'PSE2'), (1, 'CE'), (1, 'CP'), -- Patrick (ID 1) -> Pour le poste CP
(8, 'PSE1'), (8, 'PSE2'), (8, 'CE'),          -- Hugo (ID 8) -> Pour un poste CE
(11, 'PSE1'), (11, 'PSE2'), (11, 'CE'),         -- Chloé (ID 11) -> Pour l'autre poste CE
(9, 'PSE1'), (9, 'PSE2'), (9, 'VPSP'),        -- Manon (ID 9) -> Pour le poste VPSP
(4, 'PSE1'), (4, 'PSE2'), (4, 'SSA'),          -- Sophie (ID 4) -> Pour un poste SSA
(13, 'PSE1'), (13, 'PSE2'), (13, 'SSA'),        -- Juliette (ID 13) -> Pour l'autre poste SSA
-- Secouristes pour les postes PSE2
(2, 'PSE1'), (2, 'PSE2'),
(5, 'PSE1'), (5, 'PSE2'),
(7, 'PSE1'), (7, 'PSE2'),
(10, 'PSE1'), (10, 'PSE2'),
-- Secouristes pour les postes PSE1
(3, 'PSE1'),
(6, 'PSE1');

-- *** AJUSTEMENT DES DISPONIBILITÉS : 12 SECOURISTES DISPONIBLES LE 17/06/2025 ***
INSERT INTO EstDisponible (idSecouriste, jour) VALUES 
-- Les 12 secouristes nécessaires pour la solution 12/12
(1, '2025-06-17'), (2, '2025-06-17'), (3, '2025-06-17'), (4, '2025-06-17'), (5, '2025-06-17'),
(6, '2025-06-17'), (7, '2025-06-17'), (8, '2025-06-17'), (9, '2025-06-17'), (10, '2025-06-17'),
(11, '2025-06-17'), (13, '2025-06-17'),
-- Ajout de quelques autres disponibilités pour les autres jours
(1, '2025-06-16'), (8, '2025-06-18'), (11, '2025-06-19');


-- --------------------------------------------------------------------------
-- ÉTAPE 5 : ÉVÉNEMENTS (DPS) ET LEURS BESOINS (inchangé)
-- --------------------------------------------------------------------------
INSERT INTO DPS (id, horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour) VALUES 
(1, 8, 30, 12, 0, 'CRCHV', 'SKI-ALP-DH', '2025-06-16'),
(3, 10, 0, 16, 0, 'VALDI', 'SKI-ALP-SL', '2025-06-18'),
(4, 9, 0, 14, 0, 'MRBL', 'SAUT-SKI', '2025-06-19'),
(5, 8, 0, 18, 0, 'LPLGN', 'BOBSLEIGH', '2025-06-20'),
(6, 8, 0, 17, 0, 'MRBL', 'BIATHLON', '2025-06-17'); -- LE DPS DE TEST

INSERT INTO ABesoin (idDPS, intituleCompetence, nombre) VALUES 
(1, 'PSE2', 1), (1, 'PSE1', 1), (3, 'CE', 1), (4, 'SSA', 1), (5, 'VPSP', 1), 
-- Besoins complexes pour le DPS 6 du 17 Juin (Total 12 postes)
(6, 'CP', 1), (6, 'CE', 2), (6, 'VPSP', 1), (6, 'SSA', 2), (6, 'PSE2', 4), (6, 'PSE1', 2);

-- --------------------------------------------------------------------------
-- ÉTAPE 6 : AFFECTATIONS (VIDE PAR DÉFAUT)
-- --------------------------------------------------------------------------

SELECT 'Script de test "Solution Parfaite" (12/12) chargé avec succès.' AS message;