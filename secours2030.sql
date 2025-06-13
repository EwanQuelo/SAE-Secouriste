DROP DATABASE secours2030;
CREATE DATABASE secours2030;
USE secours2030;

DROP TABLE IF EXISTS EstAffecte;
DROP TABLE IF EXISTS ABesoin;
DROP TABLE IF EXISTS Possede;
DROP TABLE IF EXISTS Necessite;
DROP TABLE IF EXISTS EstDisponible;
DROP TABLE IF EXISTS DPS;
DROP TABLE IF EXISTS Sport;
DROP TABLE IF EXISTS Site;
DROP TABLE IF EXISTS Competence;
DROP TABLE IF EXISTS Journee;
DROP TABLE IF EXISTS Secouriste;
DROP TABLE IF EXISTS Utilisateur;
DROP TABLE IF EXISTS CompteUtilisateur;

CREATE TABLE Secouriste (
    id BIGINT AUTO_INCREMENT,
    nom VARCHAR(30),
    prenom VARCHAR(30),
    dateNaissance DATE,
    email VARCHAR(40) UNIQUE,
    tel VARCHAR(10),
    adresse VARCHAR(50),
    CONSTRAINT pk_Secouriste PRIMARY KEY (id)
);

CREATE TABLE CompteUtilisateur (
    login VARCHAR(255),
    motDePasseHash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    idSecouriste BIGINT NULL,
    CONSTRAINT pk_CompteUtilisateur PRIMARY KEY (login),
    CONSTRAINT fk_CompteUtilisateur_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id) ON DELETE CASCADE
);

CREATE TABLE Journee (
	jour INTEGER,
    mois INTEGER,
    annee INTEGER,
    CONSTRAINT pk_Journee PRIMARY KEY (jour, mois, annee)
);

CREATE TABLE EstDisponible (
	idSecouriste BIGINT,
    jourJournee INTEGER,
    moisJournee INTEGER,
    anneeJournee INTEGER,
    CONSTRAINT fk_EstDisponible_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id),
    CONSTRAINT fk_EstDisponible_Journee FOREIGN KEY (jourJournee, moisJournee, anneeJournee) REFERENCES Journee(jour, mois, annee),
    CONSTRAINT pk_EstDisponible PRIMARY KEY (idSecouriste, jourJournee, moisJournee, anneeJournee)
);

CREATE TABLE Competence (
	intitule VARCHAR(20),
    CONSTRAINT pk_Competence PRIMARY KEY (intitule)
);

CREATE TABLE Necessite (
	intituleCompetence VARCHAR(20),
    competenceRequise VARCHAR(20),
    CONSTRAINT fk_Necessite_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule),
    CONSTRAINT fk_Necessite_CompetenceRequise FOREIGN KEY (competenceRequise) REFERENCES Competence(intitule),
    CONSTRAINT pk_Necessite PRIMARY KEY (intituleCompetence, competenceRequise)
);

CREATE TABLE Possede (
	idSecouriste BIGINT,
    intituleCompetence VARCHAR(20),
	CONSTRAINT pk_Possede PRIMARY KEY (idSecouriste, intituleCompetence)
);

CREATE TABLE Site (
	code VARCHAR(20),
    nom VARCHAR(30),
    longitude FLOAT,
    latitude FLOAT,
    CONSTRAINT pk_Site PRIMARY KEY (code)
);

CREATE TABLE Sport (
	code VARCHAR(20),
    nom VARCHAR(30),
    CONSTRAINT pk_Sport PRIMARY KEY (code)
);

CREATE TABLE DPS (
	id BIGINT,
    horaire_depart INTEGER NOT NULL UNIQUE,
    horaire_fin INTEGER NOT NULL UNIQUE,
    lieu VARCHAR(20),
    sport VARCHAR(20),
    CONSTRAINT fk_DPS_Site FOREIGN KEY (lieu) REFERENCES Site(code),
    CONSTRAINT fk_DPS_Sport FOREIGN KEY (sport) REFERENCES Sport(code),
    CONSTRAINT pk_DPS PRIMARY KEY (id)
);

CREATE TABLE ABesoin (
    intituleCompetence VARCHAR(20),
    idDPS BIGINT,
    nombre INTEGER,
    CONSTRAINT fk_ABesoin_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule),
    CONSTRAINT fk_ABesoin_DPS FOREIGN KEY (idDPS) REFERENCES DPS(id),
    CONSTRAINT pk_ABesoin PRIMARY KEY (intituleCompetence, idDPS)
);

CREATE TABLE EstAffecte (
	idSecouriste BIGINT,
    intituleCompetence VARCHAR(20),
    idDPS BIGINT,
    CONSTRAINT fk_EstAffecte_Secouriste FOREIGN KEY (idSecouriste) REFERENCES Secouriste(id),
    CONSTRAINT fk_EstAffecte_Competence FOREIGN KEY (intituleCompetence) REFERENCES Competence(intitule),
    CONSTRAINT fk_EstAffecte_DPS FOREIGN KEY (idDPS) REFERENCES DPS(id),
    CONSTRAINT pk_EstAffecte PRIMARY KEY (idSecouriste, intituleCompetence, idDPS)
);
	
	