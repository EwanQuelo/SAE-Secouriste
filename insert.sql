-- ==========================================================================
-- SCRIPT DE REMPLISSAGE FINAL - VERSION AUGMENTÉE
-- Version la plus robuste : laisse AUTO_INCREMENT gérer les IDs.
-- ADAPTATION : Les dates ont été modifiées pour la semaine du 16 au 20 Juin 2025.
-- ENRICHISSEMENT : Ajout de 16 secouristes (total 20), de 2 DPS et de
--                  plusieurs affectations pour le secouriste n°1.
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

-- 2.1 Données de base (inchangées)
INSERT INTO Competence (intitule) VALUES ('CO'), ('CP'), ('CE'), ('PBC'), ('PBF'), ('PSE1'), ('PSE2'), ('SSA'), ('VPSP');
INSERT INTO Necessite (intituleCompetence, competenceRequise) VALUES ('CO', 'CP'), ('CP', 'CE'), ('CE', 'PSE2'), ('PSE2', 'PSE1'), ('SSA', 'PSE1'), ('VPSP', 'PSE2'), ('PBF', 'PBC');
INSERT INTO Site (code, nom, longitude, latitude) VALUES ('CRCHV', 'Courchevel - Le Praz', 6.6335, 45.4153), ('MRBL', 'Méribel - Chaudanne', 6.5665, 45.3967), ('VALDI', 'Val d''Isère - La Daille', 6.9800, 45.4481), ('LPLGN', 'La Plagne - Piste de Bobsleigh', 6.6742, 45.5065);
INSERT INTO Sport (code, nom) VALUES ('SKI-ALP-DH', 'Ski Alpin - Descente'), ('SKI-ALP-SL', 'Ski Alpin - Slalom Géant'), ('SAUT-SKI', 'Saut à Ski - Grand Tremplin'), ('BOBSLEIGH', 'Bobsleigh à 4');
INSERT INTO Journee (jour) VALUES ('2025-06-16'), ('2025-06-17'), ('2025-06-18'), ('2025-06-19'), ('2025-06-20');

-- 2.2 Secouristes et Comptes Utilisateurs (4 existants + 16 nouveaux)
INSERT INTO Secouriste (nom, prenom, dateNaissance, email, tel, adresse) VALUES
-- 4 premiers secouristes
('Jean', 'Patrick', '1995-05-20', 'test@mail.com', '0612345678', '1 Rue de la Montagne, 73000 Chambéry'),
('Martin', 'Marie', '1998-11-12', 'marie.martin@secours.fr', '0687654321', '15 Avenue des Alpes, 38000 Grenoble'),
('Petit', 'Luc', '2001-02-28', 'luc.petit@secours.fr', '0611223344', '8 Boulevard de la Neige, 74000 Annecy'),
('Durand', 'Sophie', '1992-07-15', 'sophie.durand@secours.fr', '0655667788', '22 Place du Forum, 73200 Albertville'),
-- 16 nouveaux secouristes
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
('Bertrand', 'Emma', '1999-05-19', 'emma.bertrand@secours.fr', '0601020314', '2 Rue Fantin Latour, 38000 Grenoble'),
('Morel', 'Gabriel', '1991-08-31', 'gabriel.morel@secours.fr', '0601020315', '1 Place de l''Hôtel de Ville, 73200 Albertville'),
('Fournier', 'Alice', '2000-10-27', 'alice.fournier@secours.fr', '0601020316', '88 Rue des Aiguilles, 74400 Chamonix'),
('Girard', 'Louis', '1997-02-11', 'louis.girard@secours.fr', '0601020317', '19 Avenue des Ducs de Savoie, 73000 Chambéry'),
('Bonnet', 'Sarah', '1994-12-13', 'sarah.bonnet@secours.fr', '0601020318', '6 Rue de la Poste, 38000 Grenoble'),
('Dupont', 'Adam', '2001-09-09', 'adam.dupont@secours.fr', '0601020319', '21 Rue Royale, 74000 Annecy');

-- Les idSecouriste 1 à 20 correspondent aux secouristes insérés juste avant.
-- Le hash de mot de passe est un exemple et peut être le même pour les tests.
INSERT INTO CompteUtilisateur (login, motDePasseHash, role, idSecouriste) VALUES
('test@mail.com', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 1),
('marie.martin@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 2),
('luc.petit@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 3),
('sophie.durand@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 4),
('thomas.bernard@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 5),
('camille.dubois@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 6),
('lea.moreau@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 7),
('hugo.laurent@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 8),
('manon.simon@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 9),
('alex.michel@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 10),
('chloe.lefevre@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 11),
('enzo.leroy@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 12),
('juliette.roux@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 13),
('lucas.david@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 14),
('emma.bertrand@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 15),
('gabriel.morel@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 16),
('alice.fournier@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 17),
('louis.girard@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 18),
('sarah.bonnet@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 19),
('adam.dupont@secours.fr', '$2a$12$D4TTPa3qLz8o4UsoBUC8A.m2d7B/obL5x1e1o/2GZpI1xG/2uC.XC', 'SECOURISTE', 20),
('admin@jo2030.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'ADMINISTRATEUR', NULL);

-- 2.3 Compétences et Disponibilités des Secouristes

-- MODIFIÉ : Ajout de compétences à l'utilisateur 1 et 4, et ajout de compétences pour tous les nouveaux.
INSERT INTO Possede (idSecouriste, intituleCompetence) VALUES 
(1, 'PSE1'), (1, 'PSE2'), (1, 'CE'), (1, 'VPSP'), -- User 1 : compétences étendues
(2, 'PSE1'), (2, 'PSE2'), 
(3, 'PSE1'), 
(4, 'PSE1'), (4, 'PSE2'), (4, 'CP'), -- User 4 : ajout PSE1/2 pour rendre l'affectation valide
-- Compétences pour les nouveaux secouristes
(5, 'PSE1'), (5, 'PSE2'), (5, 'SSA'),
(6, 'PSE1'),
(7, 'PSE1'), (7, 'PSE2'),
(8, 'PSE1'), (8, 'PSE2'), (8, 'CE'),
(9, 'PSE1'),
(10, 'VPSP'), (10, 'PSE2'), (10, 'PSE1'),
(11, 'CP'), (11, 'CE'), (11, 'PSE2'), (11, 'PSE1'),
(12, 'PSE1'),
(13, 'PSE1'), (13, 'PSE2'),
(14, 'CO'), (14, 'CP'), (14, 'CE'), (14, 'PSE2'), (14, 'PSE1'),
(15, 'PSE1'),
(16, 'PBC'), (16, 'PBF'),
(17, 'PSE1'), (17, 'PSE2'),
(18, 'PSE1'),
(19, 'PSE1'), (19, 'PSE2'), (19, 'SSA'),
(20, 'PSE1');

-- MODIFIÉ : Disponibilités mappées à Juin 2025, étendues pour l'utilisateur 1, et ajoutées pour tous les nouveaux.
INSERT INTO EstDisponible (idSecouriste, jour) VALUES 
(1, '2025-06-16'), (1, '2025-06-17'), (1, '2025-06-18'), (1, '2025-06-19'), (1, '2025-06-20'), -- User 1 : disponible toute la semaine
(2, '2025-06-16'), (2, '2025-06-19'), 
(3, '2025-06-18'), 
(4, '2025-06-16'), (4, '2025-06-17'),
-- Disponibilités pour les nouveaux secouristes
(5, '2025-06-16'), (5, '2025-06-19'),
(6, '2025-06-20'),
(7, '2025-06-17'), (7, '2025-06-18'), (7, '2025-06-19'),
(8, '2025-06-19'),
(9, '2025-06-16'),
(10, '2025-06-17'), (10, '2025-06-20'),
(11, '2025-06-16'), (11, '2025-06-20'),
(12, '2025-06-18'),
(13, '2025-06-19'),
(14, '2025-06-20'),
(15, '2025-06-16'),
(16, '2025-06-17'),
(17, '2025-06-18'), (17, '2025-06-19'),
(18, '2025-06-20'),
(19, '2025-06-19'),
(20, '2025-06-20');

-- 2.4 Événements (DPS) et Leurs Besoins
-- MODIFIÉ : Ajout de deux DPS pour le jeudi et le vendredi
INSERT INTO DPS (id, horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour) VALUES 
(1, 8, 30, 17, 0, 'CRCHV', 'SKI-ALP-DH', '2025-06-16'), -- Lundi
(2, 9, 0, 12, 15, 'LPLGN', 'BOBSLEIGH', '2025-06-17'),  -- Mardi
(3, 10, 45, 16, 30, 'VALDI', 'SKI-ALP-SL', '2025-06-18'), -- Mercredi
(4, 9, 0, 14, 0, 'MRBL', 'SAUT-SKI', '2025-06-19'),   -- Jeudi (NOUVEAU)
(5, 8, 0, 16, 0, 'CRCHV', 'SKI-ALP-SL', '2025-06-20');    -- Vendredi (NOUVEAU)

INSERT INTO ABesoin (idDPS, intituleCompetence, nombre) VALUES 
(1, 'CP', 1), (1, 'CE', 1), (1, 'PSE2', 2), 
(2, 'VPSP', 1), (2, 'PSE2', 1), 
(3, 'CE', 1), (3, 'PSE1', 1),
(4, 'SSA', 1), (4, 'PSE2', 2), -- Besoins pour le nouveau DPS 4
(5, 'CO', 1), (5, 'PSE2', 1), (5, 'PSE1', 2); -- Besoins pour le nouveau DPS 5

-- 2.5 Affectations Finales
-- MODIFIÉ : Ajout de 3 affectations pour le secouriste n°1 et affectations pour les nouveaux DPS.
INSERT INTO Affectation (idSecouriste, intituleCompetence, idDPS) VALUES 
-- DPS 1 (Lundi 16)
(4, 'CP', 1), 
(1, 'CE', 1),   -- Affectation existante pour l'utilisateur 1
(2, 'PSE2', 1), 
(5, 'PSE2', 1), -- Remplacé par un nouvel utilisateur compétent et disponible

-- DPS 2 (Mardi 17)
(1, 'VPSP', 2), -- Affectation existante pour l'utilisateur 1
(4, 'PSE2', 2), -- Affectation rendue valide car l'utilisateur 4 a maintenant la compétence PSE2

-- DPS 3 (Mercredi 18)
(1, 'CE', 3),   -- Affectation existante pour l'utilisateur 1
(3, 'PSE1', 3),

-- DPS 4 (Jeudi 19) - NOUVEAU
(19, 'SSA', 4),
(1, 'PSE2', 4), -- NOUVELLE affectation pour l'utilisateur 1
(13, 'PSE2', 4),

-- DPS 5 (Vendredi 20) - NOUVEAU
(14, 'CO', 5),
(1, 'PSE2', 5), -- NOUVELLE affectation pour l'utilisateur 1
(6, 'PSE1', 5),
(1, 'PSE1', 5); -- NOUVELLE affectation pour l'utilisateur 1 (il peut couvrir un poste PSE1 avec sa compétence PSE2)


SELECT 'Script de remplissage augmenté terminé avec succès pour Juin 2025.' AS message;