USE RicettarioOnline;

-- Utenti
INSERT INTO UTENTI (Nome, Cognome, Email, Password, Ruolo, Attivo, IndirizzoSpedizione)
VALUES
('Mirco', 'Alessandrini', 'utente1@mail.com', SHA2('password1', 512), 'UTENTE',TRUE, "Via Cristoforo Colombo 51, Roma"),
('Alessandro', 'Borghese', 'utente2@mail.com', SHA2('password2', 512), 'UTENTE',TRUE, "Via Giovanni Pascoli 11, Campobasso"),
('Marco', 'Giordano', 'utente3@mail.com', SHA2('password3', 512), 'UTENTE',TRUE, "Via della Liberazione 1, Napoli"),
('Bruno', 'Barbieri', 'utente4@mail.com', SHA2('password4', 512), 'UTENTE',TRUE, "Via Eugenio Montale 8, Bologna"),
('Simone', 'Marsili','admin@ricettario.com', SHA2('admin123', 512), 'ADMIN',TRUE, NULL);
SET @user1 = (SELECT CodiceUtente FROM utenti WHERE Email = "utente1@mail.com");
SET @user2 = (SELECT CodiceUtente FROM utenti WHERE Email = "utente2@mail.com");
SET @user3 = (SELECT CodiceUtente FROM utenti WHERE Email = "utente3@mail.com");
SET @user4 = (SELECT CodiceUtente FROM utenti WHERE Email = "utente4@mail.com");
SET @admin1 = (SELECT CodiceUtente FROM utenti WHERE Email = "admin@ricettario.com");

-- Categorie
INSERT INTO CATEGORIE (Nome, Descrizione)
VALUES
("Vegano", "Senza derivazione animale"),
("Vegetariano", "Non contenente carne"),
("Italiano", "Della tradizione italiana"),
("Asiatico", "Di provenienza asiatica"),
("Primo Piatto", ""),
("Dessert","");
SET @cat_vegano = (SELECT CodiceCategoria FROM categorie WHERE Nome = 'Vegano');
SET @cat_vege = (SELECT CodiceCategoria FROM categorie WHERE Nome = 'Vegetariano');
SET @cat_ita = (SELECT CodiceCategoria FROM categorie WHERE Nome = 'Italiano');
SET @cat_asia = (SELECT CodiceCategoria FROM categorie WHERE Nome = 'Asiatico');
SET @cat_primo = (SELECT CodiceCategoria FROM categorie WHERE Nome = 'Primo Piatto');
SET @cat_dessert = (SELECT CodiceCategoria FROM categorie WHERE Nome = 'Dessert');

-- Ingredienti
INSERT INTO ingredienti (Nome, Prezzo, Vegano)
VALUES
("Petto di pollo", 13, FALSE),
("Patate pasta gialla", 2, TRUE),
("Farina 00", 2, TRUE),
("Uova", 4.50, FALSE),
("Pecorino Romano", 18, FALSE),
("Latte intero", 2.50, FALSE),
("Parmigiano Reggiano", 20, FALSE),
("Burro", 10, FALSE),
("Guanciale", 17, FALSE),
("Spaghetti", 2.50, TRUE),
("Pomodori pelati", 3.50, TRUE),
("Basilico", 13, TRUE),
("Aglio", 4, TRUE),
("Pepe nero in grani", 22, TRUE),
("Vino bianco", 7, TRUE);
SET @petto_pollo = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Petto di pollo');
SET @patate_gialle = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Patate pasta gialla');
SET @farina_00 = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Farina 00');
SET @uova = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Uova');
SET @pecorino = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Pecorino Romano');
SET @latte_int = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Latte intero');
SET @parm_regg = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Parmigiano Reggiano');
SET @burro = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Burro');
SET @guanciale = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Guanciale');
SET @spaghetti = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Spaghetti');
SET @pomodori_pel = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Pomodori pelati');
SET @basilico = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Basilico');
SET @aglio = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Aglio');
SET @pepe = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Pepe nero in grani');
SET @vino_bianco = (SELECT CodiceIngrediente FROM ingredienti WHERE Nome = 'Vino bianco');


-- Ricette

SET @prep_pollo = "Infarinare il petto di pollo con la farina 00, rosolarlo nel burro, sfumare con il vino bianco e aggiungere il latte. Cuocere fino a ottenere una salsa cremosa.";
SET @prep_carbonara = "Rosolare il guanciale, cuocere gli spaghetti e unirli a uova, Pecorino Romano e pepe nero macinato. Aggiungere il guanciale e mantecare fuori dal fuoco.";
SET @prep_pure = "Lessare le patate, schiacciarle e incorporare latte intero e burro fino a ottenere un composto liscio e cremoso.";
SET @prep_spaghetti_pom = "Soffriggere l'aglio, aggiungere i pomodori pelati e cuocere il sugo. Unire gli spaghetti cotti e completare con il basilico.";
SET @prep_amatriciana = "Rosolare il guanciale, sfumare con il vino bianco e aggiungere i pomodori pelati. Condire gli spaghetti con il sugo e completare con Pecorino Romano.";
SET @prep_cep = "Cuocere gli spaghetti e mantecarli con Pecorino Romano, pepe nero e poca acqua di cottura fino a ottenere una crema omogenea.";



START TRANSACTION;
INSERT INTO ricette
	(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
("Pollo al latte", @user2, @prep_pollo, 25, 0, 0);
SET @pollo_latte = LAST_INSERT_ID();
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES 
(@pollo_latte, @petto_pollo, 300),
(@pollo_latte, @latte_int, 100),
(@pollo_latte, @burro, 50),
(@pollo_latte, @farina_00, 100);
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES (@pollo_latte, @cat_ita);
COMMIT;



START TRANSACTION;
INSERT INTO ricette
	(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
("Carbonara", @user1, @prep_carbonara, 30, 0, 0);
SET @carbonara = LAST_INSERT_ID();
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES 
(@carbonara, @spaghetti, 200),
(@carbonara, @pecorino, 100),
(@carbonara, @pepe, 30),
(@carbonara, @uova, 180),
(@carbonara, @guanciale, 150);
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES 
(@carbonara, @cat_ita),
(@carbonara, @cat_primo);
COMMIT;



START TRANSACTION;
INSERT INTO ricette
	(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
("Purè di patate", @user4, @prep_pure, 80, 0, 0);
SET @pure = LAST_INSERT_ID();
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES 
(@pure, @patate_gialle, 500),
(@pure, @latte_int, 125),
(@pure, @burro, 40);
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES 
(@pure, @cat_vege);
COMMIT;



START TRANSACTION;
INSERT INTO ricette
	(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
("Spaghetti al pomodoro", @user3, @prep_spaghetti_pom, 60, 0, 0);
SET @spagh_pom = LAST_INSERT_ID();
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES 
(@spagh_pom, @spaghetti, 200),
(@spagh_pom, @pomodori_pel, 400),
(@spagh_pom, @aglio, 20),
(@spagh_pom, @basilico, 20);
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES 
(@spagh_pom, @cat_ita),
(@spagh_pom, @cat_primo),
(@spagh_pom, @cat_vege),
(@spagh_pom, @cat_vegano);
COMMIT;



START TRANSACTION;
INSERT INTO ricette
	(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
("Amatriciana", @user1, @prep_amatriciana, 35, 0, 0);
SET @amatriciana = LAST_INSERT_ID();
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES
(@amatriciana, @spaghetti, 200),
(@amatriciana, @guanciale, 100),
(@amatriciana, @vino_bianco, 50),
(@amatriciana, @pomodori_pel, 200),
(@amatriciana, @pecorino, 70);
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES 
(@amatriciana, @cat_ita),
(@amatriciana, @cat_primo);
COMMIT;



START TRANSACTION;
INSERT INTO ricette
	(Nome, CodiceUtente, Preparazione, TempoRichiesto, NumeroIngredienti, PrezzoRicetta)
VALUES
("Cacio e pepe", @user3, @prep_cep, 25, 0, 0);
SET @cep = LAST_INSERT_ID();
INSERT INTO DETTAGLI_RICETTA (CodiceRicetta, CodiceIngrediente, Quantità)
VALUES 
(@cep, @spaghetti, 200),
(@cep, @pecorino, 150),
(@cep, @pepe, 30);
INSERT INTO CLASSIFICAZIONI (CodiceRicetta, CodiceCategoria)
VALUES 
(@cep, @cat_ita),
(@cep, @cat_primo);
COMMIT;



-- Recensioni

INSERT INTO RECENSIONI
    (CodiceUtente, CodiceRicetta, Data, Voto, Commento)
VALUES
    (@user2, @carbonara, CURRENT_DATE, 10, "Ottima ricetta, cremosa e ben spiegata."),
    (@user3, @carbonara, CURRENT_DATE, 8, "Molto buona, aumenterei leggermente il pecorino."),
	(@user4, @carbonara, CURRENT_DATE, 6, "Buona ma il guanciale è un po' abbondante."),
	(@user1, @pollo_latte, CURRENT_DATE, 9, "Carne tenerissima e salsa davvero cremosa."),
	(@user3, @pollo_latte, CURRENT_DATE, 8, "Facile da preparare e molto gustosa."),
	(@user4, @pollo_latte, CURRENT_DATE, 7, "Buona, ma avrei aggiunto più latte."),
	(@user1, @pure, CURRENT_DATE, 10, "Consistenza perfetta."),
	(@user2, @pure, CURRENT_DATE, 7, "Buono, ma preferisco meno burro."),
	(@user3, @pure, CURRENT_DATE, 8, "Ottimo come contorno."),
	(@user1, @spagh_pom, CURRENT_DATE, 8, "Semplice ma molto saporito."),
	(@user2, @spagh_pom, CURRENT_DATE, 10, "Perfetto per un pranzo veloce."),
	(@user4, @spagh_pom, CURRENT_DATE, 4, "Avrei cotto di più il sugo."),
	(@user2, @amatriciana, CURRENT_DATE, 10, "Molto vicina alla ricetta tradizionale."),
	(@user3, @amatriciana, CURRENT_DATE, 9, "Ottimo equilibrio tra pomodoro e guanciale."),
	(@user4, @amatriciana, CURRENT_DATE, 7, "Un po' troppo pecorino per i miei gusti."),
	(@user1, @cep, CURRENT_DATE, 9, "Cremosa e saporita."),
	(@user2, @cep, CURRENT_DATE, 8, "Ottima, ma serve attenzione nella mantecatura."),
	(@user4, @cep, CURRENT_DATE, 2, "La crema mi è venuta troppo densa.");



-- Promozioni

INSERT INTO PROMOZIONI (Nome, DataInizio, DataFine)
VALUES
("Estate 2026", '2026-06-01', '2026-09-15'),
("Settimana italiana", '2026-03-10', '2026-03-20'),
("Offerte autunnali", '2026-10-01', '2026-11-02');
SET @promo_estate = (SELECT CodicePromo FROM promozioni WHERE Nome = "Estate 2026");
SET @promo_sett_ita = (SELECT CodicePromo FROM promozioni WHERE Nome = "Settimana italiana");
SET @promo_autunno = (SELECT CodicePromo FROM promozioni WHERE Nome = "Offerte autunnali");



-- Sconti

INSERT INTO SCONTI (CodicePromo, CodiceCategoria, MinIngredienti, MaxIngredienti, PercentualeSconto)
VALUES
(@promo_sett_ita, @cat_ita, 3, 8, 20),
(@promo_autunno, @cat_asia, 4, 10, 5),
(@promo_autunno, @cat_dessert, 4, 7, 10),
(@promo_estate, @cat_vegano, 2, 6, 10),
(@promo_estate, @cat_vegano, 7, 12, 15),
(@promo_estate, @cat_vege, 2, 7, 5),
(@promo_estate, @cat_primo, 4, 8, 5);



-- Ordini
-- NOTA: la SELECT ... INTO che calcola @MigliorSconto e' stata corretta
-- rispetto alla versione originale. Con un LEFT JOIN, mettere il controllo
-- sulla data (CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine) solo dentro
-- la condizione ON del join su PROMOZIONI non esclude le righe con
-- promozione scaduta/futura dal calcolo di MAX(S.PercentualeSconto): quella
-- riga di SCONTI resta comunque nel risultato (con P.* a NULL), quindi
-- MAX guardava anche sconti non piu' validi. Il CASE WHEN dentro MAX fa si'
-- che una riga contribuisca al MAX solo se la promozione e' stata davvero
-- agganciata (P.CodicePromo IS NOT NULL), cioe' solo se e' attiva oggi.

START TRANSACTION;
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES (CURRENT_DATE, "Consegna a domicilio", @user1);
SET @ordine1 = LAST_INSERT_ID();
SET @CodiceRicetta = @carbonara;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine1, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
SET @CodiceRicetta = @cep;
SET @Quantita = 2;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine1, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
COMMIT;



START TRANSACTION;
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES 
(CURRENT_DATE, "Ordine singolo", @user1);
SET @ordine2 = LAST_INSERT_ID();
SET @CodiceRicetta = @amatriciana;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine2, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
COMMIT;



START TRANSACTION;
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES 
(CURRENT_DATE, "Ordine con secondo e contorno", @user2);
SET @ordine3 = LAST_INSERT_ID();
SET @CodiceRicetta = @pollo_latte;
SET @Quantita = 2;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine3, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
SET @CodiceRicetta = @pure;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine3, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
COMMIT;




START TRANSACTION;
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES 
(CURRENT_DATE, "Ordine per più porzioni", @user2);
SET @ordine4 = LAST_INSERT_ID();
SET @CodiceRicetta = @spagh_pom;
SET @Quantita = 3;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine4, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
COMMIT;




START TRANSACTION;
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES 
(CURRENT_DATE, "Ordine con più primi piatti", @user3);
SET @ordine5 = LAST_INSERT_ID();
SET @CodiceRicetta = @cep;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine5, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
SET @CodiceRicetta = @amatriciana;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine5, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
SET @CodiceRicetta = @carbonara;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine5, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
COMMIT;




START TRANSACTION;
INSERT INTO ORDINI (Data, Note, CodiceUtente)
VALUES 
(CURRENT_DATE, "Ordine con contorno e primo piatto", @user4);
SET @ordine6 = LAST_INSERT_ID();
-- Purè di patate
SET @CodiceRicetta = @pure;
SET @Quantita = 2;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine6, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
SET @CodiceRicetta = @spagh_pom;
SET @Quantita = 1;
SELECT
    R.PrezzoRicetta,
    COALESCE(MAX(CASE WHEN P.CodicePromo IS NOT NULL THEN S.PercentualeSconto END), 0)
INTO
    @PrezzoUnitario,
    @MigliorSconto
FROM RICETTE R
LEFT JOIN CLASSIFICAZIONI CL 
    ON CL.CodiceRicetta = R.CodiceRicetta
LEFT JOIN SCONTI S
    ON S.CodiceCategoria = CL.CodiceCategoria
    AND R.NumeroIngredienti BETWEEN S.MinIngredienti AND S.MaxIngredienti
LEFT JOIN PROMOZIONI P
    ON P.CodicePromo = S.CodicePromo
    AND CURRENT_DATE BETWEEN P.DataInizio AND P.DataFine
WHERE R.CodiceRicetta = @CodiceRicetta
GROUP BY R.CodiceRicetta, R.PrezzoRicetta;
INSERT INTO DETTAGLI_ORDINE
(CodiceOrdine, CodiceRicetta, PrezzoUnitario, Quantità, ScontoApplicato)
VALUES
(@ordine6, @CodiceRicetta, @PrezzoUnitario, @Quantita, @MigliorSconto);
COMMIT;