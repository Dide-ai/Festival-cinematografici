-- Dati di esempio. Gli id sono espliciti cosi' gli URL della demo sono prevedibili
-- (/festival/1, /film/1, ...) e le righe della join table si scrivono a mano.
-- In fondo le sequenze vengono fatte ripartire da 100 per non collidere con questi id.

insert into festival (id, nome, anno, citta, data_inizio, data_fine, descrizione) values (1, 'Festival del Cinema Italiano', 2025, 'Roma', '2025-10-10', '2025-10-14', 'Rassegna dedicata al cinema italiano contemporaneo.');
insert into festival (id, nome, anno, citta, data_inizio, data_fine, descrizione) values (2, 'Rassegna Cinema d''Autore', 2026, 'Torino', '2026-11-20', '2026-11-23', 'Selezione di opere d''autore italiane e internazionali.');

insert into regista (id, nome, cognome, data_nascita, nazionalita) values (1, 'Paolo', 'Sorrentino', '1970-05-31', 'Italiana');
insert into regista (id, nome, cognome, data_nascita, nazionalita) values (2, 'Matteo', 'Garrone', '1968-10-15', 'Italiana');
insert into regista (id, nome, cognome, data_nascita, nazionalita) values (3, 'Alice', 'Rohrwacher', '1981-12-29', 'Italiana');
insert into regista (id, nome, cognome, data_nascita, nazionalita) values (4, 'Wim', 'Wenders', '1945-08-14', 'Tedesca');
insert into regista (id, nome, cognome, data_nascita, nazionalita) values (5, 'Sofia', 'Coppola', '1971-05-14', 'Statunitense');

insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (1, 'La grande bellezza', 2013, 141, 'Drammatico', 'Italia', 1);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (2, 'Il divo', 2008, 110, 'Drammatico', 'Italia', 1);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (3, 'Gomorra', 2008, 137, 'Drammatico', 'Italia', 2);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (4, 'Dogman', 2018, 103, 'Drammatico', 'Italia', 2);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (5, 'Lazzaro felice', 2018, 130, 'Drammatico', 'Italia', 3);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (6, 'La chimera', 2023, 130, 'Drammatico', 'Italia', 3);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (7, 'Il cielo sopra Berlino', 1987, 128, 'Fantastico', 'Germania', 4);
insert into film (id, titolo, anno, durata, genere, paese_produzione, regista_id) values (8, 'Lost in Translation', 2003, 102, 'Commedia', 'Stati Uniti', 5);

insert into film_festival (film_id, festival_id) values (1, 1);
insert into film_festival (film_id, festival_id) values (2, 1);
insert into film_festival (film_id, festival_id) values (3, 1);
insert into film_festival (film_id, festival_id) values (4, 1);
insert into film_festival (film_id, festival_id) values (5, 1);
insert into film_festival (film_id, festival_id) values (5, 2);
insert into film_festival (film_id, festival_id) values (6, 2);
insert into film_festival (film_id, festival_id) values (7, 2);
insert into film_festival (film_id, festival_id) values (8, 2);

insert into sala (id, nome, indirizzo, capienza) values (1, 'Sala Grande', 'Via del Cinema 1, Roma', 500);
insert into sala (id, nome, indirizzo, capienza) values (2, 'Sala Rossa', 'Via Nazionale 45, Roma', 200);
insert into sala (id, nome, indirizzo, capienza) values (3, 'Sala Piccola', 'Corso Vittorio 12, Torino', 80);

insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (1, '2025-10-10', '18:00', 'COMPLETED', 1, 1, 1);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (2, '2025-10-10', '21:00', 'COMPLETED', 1, 3, 1);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (3, '2025-10-11', '19:00', 'COMPLETED', 1, 2, 2);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (4, '2025-10-11', '21:30', 'COMPLETED', 1, 4, 2);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (5, '2025-10-12', '20:00', 'CANCELLED', 1, 5, 1);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (6, '2026-11-20', '18:30', 'SCHEDULED', 2, 6, 3);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (7, '2026-11-20', '21:30', 'SCHEDULED', 2, 7, 3);
insert into proiezione (id, data, ora, stato, festival_id, film_id, sala_id) values (8, '2026-11-21', '19:00', 'SCHEDULED', 2, 8, 3);

insert into utente (id, nome, cognome, email) values (1, 'Paolo', 'Rossi', 'paolo@siwfestival.it');
insert into utente (id, nome, cognome, email) values (2, 'Mario', 'Bianchi', 'mario@siwfestival.it');

insert into credenziali (id, username, password, ruolo, utente_id) values (1, 'paolo', '$2a$10$yWAIDyuEr78BBBFZ5cYh8.Nw4gUHFTRG5FwaWqNCGeOD8M4mh3.xy', 'ADMIN', 1);
insert into credenziali (id, username, password, ruolo, utente_id) values (2, 'mario', '$2a$10$q0aldVo8qshr.Zp6PnR5CO7UClGBVHdt4jc1WTyQL3G9fnh1hhB4S', 'DEFAULT', 2);

insert into recensione (id, testo, voto, data, film_id, utente_id) values (1, 'Un affresco visivo memorabile, con un ritmo che sa prendersi il suo tempo.', 9, '2025-10-15', 1, 2);
insert into recensione (id, testo, voto, data, film_id, utente_id) values (2, 'Racconto durissimo e senza retorica, la fotografia resta impressa.', 8, '2025-10-16', 3, 2);
insert into recensione (id, testo, voto, data, film_id, utente_id) values (3, 'Buona prova, ma in alcuni passaggi la narrazione si perde.', 7, '2025-10-17', 1, 1);

alter sequence festival_seq restart with 100;
alter sequence film_seq restart with 100;
alter sequence regista_seq restart with 100;
alter sequence sala_seq restart with 100;
alter sequence proiezione_seq restart with 100;
alter sequence recensione_seq restart with 100;
alter sequence utente_seq restart with 100;
alter sequence credenziali_seq restart with 100;
