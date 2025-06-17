DROP DATABASE IF EXISTS secours2030;
CREATE DATABASE secours2030;
USE secours2030;

DROP TABLE IF EXISTS Affectation, ABesoin, Possede, Necessite, EstDisponible, DPS, Sport, Site, Competence, Journee, CompteUtilisateur, Secouriste;

CREATE TABLE Secouriste (
    id BIGINT AUTO_INCREMENT,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    dateNaissance DATE,
    email VARCHAR(100) UNIQUE,
    tel VARCHAR(20),
    adresse VARCHAR(100),
    CONSTRAINT pk_Secouriste PRIMARY KEY (id)
);

CREATE TABLE CompteUtilisateur (
    login VARCHAR(255),
    motDePasseHash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    idSecouriste BIGINT NULL,
    CONSTRAINT pk_CompteUtilisateur PRIMARY KEY (login),
    -- ON DELETE CASCADE: si un secouriste est supprimé, son compte l'est aussi.
    CONSTRAINT fk_CompteUtilisateur_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id) ON DELETE CASCADE
);


CREATE TABLE Journee (
	jour DATE,
    CONSTRAINT pk_Journee PRIMARY KEY (jour)
);

CREATE TABLE EstDisponible (
	idSecouriste BIGINT,
    jour DATE,
    CONSTRAINT fk_EstDisponible_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id) ON DELETE CASCADE,
    CONSTRAINT fk_EstDisponible_Journee FOREIGN KEY (jour) REFERENCES Journee(jour) ON DELETE CASCADE,
    CONSTRAINT pk_EstDisponible PRIMARY KEY (idSecouriste, jour)
);


CREATE TABLE Competence (
	intitule VARCHAR(100),
    CONSTRAINT pk_Competence PRIMARY KEY (intitule)
);

CREATE TABLE Necessite (
	intituleCompetence VARCHAR(100),
    competenceRequise VARCHAR(100),
    CONSTRAINT fk_Necessite_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule) ON DELETE CASCADE,
    CONSTRAINT fk_Necessite_CompetenceRequise FOREIGN KEY (competenceRequise) REFERENCES Competence(intitule) ON DELETE CASCADE,
    CONSTRAINT pk_Necessite PRIMARY KEY (intituleCompetence, competenceRequise)
);

CREATE TABLE Possede (
	idSecouriste BIGINT,
    intituleCompetence VARCHAR(100),
    CONSTRAINT fk_Possede_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id) ON DELETE CASCADE,
    CONSTRAINT fk_Possede_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule) ON DELETE CASCADE,
	CONSTRAINT pk_Possede PRIMARY KEY (idSecouriste, intituleCompetence)
);

/*
 * CORRIGÉ: Orthographe de 'latitude' corrigée.
 */
CREATE TABLE Site (
	code VARCHAR(20),
    nom VARCHAR(50),
    longitude FLOAT,
    latitude FLOAT,
    CONSTRAINT pk_Site PRIMARY KEY (code)
);

CREATE TABLE Sport (
	code VARCHAR(20),
    nom VARCHAR(50),
    CONSTRAINT pk_Sport PRIMARY KEY (code)
);

/*
 * CORRIGÉ:
 * - ID est maintenant AUTO_INCREMENT.
 * - Les contraintes UNIQUE sur les horaires ont été supprimées car elles étaient illogiques.
 * - Ajout de la clé étrangère 'jour' pour lier le DPS à une Journee.
 */
CREATE TABLE DPS (
	id BIGINT AUTO_INCREMENT,
    horaire_depart_heure INTEGER NOT NULL,
    horaire_depart_minute INTEGER NOT NULL,
    horaire_fin_heure INTEGER NOT NULL,
    horaire_fin_minute INTEGER NOT NULL,
    lieu VARCHAR(20),
    sport VARCHAR(20),
    jour DATE NOT NULL,
    CONSTRAINT fk_DPS_Site FOREIGN KEY (lieu) REFERENCES Site(code),
    CONSTRAINT fk_DPS_Sport FOREIGN KEY (sport) REFERENCES Sport(code),
    CONSTRAINT fk_DPS_Journee FOREIGN KEY (jour) REFERENCES Journee(jour),
    CONSTRAINT pk_DPS PRIMARY KEY (id)
);

CREATE TABLE ABesoin (
    intituleCompetence VARCHAR(100),
    idDPS BIGINT,
    nombre INTEGER,
    CONSTRAINT fk_ABesoin_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule) ON DELETE CASCADE,
    CONSTRAINT fk_ABesoin_DPS FOREIGN KEY (idDPS) REFERENCES DPS(id) ON DELETE CASCADE,
    CONSTRAINT pk_ABesoin PRIMARY KEY (intituleCompetence, idDPS)
);


CREATE TABLE Affectation (
	idSecouriste BIGINT,
    intituleCompetence VARCHAR(100),
    idDPS BIGINT,
    CONSTRAINT fk_Affectation_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id) ON DELETE CASCADE,
    CONSTRAINT fk_Affectation_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule) ON DELETE CASCADE,
    CONSTRAINT fk_Affectation_DPS FOREIGN KEY (idDPS) REFERENCES DPS(id) ON DELETE CASCADE,
    CONSTRAINT pk_Affectation PRIMARY KEY (idSecouriste, intituleCompetence, idDPS)
);