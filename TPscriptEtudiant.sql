
     /* tache 1*/
 
CREATE USER BDDAdmin IDENTIFIED BY TPAdmin;
GRANT ALL PRIVILEGES TO BDDAdmin;
CONNECT BDDAdmin/TPAdmin;

create table Etudiant (
  matricule_etu integer    not null,
  nom_etu varchar2(30),
  prenom_etu varchar2(30),
  date_naissance varchar2(30) ,
  constraint pk_etu primary key (matricule_etu)
)
/

create table unite (
  code_Unite varchar2(30) not null,
  libelle varchar2(20) ,
  nbr_heures integer ,
  matricule_ens integer ,
  constraint pk_unite primary key (code_Unite),
  constraint fk_unite foreign key (matricule_ens)
         references Enseignant (matricule_ens) ON DELETE CASCADE
)
/

create table Enseignant (
 matricule_ens integer not null,
 nom_ens varchar2(30),
 prenom_ens varchar2(30),
 age integer ,
 constraint pk_ens primary key (matricule_ens)
 )
 /
 
 create table EtudiantUnite(
   matricule_etu  integer not null,
   code_Unite varchar2(30) not null,
   note_CC integer ,
   note_TP integer ,
   note_examen integer ,
   constraint pk_etu_unite primary key (code_Unite,matricule_etu),
   constraint fk_mat_etu1 foreign key (matricule_etu)
         references Etudiant (matricule_etu) ON DELETE CASCADE,
   constraint fk_code_unite1 foreign key ( code_Unite)
         references Unite (code_Unite) ON DELETE CASCADE 
)
/
   
  



 /* tache 2 */
  
CREATE USER Etudiant IDENTIFIED BY TPEtudiant;
GRANT SELECT ON Etudiant TO Etudiant;
 
CREATE USER Enseignant IDENTIFIED BY TPEnseignant;
GRANT SELECT, INSERT ON Enseignant TO Enseignant;

 /* tache 3 */
  
ALTER TABLE Etudiant ADD Adresse VARCHAR(100);
ALTER TABLE Enseignant DROP COLUMN age;
ALTER TABLE Etudiant ADD CONSTRAINT chk_matricule_etu CHECK (matricule_etu BETWEEN 20190000 AND 20199999);
ALTER TABLE Etudiant MODIFY (prenom_etu VARCHAR2(35));



 /* tache 4 */
 

INSERT INTO Etudiant VALUES(20190001, 'BOUSSAI', 'MOHAMED', TO_DATE('2000-01-12', 'YYYY-MM-DD'), 'Alger');
INSERT INTO Etudiant VALUES(20190002, 'CHAID', 'LAMIA', TO_DATE('1999-10-01', 'YYYY-MM-DD'), 'Batna');
INSERT INTO Etudiant VALUES(20190003, 'BRAHIMI', 'SOUAD', TO_DATE('2000-11-18', 'YYYY-MM-DD'), 'Setif');
INSERT INTO Etudiant VALUES(20190004, 'LAMA', 'SAID', TO_DATE('1999-05-23', 'YYYY-MM-DD'), 'Oran');

commit;

INSERT INTO Enseignant VALUES(20000001, 'HAROUNI', 'AMINE');
INSERT INTO Enseignant VALUES(19990011, 'FATHI', 'OMAR');
INSERT INTO Enseignant VALUES(19980078, 'BOUZIDANE', 'FARAH');
INSERT INTO Enseignant VALUES(20170015, 'ARABI', 'ZOUBIDA');

commit; 
 

INSERT INTO Unite VALUES('FEI0001', 'POO', 6, 20000001);
INSERT INTO Unite VALUES('FEI0002', 'BDD', 6, 19990011);
INSERT INTO Unite VALUES('FEI0003', 'RESEAU', 3, 20170015);
INSERT INTO Unite VALUES('FEI0004', 'SYSTEME', 6, 19980078);

commit;

INSERT INTO EtudiantUnite VALUES(20190001, 'FEI0001', 10, 15, 9);
INSERT INTO EtudiantUnite VALUES(20190002, 'FEI0001', 20, 13, 10);
INSERT INTO EtudiantUnite VALUES(20190004, 'FEI0001', 13, 17, 16);
INSERT INTO EtudiantUnite VALUES(20190002, 'FEI0002', 10, 16, 17);
INSERT INTO EtudiantUnite VALUES(20190003, 'FEI0002', 9, 8, 15);
INSERT INTO EtudiantUnite VALUES(20190004, 'FEI0002', 15, 9, 20);
INSERT INTO EtudiantUnite VALUES(20190002, 'FEI0004', 12, 18, 14);
INSERT INTO EtudiantUnite VALUES(20190003, 'FEI0004', 17, 12, 15);
INSERT INTO EtudiantUnite VALUES(20190004, 'FEI0004', 12, 13, 20);

commit;



UPDATE EtudiantUnite
SET note_CC = note_CC + 2
WHERE matricule_etu IN (
    SELECT matricule_etu
    FROM Etudiant
    WHERE nom_etu LIKE 'B%'
);

UPDATE EtudiantUnite
SET note_examen = 0
WHERE code_Unite = 'FEI0004'; 


 /* tache 5 */
 
SELECT DISTINCT nom_etu, prenom_etu
FROM Etudiant, EtudiantUnite
WHERE Etudiant.matricule_etu = EtudiantUnite.matricule_etu
AND note_examen = 20;


SELECT nom_etu, prenom_etu 
FROM Etudiant 
WHERE matricule_etu NOT IN (
               SELECT matricule_etu 
			   FROM EtudiantUnite 
			   WHERE code_Unite = 'FEI0001');
			   

SELECT libelle 
FROM Unite 
WHERE code_Unite NOT IN (
               SELECT DISTINCT code_Unite 
			   FROM EtudiantUnite);
			   

SELECT E.nom_etu, E.prenom_etu, U.libelle, AVG(UE.note_examen) AS moyenne
FROM Etudiant E, EtudiantUnite UE, Unite U
WHERE E.matricule_etu = UE.matricule_etu
AND UE.code_Unite = U.code_Unite
GROUP BY E.nom_etu, E.prenom_etu, U.libelle;
