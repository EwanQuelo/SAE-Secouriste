-- ==========================================================================
-- SCRIPT DE REMPLISSAGE - SCÉNARIO DE TEST COMPLEXE
-- Version conçue pour challenger les algorithmes d'affectation.
--
-- OBJECTIF : Créer un grand nombre de possibilités d'affectation
--            pour le 17 Juin 2025 afin de différencier les performances
--            des algorithmes glouton et exhaustif.
--
-- MODIFICATIONS CLÉS :
-- - Un nouveau DPS (ID 6) a été créé pour le 17/06/2025 avec de nombreux besoins.
-- - TOUS les 20 secouristes sont disponibles le 17/06/2025.
-- - Les compétences ont été redistribuées pour créer des choix intéressants.
-- - Les affectations pré-remplies ont été supprimées pour ne pas fausser les tests.
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
-- ÉTAPE 2 : REMPLISSAGE DES DONNÉES DE BASE
-- --------------------------------------------------------------------------
INSERT INTO Competence (intitule) VALUES ('CO'), ('CP'), ('CE'), ('PBC'), ('PBF'), ('PSE1'), ('PSE2'), ('SSA'), ('VPSP');
INSERT INTO Necessite (intituleCompetence, competenceRequise) VALUES ('CO', 'CP'), ('CP', 'CE'), ('CE', 'PSE2'), ('PSE2', 'PSE1'), ('SSA', 'PSE1'), ('VPSP', 'PSE2'), ('PBF', 'PBC');
INSERT INTO Site (code, nom, longitude, latitude) VALUES ('CRCHV', 'Courchevel - Le Praz', 6.6335, 45.4153), ('MRBL', 'Méribel - Chaudanne', 6.5665, 45.3967), ('VALDI', 'Val d''Isère - La Daille', 6.9800, 45.4481), ('LPLGN', 'La Plagne - Piste de Bobsleigh', 6.6742, 45.5065);
INSERT INTO Sport (code, nom) VALUES ('SKI-ALP-DH', 'Ski Alpin - Descente'), ('SKI-ALP-SL', 'Ski Alpin - Slalom Géant'), ('SAUT-SKI', 'Saut à Ski - Grand Tremplin'), ('BOBSLEIGH', 'Bobsleigh à 4'), ('BIATHLON', 'Biathlon - Sprint');
INSERT INTO Journee (jour) VALUES ('2025-06-16'), ('2025-06-17'), ('2025-06-18'), ('2025-06-19'), ('2025-06-20');

-- --------------------------------------------------------------------------
-- ÉTAPE 3 : CRÉATION DES SECOURISTES ET COMPTES
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
('Bertrand', 'Emma', '1999-05-19', 'emma.bertrand@secours.fr', '0601020314', '2 Rue Fantin Latour, 38000 Grenoble'),
('Morel', 'Gabriel', '1991-08-31', 'gabriel.morel@secours.fr', '0601020315', '1 Place de l''Hôtel de Ville, 73200 Albertville'),
('Fournier', 'Alice', '2000-10-27', 'alice.fournier@secours.fr', '0601020316', '88 Rue des Aiguilles, 74400 Chamonix'),
('Girard', 'Louis', '1997-02-11', 'louis.girard@secours.fr', '0601020317', '19 Avenue des Ducs de Savoie, 73000 Chambéry'),
('Bonnet', 'Sarah', '1994-12-13', 'sarah.bonnet@secours.fr', '0601020318', '6 Rue de la Poste, 38000 Grenoble'),
('Dupont', 'Adam', '2001-09-09', 'adam.dupont@secours.fr', '0601020319', '21 Rue Royale, 74000 Annecy');

-- Le hash de mot de passe est un exemple BCrypt.
INSERT INTO CompteUtilisateur (login, motDePasseHash, role, idSecouriste) VALUES
('test@mail.com', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 1),
('marie.martin@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 2),
('luc.petit@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 3),
('sophie.durand@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 4),
('thomas.bernard@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 5),
('camille.dubois@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 6),
('lea.moreau@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 7),
('hugo.laurent@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 8),
('manon.simon@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 9),
('alex.michel@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 10),
('chloe.lefevre@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 11),
('enzo.leroy@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 12),
('juliette.roux@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 13),
('lucas.david@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 14),
('emma.bertrand@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 15),
('gabriel.morel@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 16),
('alice.fournier@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 17),
('louis.girard@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 18),
('sarah.bonnet@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 19),
('adam.dupont@secours.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'SECOURISTE', 20),
('admin@jo2030.fr', '$2a$10$Nm84K.ob81kQjIr.1nPGH.mQBSqTKve55c3F8wvn2E0Pl.2avlbpi', 'ADMINISTRATEUR', NULL);

-- --------------------------------------------------------------------------
-- ÉTAPE 4 : DÉFINITION DES COMPÉTENCES ET DISPONIBILITÉS
-- --------------------------------------------------------------------------
INSERT INTO Possede (idSecouriste, intituleCompetence) VALUES 
(1, 'PSE1'), (1, 'PSE2'), (1, 'CE'), (1, 'CP'), -- Patrick est Chef de Poste
(2, 'PSE1'), (2, 'PSE2'),
(3, 'PSE1'),
(4, 'PSE1'), (4, 'PSE2'), (4, 'SSA'), -- Sophie est Sauveteur Aquatique
(5, 'PSE1'), (5, 'PSE2'),
(6, 'PSE1'),
(7, 'PSE1'), (7, 'PSE2'),
(8, 'PSE1'), (8, 'PSE2'), (8, 'CE'), -- Hugo est Chef d'Equipe
(9, 'PSE1'), (9, 'PSE2'), (9, 'VPSP'), -- Manon est Conducteur
(10, 'PSE1'), (10, 'PSE2'),
(11, 'PSE1'), (11, 'PSE2'), (11, 'CE'), (11, 'CP'), (11, 'CO'), -- Chloé est la plus qualifiée (Cadre Opérationnel)
(12, 'PSE1'),
(13, 'PSE1'), (13, 'PSE2'),
(14, 'PSE1'), (14, 'PSE2'),
(15, 'PSE1'),
(16, 'PBC'), (16, 'PBF'), -- Gabriel est Pilote de Bateaux
(17, 'PSE1'), (17, 'PSE2'),
(18, 'PSE1'),
(19, 'PSE1'), (19, 'PSE2'), (19, 'SSA'), -- Sarah est aussi Sauveteur Aquatique
(20, 'PSE1'), (20, 'VPSP'); -- Adam est PSE1 + Conducteur (cas rare mais possible)

-- ATTENTION : TOUS LES SECOURISTES SONT DISPONIBLES LE 17/06/2025
-- Cela crée un grand pool de candidats pour le DPS de test.
INSERT INTO EstDisponible (idSecouriste, jour) VALUES 
(1, '2025-06-17'), (2, '2025-06-17'), (3, '2025-06-17'), (4, '2025-06-17'), (5, '2025-06-17'),
(6, '2025-06-17'), (7, '2025-06-17'), (8, '2025-06-17'), (9, '2025-06-17'), (10, '2025-06-17'),
(11, '2025-06-17'), (12, '2025-06-17'), (13, '2025-06-17'), (14, '2025-06-17'), (15, '2025-06-17'),
(16, '2025-06-17'), (17, '2025-06-17'), (18, '2025-06-17'), (19, '2025-06-17'), (20, '2025-06-17'),
-- Ajout de quelques autres disponibilités pour rendre le calendrier moins vide
(1, '2025-06-16'), (8, '2025-06-18'), (11, '2025-06-19'), (16, '2025-06-20');

-- --------------------------------------------------------------------------
-- ÉTAPE 5 : ÉVÉNEMENTS (DPS) ET LEURS BESOINS
-- --------------------------------------------------------------------------
INSERT INTO DPS (id, horaire_depart_heure, horaire_depart_minute, horaire_fin_heure, horaire_fin_minute, lieu, sport, jour) VALUES 
-- DPS simples pour les autres jours
(1, 8, 30, 12, 0, 'CRCHV', 'SKI-ALP-DH', '2025-06-16'),
(3, 10, 0, 16, 0, 'VALDI', 'SKI-ALP-SL', '2025-06-18'),
(4, 9, 0, 14, 0, 'MRBL', 'SAUT-SKI', '2025-06-19'),
(5, 8, 0, 18, 0, 'LPLGN', 'BOBSLEIGH', '2025-06-20'),
-- *** LE DPS DE TEST ***
(6, 8, 0, 17, 0, 'MRBL', 'BIATHLON', '2025-06-17');

INSERT INTO ABesoin (idDPS, intituleCompetence, nombre) VALUES 
-- Besoins simples
(1, 'PSE2', 1), (1, 'PSE1', 1), 
(3, 'CE', 1), 
(4, 'SSA', 1),
(5, 'VPSP', 1), 
-- *** BESOINS COMPLEXES POUR LE DPS 6 DU 17 JUIN ***
(6, 'CP', 1),      -- 1 Chef de Poste
(6, 'CE', 2),      -- 2 Chefs d'Equipe
(6, 'VPSP', 1),    -- 1 Conducteur
(6, 'SSA', 2),     -- 2 Sauveteurs Aquatiques
(6, 'PSE2', 4),    -- 4 secouristes PSE2
(6, 'PSE1', 2);    -- 2 secouristes PSE1

-- --------------------------------------------------------------------------
-- ÉTAPE 6 : AFFECTATIONS (VIDE PAR DÉFAUT)
-- La table Affectation est volontairement laissée vide pour que vos algorithmes
-- puissent travailler sur une base propre.
-- --------------------------------------------------------------------------

SELECT 'Script de test complexe (17/06/2025) chargé avec succès.' AS message;