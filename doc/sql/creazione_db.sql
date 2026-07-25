-- *********************************************
-- * Ricettario Online - Schema fisico MySQL   *
-- * Basato sullo schema relazionale finale    *
-- * ottenuto dalla progettazione logica       *
-- *********************************************
-- Lo script crea da zero il database MySQL e seleziona
-- lo schema prima di definire le relazioni. L'ordine di
-- creazione segue le dipendenze referenziali: prima le
-- entita' indipendenti, poi quelle che importano chiavi
-- esterne, infine le associazioni N:N.

-- Database Section
-- ________________

DROP DATABASE IF EXISTS RicettarioOnline;
CREATE DATABASE RicettarioOnline;
USE RicettarioOnline;

CREATE TABLE UTENTI (
    CodiceUtente        INT NOT NULL AUTO_INCREMENT,
    Nome                VARCHAR(80) NOT NULL,
    Cognome             VARCHAR(80) NOT NULL,
    Email               VARCHAR(255) NOT NULL,
    Password            VARCHAR(128) NOT NULL,
    Ruolo				VARCHAR(10) NOT NULL DEFAULT 'UTENTE',
    Attivo              BOOLEAN NOT NULL DEFAULT TRUE,
    IndirizzoSpedizione VARCHAR(255),
    CONSTRAINT PK_UTENTE PRIMARY KEY (CodiceUtente),
    CONSTRAINT UQ_UTENTE_EMAIL UNIQUE (Email),
    CONSTRAINT CK_UTENTE_RUOLO CHECK (Ruolo IN ('UTENTE','ADMIN'))
);

CREATE TABLE CATEGORIE (
    CodiceCategoria INT NOT NULL AUTO_INCREMENT,
    Nome            VARCHAR(80) NOT NULL,
    Descrizione     VARCHAR(255) NOT NULL,
    CONSTRAINT PK_CATEGORIA PRIMARY KEY (CodiceCategoria),
    CONSTRAINT UQ_CATEGORIA_NOME UNIQUE (Nome)
);

CREATE TABLE INGREDIENTI (
    CodiceIngrediente INT NOT NULL AUTO_INCREMENT,
    Nome              VARCHAR(120) NOT NULL,
    Prezzo            DECIMAL(6,2) NOT NULL COMMENT 'euro al chilo',
    Vegano            BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT PK_INGREDIENTE PRIMARY KEY (CodiceIngrediente),
    CONSTRAINT UQ_INGREDIENTE_NOME UNIQUE (Nome),
    CONSTRAINT CK_INGREDIENTE_PREZZO CHECK (Prezzo >= 0)
);

CREATE TABLE PROMOZIONI (
    CodicePromo INT NOT NULL AUTO_INCREMENT,
    Nome        VARCHAR(120) NOT NULL,
    DataInizio  DATE NOT NULL,
    DataFine    DATE NOT NULL,
    CONSTRAINT PK_PROMOZIONE PRIMARY KEY (CodicePromo),
    CONSTRAINT CK_PROMOZIONE_DATE CHECK (DataFine >= DataInizio)
);

CREATE TABLE RICETTE (
    CodiceRicetta      INT NOT NULL AUTO_INCREMENT,
    Nome               VARCHAR(120) NOT NULL,
    CodiceUtente       INT NOT NULL,
    Preparazione       TEXT NOT NULL,
    TempoRichiesto     INT NOT NULL COMMENT 'minuti',
    NumeroIngredienti  INT NOT NULL DEFAULT 0,
    PrezzoRicetta      DECIMAL(6,2) NOT NULL DEFAULT 0,
    MediaRecensioni    DECIMAL(4,2) NOT NULL DEFAULT 0,
    Rimossa            BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT PK_RICETTA PRIMARY KEY (CodiceRicetta),
    CONSTRAINT UQ_RICETTA UNIQUE (CodiceUtente, Nome),
    CONSTRAINT CK_RICETTA_TEMPO CHECK (TempoRichiesto > 0),
    CONSTRAINT CK_RICETTA_NINGR CHECK (NumeroIngredienti >= 0),
    CONSTRAINT CK_RICETTA_PREZZO CHECK (PrezzoRicetta >= 0),
    CONSTRAINT CK_RICETTA_MEDIA CHECK (MediaRecensioni BETWEEN 0 AND 10),
    CONSTRAINT FK_RICETTA_UTENTE
        FOREIGN KEY (CodiceUtente)
        REFERENCES UTENTI (CodiceUtente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE ORDINI (
    CodiceOrdine INT NOT NULL AUTO_INCREMENT,
    Data         DATE NOT NULL DEFAULT (CURRENT_DATE),
    Note         VARCHAR(255),
    CodiceUtente INT NOT NULL,
    CONSTRAINT PK_ORDINE PRIMARY KEY (CodiceOrdine),
    CONSTRAINT FK_ORDINE_UTENTE
        FOREIGN KEY (CodiceUtente)
        REFERENCES UTENTI (CodiceUtente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
 
CREATE TABLE CLASSIFICAZIONI (
    CodiceRicetta   INT NOT NULL,
    CodiceCategoria INT NOT NULL,
    CONSTRAINT PK_CLASSIFICAZIONE PRIMARY KEY (CodiceRicetta, CodiceCategoria),
    CONSTRAINT FK_CLASSIFICAZIONE_RIC
        FOREIGN KEY (CodiceRicetta)
        REFERENCES RICETTE (CodiceRicetta)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_CLASSIFICAZIONE_CAT
        FOREIGN KEY (CodiceCategoria)
        REFERENCES CATEGORIE (CodiceCategoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
 
CREATE TABLE DETTAGLI_RICETTA (
    CodiceRicetta     INT NOT NULL,
    CodiceIngrediente INT NOT NULL,
    Quantità          DECIMAL(6,2) NOT NULL COMMENT 'grammi',
    CONSTRAINT PK_DETTAGLI_RICETTA PRIMARY KEY (CodiceRicetta, CodiceIngrediente),
    CONSTRAINT CK_DETTAGLI_RICETTA_QTA CHECK (Quantità > 0),
    CONSTRAINT FK_DETTRIC_RICETTA
        FOREIGN KEY (CodiceRicetta)
        REFERENCES RICETTE (CodiceRicetta)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_DETTRIC_INGREDIENTE
        FOREIGN KEY (CodiceIngrediente)
        REFERENCES INGREDIENTI (CodiceIngrediente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
 
CREATE TABLE DETTAGLI_ORDINE (
    CodiceOrdine    INT NOT NULL,
    CodiceRicetta   INT NOT NULL,
    PrezzoUnitario  DECIMAL(6,2) NOT NULL,
    Quantità        INT NOT NULL,
    ScontoApplicato DECIMAL(5,2) NOT NULL DEFAULT 0,
    CONSTRAINT PK_DETTAGLI_ORDINE PRIMARY KEY (CodiceOrdine, CodiceRicetta),
    CONSTRAINT CK_DETTORD_PREZZO CHECK (PrezzoUnitario >= 0),
    CONSTRAINT CK_DETTORD_QTA CHECK (Quantità > 0),
    CONSTRAINT CK_DETTORD_SCONTO CHECK (ScontoApplicato BETWEEN 0 AND 100),
    CONSTRAINT FK_DETTORD_ORDINE
        FOREIGN KEY (CodiceOrdine)
        REFERENCES ORDINI (CodiceOrdine)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_DETTORD_RICETTA
        FOREIGN KEY (CodiceRicetta)
        REFERENCES RICETTE (CodiceRicetta)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
 
CREATE TABLE SCONTI (
    CodiceCategoria   INT NOT NULL,
    CodicePromo       INT NOT NULL,
    MinIngredienti    INT NOT NULL COMMENT 'incluso',
    MaxIngredienti    INT NOT NULL COMMENT 'incluso',
    PercentualeSconto DECIMAL(5,2) NOT NULL,
    CONSTRAINT PK_SCONTO PRIMARY KEY (CodiceCategoria, CodicePromo, MinIngredienti),
    CONSTRAINT CK_SCONTO_RANGE CHECK (MaxIngredienti >= MinIngredienti),
    CONSTRAINT CK_SCONTO_PERC CHECK (PercentualeSconto BETWEEN 0 AND 100),
    CONSTRAINT FK_SCONTO_CATEGORIA
        FOREIGN KEY (CodiceCategoria)
        REFERENCES CATEGORIE (CodiceCategoria)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_SCONTO_PROMOZIONE
        FOREIGN KEY (CodicePromo)
        REFERENCES PROMOZIONI (CodicePromo)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
 
CREATE TABLE RECENSIONI (
    CodiceUtente  INT NOT NULL,
    CodiceRicetta INT NOT NULL,
    Data          DATE NOT NULL DEFAULT (CURRENT_DATE),
    Voto          TINYINT NOT NULL,
    Commento      VARCHAR(500),
    CONSTRAINT PK_RECENSIONI PRIMARY KEY (CodiceUtente, CodiceRicetta),
    CONSTRAINT CK_RECENSIONI_VOTO CHECK (Voto BETWEEN 1 AND 10),
    CONSTRAINT FK_RECENSIONI_UTENTE
        FOREIGN KEY (CodiceUtente)
        REFERENCES UTENTI (CodiceUtente)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT FK_RECENSIONI_RICETTA
        FOREIGN KEY (CodiceRicetta)
        REFERENCES RICETTE (CodiceRicetta)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);



-- INDICI

CREATE INDEX IDX_CLASSIFICAZIONI_CATEGORIA
    ON CLASSIFICAZIONI (CodiceCategoria, CodiceRicetta);
CREATE INDEX IDX_RICETTE_MEDIA_RECENSIONI
    ON RICETTE (Rimossa, MediaRecensioni);
CREATE INDEX IDX_DETTAGLI_ORDINE_RICETTA
    ON DETTAGLI_ORDINE (CodiceRicetta);
CREATE INDEX IDX_ORDINI_UTENTE_DATA
    ON ORDINI (CodiceUtente, Data);
CREATE INDEX IDX_SCONTI_CATEGORIA
    ON SCONTI (CodiceCategoria);
CREATE INDEX IDX_PROMOZIONI_VALIDITA
    ON PROMOZIONI (DataInizio, DataFine);
    

-- TRIGGER

DELIMITER $$
-- _____________________________________________________________
-- Trigger per INSERT su SCONTI

CREATE TRIGGER trg_sconto_no_sovrapposto_ins
BEFORE INSERT ON SCONTI
FOR EACH ROW
BEGIN
    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count
    FROM SCONTI
    WHERE CodiceCategoria = NEW.CodiceCategoria
      AND CodicePromo     = NEW.CodicePromo
      AND NEW.MinIngredienti <= MaxIngredienti
      AND NEW.MaxIngredienti >= MinIngredienti;

    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Intervallo ingredienti sovrapposto per stesse categoria e  promozione';
    END IF;
END$$

-- _____________________________________________________________
-- Trigger per UPDATE SCONTI

CREATE TRIGGER trg_sconto_no_sovrapposto_agg
BEFORE UPDATE ON SCONTI
FOR EACH ROW
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count
    FROM SCONTI
    WHERE CodiceCategoria = NEW.CodiceCategoria
      AND CodicePromo     = NEW.CodicePromo
      AND NOT (CodiceCategoria = OLD.CodiceCategoria
               AND CodicePromo = OLD.CodicePromo
               AND MinIngredienti = OLD.MinIngredienti)
      AND NEW.MinIngredienti <= MaxIngredienti
      AND NEW.MaxIngredienti >= MinIngredienti;

    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Intervallo ingredienti sovrapposto per stesse categoria e promozione';
    END IF;
END$$

-- _____________________________________________________________
-- Ricalcolo MediaRecensioni ogni volta che una recensione viene inserita

CREATE TRIGGER trg_recensioni_insert_media
AFTER INSERT ON RECENSIONI
FOR EACH ROW
BEGIN
    UPDATE RICETTE
    SET MediaRecensioni = (
        SELECT AVG(Voto)
        FROM RECENSIONI
        WHERE CodiceRicetta = NEW.CodiceRicetta
    )
    WHERE CodiceRicetta = NEW.CodiceRicetta;
END$$

-- _____________________________________________________________
-- Ricalcolo MediaRecensioni ogni volta che una recensione viene eliminata

CREATE TRIGGER trg_recensioni_delete_media
AFTER DELETE ON RECENSIONI
FOR EACH ROW
BEGIN
    UPDATE RICETTE
    SET MediaRecensioni = COALESCE(
        (SELECT AVG(Voto) FROM RECENSIONI WHERE CodiceRicetta = OLD.CodiceRicetta),
        0
    )
    WHERE CodiceRicetta = OLD.CodiceRicetta;
END$$

-- _____________________________________________________________
-- Modifica il prezzo di una ricetta dopo la modifica di un ingrediente 

CREATE TRIGGER trg_ingrediente_aggiorna_prezzo_ricetta
AFTER UPDATE ON INGREDIENTI
FOR EACH ROW
BEGIN
    IF NEW.Prezzo <> OLD.Prezzo THEN
        UPDATE RICETTE
        SET PrezzoRicetta = (
            SELECT COALESCE(SUM(DR.Quantità / 1000 * I.Prezzo), 0)
            FROM DETTAGLI_RICETTA DR
            JOIN INGREDIENTI I ON I.CodiceIngrediente = DR.CodiceIngrediente
            WHERE DR.CodiceRicetta = RICETTE.CodiceRicetta
        )
        WHERE CodiceRicetta IN (
            SELECT CodiceRicetta
            FROM DETTAGLI_RICETTA
            WHERE CodiceIngrediente = NEW.CodiceIngrediente
        );
    END IF;
END$$


-- _____________________________________________________________
-- Aggiorno NumIngredienti e PrezzoRicetta di RICETTE ogni volta che aggiungo un'istanza di DETTAGLI_RICETTA

CREATE TRIGGER trg_dettagli_ricetta_ins_agg_ricetta
AFTER INSERT ON DETTAGLI_RICETTA
FOR EACH ROW
BEGIN
    UPDATE RICETTE
    SET NumeroIngredienti = (
            SELECT COUNT(*)
            FROM DETTAGLI_RICETTA
            WHERE CodiceRicetta = NEW.CodiceRicetta
        ),
        PrezzoRicetta = (
            SELECT COALESCE(SUM(DR.Quantità / 1000 * I.Prezzo), 0)
            FROM DETTAGLI_RICETTA DR
            JOIN INGREDIENTI I ON I.CodiceIngrediente = DR.CodiceIngrediente
            WHERE DR.CodiceRicetta = NEW.CodiceRicetta
        )
    WHERE CodiceRicetta = NEW.CodiceRicetta;
END$$

-- _____________________________________________________________
-- Controllo che L'utente che effettua l'ordine abbia indirizzo di spedizione non nullo

CREATE TRIGGER trg_ordini_verifica_indirizzo
BEFORE INSERT ON ORDINI
FOR EACH ROW
BEGIN
    DECLARE v_indirizzo VARCHAR(255);

    SELECT IndirizzoSpedizione INTO v_indirizzo
    FROM UTENTI
    WHERE CodiceUtente = NEW.CodiceUtente;

    IF v_indirizzo IS NULL OR v_indirizzo = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Impossibile creare ordine: indirizzo di spedizione non impostato';
    END IF;
END$$

DELIMITER ;


-- ______________________________________________________________
-- VISTE PER L'AMMINISTRATORE

-- Fatturato giornaliero: per ogni giorno in cui sono stati effettuati ordini,
-- mostra numero di ordini, quantita' totale venduta e incasso totale
-- (tenendo conto degli sconti effettivamente applicati in fase d'ordine
CREATE OR REPLACE VIEW v_fatturato_giornaliero AS
SELECT
    O.Data,
    COUNT(DISTINCT O.CodiceOrdine) AS NumeroOrdini,
    SUM(DEO.Quantità) AS QuantitaTotaleVenduta,
    SUM(
        DEO.Quantità * DEO.PrezzoUnitario * (1 - DEO.ScontoApplicato / 100)
    ) AS IncassoTotale
FROM ORDINI O
JOIN DETTAGLI_ORDINE DEO ON DEO.CodiceOrdine = O.CodiceOrdine
GROUP BY O.Data
ORDER BY O.Data DESC;


-- Ricetta piu' venduta: per ogni ricetta, quantita' totale ordinata,
-- numero di ordini distinti in cui compare e incasso generato.
-- Utile per capire quali ricette "trainano" le vendite.
CREATE OR REPLACE VIEW v_fatturato_per_ricetta AS
SELECT
    R.CodiceRicetta,
    R.Nome AS NomeRicetta,
    COUNT(DISTINCT DEO.CodiceOrdine) AS NumeroOrdini,
    SUM(DEO.Quantità) AS QuantitaTotaleVenduta,
    SUM(
        DEO.Quantità * DEO.PrezzoUnitario * (1 - DEO.ScontoApplicato / 100)
    ) AS IncassoTotale
FROM RICETTE R
JOIN DETTAGLI_ORDINE DEO ON DEO.CodiceRicetta = R.CodiceRicetta
GROUP BY R.CodiceRicetta, R.Nome
ORDER BY IncassoTotale DESC;



-- Recensioni negative recenti (voto <= 3) delle ultime 2 settimane:
-- utile per moderazione rapida o per contattare l'autore della ricetta.
CREATE OR REPLACE VIEW v_recensioni_negative_recenti AS
SELECT
    RE.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    RE.CodiceUtente,
    U.Nome              AS NomeUtente,
    U.Cognome           AS CognomeUtente,
    RE.Voto,
    RE.Commento,
    RE.Data
FROM RECENSIONI RE
JOIN RICETTE R ON R.CodiceRicetta = RE.CodiceRicetta
JOIN UTENTI U ON U.CodiceUtente = RE.CodiceUtente
WHERE RE.Voto <= 3
  AND RE.Data >= CURRENT_DATE - INTERVAL 14 DAY
ORDER BY RE.Data DESC;

