-- Sélection de la base de données
USE secours2030;

-- --------------------------------------------------------
--            VIDAGE DES DONNÉES EXISTANTES
-- On supprime dans l'ordre inverse des dépendances pour éviter les erreurs
-- --------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0; -- Désactive temporairement la vérification des clés étrangères
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
SET FOREIGN_KEY_CHECKS = 1; -- Réactive la vérification

-- --------------------------------------------------------
--          INSERTION DANS LES TABLES DE RÉFÉRENCE
-- --------------------------------------------------------

-- Table: Site
INSERT INTO `Site` (`code`, `nom`, `longitude`, `latitude`) VALUES
('STA', 'Stade de France', 2.3601, 48.9244),
('BER', 'Bercy Arena', 2.3787, 48.8384),
('DEF', 'Paris La Défense Arena', 2.2283, 48.8925),
('CHA', 'Champs de Mars Arena', 2.2987, 48.8545);

-- Table: Sport
INSERT INTO `Sport` (`code`, `nom`) VALUES
('ATH', 'Athlétisme'),
('BSK', 'Basketball'),
('NAT', 'Natation'),
('JUD', 'Judo'),
('VOL', 'Volleyball');

-- Table: Journee (CORRIGÉE)
INSERT INTO `Journee` (`jour`) VALUES
('2024-07-28'),
('2024-07-29'),
('2024-07-30');

-- Table: Competence
INSERT INTO `Competence` (`intitule`) VALUES
('PSE1'),
('PSE2'),
('Chef de Poste'),
('Conducteur VPSP'),
('SST');

-- --------------------------------------------------------
--          INSERTION DANS LES TABLES AVEC DÉPENDANCES (NIVEAU 1)
-- --------------------------------------------------------

-- Table: Necessite
INSERT INTO `Necessite` (`intituleCompetence`, `competenceRequise`) VALUES
('PSE2', 'PSE1'),
('Chef de Poste', 'PSE2'),
('Conducteur VPSP', 'PSE2');

-- Table: Secouriste
INSERT INTO `Secouriste` (`id`, `nom`, `prenom`, `dateNaissance`, `email`, `tel`, `adresse`) VALUES
(1, 'Dubois', 'Alice', '1990-05-15', 'alice.dubois@email.com', '0601020304', '1 rue de Paris'),
(2, 'Lambert', 'Bruno', '1995-09-20', 'bruno.lambert@email.com', '0611223344', '2 avenue de Lyon'),
(3, 'Martin', 'Chloé', '1998-02-10', 'chloe.martin@email.com', '0655667788', '3 boulevard de Marseille'),
(4, 'Petit', 'David', '2002-11-30', 'david.petit@email.com', '0699887766', '4 place de Lille'),
(5, 'Royer', 'Eva', '1985-06-25', 'eva.royer@email.com', '0700112233', '5 rue de Bordeaux');

-- Table: CompteUtilisateur
INSERT INTO `CompteUtilisateur` (`login`, `motDePasseHash`, `role`, `idSecouriste`) VALUES
('alice.dubois@email.com', '$2a$10$47eyYuELzUi96Nh6g/a.3.Xpe4EiPMbbuLl9cKwzpw5GQJSistpCC', 'SECOURISTE', 1),
('bruno.lambert@email.com', '$2a$10$47eyYuELzUi96Nh6g/a.3.Xpe4EiPMbbuLl9cKwzpw5GQJSistpCC', 'SECOURISTE', 2),
('chloe.martin@email.com', '$2a$10$47eyYuELzUi96Nh6g/a.3.Xpe4EiPMbbuLl9cKwzpw5GQJSistpCC', 'SECOURISTE', 3),
('david.petit@email.com', '$2a$10$47eyYuELzUi96Nh6g/a.3.Xpe4EiPMbbuLl9cKwzpw5GQJSistpCC', 'SECOURISTE', 4),
('eva.royer@email.com', '$2a$10$47eyYuELzUi96Nh6g/a.3.Xpe4EiPMbbuLl9cKwzpw5GQJSistpCC', 'ADMINISTRATEUR', 5),
('admin@secours.fr', '$2a$10$47eyYuELzUi96Nh6g/a.3.Xpe4EiPMbbuLl9cKwzpw5GQJSistpCC', 'ADMINISTRATEUR', NULL);

-- Table: Possede
INSERT INTO `Possede` (`idSecouriste`, `intituleCompetence`) VALUES
(1, 'PSE1'), (1, 'PSE2'), (1, 'Chef de Poste'), (1, 'Conducteur VPSP'),
(2, 'PSE1'), (2, 'PSE2'),
(3, 'PSE1'), (3, 'PSE2'), (3, 'Conducteur VPSP'),
(4, 'PSE1'),
(5, 'SST');

-- Table: EstDisponible
INSERT INTO `EstDisponible` (`idSecouriste`, `jour`) VALUES
(1, '2024-07-28'), (1, '2024-07-29'), (1, '2024-07-30'),
(2, '2024-07-28'), (2, '2024-07-29'),
(3, '2024-07-29'), (3, '2024-07-30'),
(4, '2024-07-28');

-- --------------------------------------------------------
--          INSERTION DANS LES TABLES D'ÉVÉNEMENTS
-- --------------------------------------------------------

-- Table: DPS
INSERT INTO `DPS` (`id`, `horaire_depart`, `horaire_fin`, `lieu`, `sport`, `jour`) VALUES
(1, 800, 2200, 'STA', 'ATH', '2024-07-28'),
(2, 1400, 2300, 'BER', 'BSK', '2024-07-29'),
(3, 930, 1830, 'DEF', 'NAT', '2024-07-29'),
(4, 1800, 2100, 'CHA', 'JUD', '2024-07-30');

-- --------------------------------------------------------
--          INSERTION DANS LES TABLES DE JOINTURE FINALES
-- --------------------------------------------------------

-- Table: ABesoin
INSERT INTO `ABesoin` (`idDPS`, `intituleCompetence`, `nombre`) VALUES
(1, 'Chef de Poste', 1),
(1, 'Conducteur VPSP', 1),
(1, 'PSE2', 4),
(2, 'Chef de Poste', 1),
(2, 'PSE2', 2),
(3, 'Chef de Poste', 1),
(3, 'PSE2', 3),
(4, 'PSE1', 2);

-- Table: Affectation
INSERT INTO `Affectation` (`idSecouriste`, `intituleCompetence`, `idDPS`) VALUES
(1, 'Chef de Poste', 1),
(2, 'PSE2', 1),
(1, 'Chef de Poste', 2),
(3, 'PSE2', 2),
(2, 'PSE2', 3),
(3, 'PSE2', 3),
(3, 'PSE1', 4);