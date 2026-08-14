USE RicettarioOnline;


-- ___________________________________________________________________
#U1
INSERT INTO UTENTI
    (Nome, Cognome, Email, Password, Ruolo, Attivo, IndirizzoSpedizione)
VALUES
    (?, ?, ?, ?, 'UTENTE', TRUE, ?);

-- ___________________________________________________________________

#U2
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE
ORDER BY R.MediaRecensioni DESC;

-- ___________________________________________________________________

#U3.1
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE
  AND R.Nome = ?;

-- ___________________________________________________________________

#U3.2
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
JOIN DETTAGLI_RICETTA DR ON R.CodiceRicetta = DR.CodiceRicetta
JOIN INGREDIENTI I ON DR.CodiceIngrediente = I.CodiceIngrediente
WHERE R.Rimossa = FALSE
  AND I.Nome = ?;

-- ___________________________________________________________________

#U3.3
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE
AND R.TempoRichiesto <= ?;

-- ___________________________________________________________________

#U3.4
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE
AND R.PrezzoRicetta BETWEEN ? AND ?;

-- ___________________________________________________________________

#U3.5
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE
AND U.Nome = ? AND U.Cognome = ?;

-- ___________________________________________________________________

#U3.6
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.TempoRichiesto,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta
JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria
WHERE R.Rimossa = FALSE
AND C.Nome = ?;

-- ___________________________________________________________________

#U4
SELECT
    CodiceIngrediente,
    Nome,
    Prezzo,
    Vegano
FROM INGREDIENTI
ORDER BY Nome;

-- ___________________________________________________________________

#U5
INSERT INTO RECENSIONI
    (CodiceUtente, CodiceRicetta, Data, Voto, Commento)
VALUES
    (?, ?, CURRENT_DATE, ?, ?);

-- ___________________________________________________________________

#U6
START TRANSACTION;

INSERT INTO RICETTE
    (Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
    (?, ?, ?, ?, 0, 0);

SET @CodiceRicetta = LAST_INSERT_ID();

-- Da ripetere n volte per tutti gli n ingredienti selezionati
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES (@CodiceRicetta, ?, ?);

-- Da ripetere k volte per tutte le k categorie selezionate
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES (@CodiceRicetta, ?);

COMMIT;

-- ___________________________________________________________________

#U7
SELECT
    P.Nome                 AS NomePromo,
    P.DataInizio,
    P.DataFine,
    C.Nome                 AS NomeCategoria,
    S.MinIngredienti,
    S.MaxIngredienti,
    S.PercentualeSconto
FROM SCONTI S
JOIN PROMOZIONI P ON P.CodicePromo = S.CodicePromo
JOIN CATEGORIE C ON C.CodiceCategoria = S.CodiceCategoria
WHERE CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
ORDER BY S.PercentualeSconto DESC;

-- ___________________________________________________________________

#U8.1
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore,
    R.MediaRecensioni,
    R.PrezzoRicetta
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE
ORDER BY R.MediaRecensioni DESC
LIMIT ?;

-- ___________________________________________________________________

#U8.2
SELECT
    U.CodiceUtente,
    U.Nome,
    U.Cognome,
    AVG(R.MediaRecensioni) AS RatingUtente,
    COUNT(R.CodiceRicetta)  AS NumeroRicette
FROM UTENTI U
JOIN RICETTE R ON R.CodiceUtente = U.CodiceUtente
WHERE R.Rimossa = FALSE AND U.Attivo = TRUE
GROUP BY U.CodiceUtente, U.Nome, U.Cognome
ORDER BY RatingUtente DESC
LIMIT ?;

-- ___________________________________________________________________

#U8.3
SELECT
    R.CodiceRicetta,
    R.Nome                    AS NomeRicetta,
    R.PrezzoRicetta,
    SUM(DEO.Quantità)          AS QuantitaTotaleOrdinata
FROM DETTAGLI_ORDINE DEO
JOIN RICETTE R ON R.CodiceRicetta = DEO.CodiceRicetta
WHERE R.Rimossa = FALSE
GROUP BY R.CodiceRicetta, R.Nome, R.PrezzoRicetta
ORDER BY QuantitaTotaleOrdinata DESC
LIMIT ?;

-- ___________________________________________________________________

#U8.4
SELECT
    C.CodiceCategoria,
    C.Nome                    AS NomeCategoria,
    SUM(DO.Quantità)          AS QuantitaTotaleOrdinata
FROM DETTAGLI_ORDINE DO
JOIN RICETTE R          ON R.CodiceRicetta = DO.CodiceRicetta
JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta
JOIN CATEGORIE C        ON C.CodiceCategoria = CL.CodiceCategoria
GROUP BY C.CodiceCategoria, C.Nome
ORDER BY QuantitaTotaleOrdinata DESC
LIMIT ?;

-- ___________________________________________________________________

#U9

START TRANSACTION;

-- 1. Creazione ordine
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES (CURRENT_DATE, ?, ?);

SET @CodiceOrdine = LAST_INSERT_ID();

-- 2. Da ripetere j volte, una per ciascuna delle j ricette scelte nel carrello
SET @CodiceRicetta = ?;
SET @Quantita = ?;

-- 2a. Determinare prezzo unitario e sconto migliore attivo per questa ricetta
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
   AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
   AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;

-- 2b. Inserimento riga di dettaglio per questa ricetta
INSERT INTO DETTAGLI_ORDINE (CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES (@CodiceOrdine, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);

-- (fine blocco da ripetere j volte)

COMMIT;

-- ___________________________________________________________________

#U10
SELECT 
    O.CodiceOrdine,
    O.Data,
    R.CodiceRicetta,
    R.Nome AS NomeRicetta,
    DEO.Quantità,
    DEO.PrezzoUnitario,
    DEO.ScontoApplicato,
    ROUND(DEO.Quantità * DEO.PrezzoUnitario * (1 - DEO.ScontoApplicato / 100), 2) As TotaleRiga
FROM ORDINI O
JOIN DETTAGLI_ORDINE DEO ON O.CodiceOrdine = DEO.CodiceOrdine
JOIN RICETTE R ON DEO.CodiceRicetta = R.CodiceRicetta
WHERE O.CodiceUtente = ?
ORDER BY O.Data DESC, O.CodiceOrdine;

-- ___________________________________________________________________

#U11
-- Serve separare le query per avere risultati distinti

#U11.1
SELECT
    R.CodiceRicetta,
    R.Nome              AS NomeRicetta,
    R.Preparazione,
    R.TempoRichiesto,
    R.PrezzoRicetta,
    R.MediaRecensioni,
    U.Nome              AS NomeAutore,
    U.Cognome           AS CognomeAutore
FROM RICETTE R
JOIN UTENTI U ON R.CodiceUtente = U.CodiceUtente
WHERE R.CodiceRicetta = ?
  AND R.Rimossa = FALSE;

-- ___________________________________________________________________

#U11.2
SELECT
    I.CodiceIngrediente,
    I.Nome,
    DR.Quantità
FROM DETTAGLI_RICETTA DR
JOIN INGREDIENTI I ON I.CodiceIngrediente = DR.CodiceIngrediente
WHERE DR.CodiceRicetta = ?;

-- ___________________________________________________________________

#U11.3
SELECT
    C.CodiceCategoria,
    C.Nome
FROM CLASSIFICAZIONI CL
JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria
WHERE CL.CodiceRicetta = ?;

-- ___________________________________________________________________

#A1
INSERT INTO INGREDIENTI (Nome, Prezzo, Vegano)
VALUES (?, ?, ?);

-- ___________________________________________________________________

#A2
UPDATE INGREDIENTI
SET 
    Nome = COALESCE(?, Nome),
    Prezzo = COALESCE(?, Prezzo),
    Vegano = COALESCE(?, Vegano)
WHERE CodiceIngrediente = ?;

-- ___________________________________________________________________

#A3
INSERT INTO CATEGORIE (Nome, Descrizione)
VALUES (?,?)
ON DUPLICATE KEY UPDATE
	Descrizione = VALUES(Descrizione);

-- ___________________________________________________________________

#A4
INSERT INTO PROMOZIONI (Nome, DataInizio, DataFine)
VALUES (?, ?, ?);

-- ___________________________________________________________________

#A5
INSERT INTO SCONTI (CodicePromo, CodiceCategoria, MinIngredienti, MaxIngredienti, PercentualeSconto)
VALUES (?, ?, ?, ?, ?)
ON DUPLICATE KEY UPDATE
    PercentualeSconto = VALUES(PercentualeSconto);

-- ___________________________________________________________________

#A6
START TRANSACTION;

-- 1. Identifica gli utenti da disattivare (rimozione logica)
CREATE TEMPORARY TABLE UtentiDaBloccare AS
SELECT R.CodiceUtente
FROM RICETTE R
JOIN CLASSIFICAZIONI CL ON CL.CodiceRicetta = R.CodiceRicetta
JOIN CATEGORIE C ON C.CodiceCategoria = CL.CodiceCategoria AND C.Nome = 'Vegano'
WHERE EXISTS (
    SELECT 1
    FROM DETTAGLI_RICETTA DR
    JOIN INGREDIENTI I ON I.CodiceIngrediente = DR.CodiceIngrediente
    WHERE DR.CodiceRicetta = R.CodiceRicetta
      AND I.Vegano = FALSE
)
GROUP BY R.CodiceUtente
HAVING COUNT(DISTINCT R.CodiceRicetta) >= 3;

-- 2. Disattiva gli utenti individuati
UPDATE UTENTI
SET Attivo = FALSE
WHERE CodiceUtente IN (SELECT CodiceUtente FROM UtentiDaBloccare);

-- 3. Rimuove tutte le ricette di questi utenti (non solo quelle incoerenti) (rimozione logica)
UPDATE RICETTE
SET Rimossa = TRUE
WHERE CodiceUtente IN (SELECT CodiceUtente FROM UtentiDaBloccare);

DROP TEMPORARY TABLE UtentiDaBloccare;

COMMIT;


