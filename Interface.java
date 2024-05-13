import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;


public class Interface extends JFrame {
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JButton connectButton;
    private JPanel mainPanel;

    public Interface() {
        setTitle("Connexion");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        usernameLabel = new JLabel("Nom d'utilisateur:");
        passwordLabel = new JLabel("Mot de passe:");
        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        connectButton = new JButton("Se connecter");

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals("BDDAdmin") && password.equals("TPAdmin")) {
                    // Pour l'utilisateur "BDDAdmin", tous les privilèges sont accordés
                    JOptionPane.showMessageDialog(null, "Bienvenue, BDDAdmin ! Tous les privilèges sont accordés.");
                    // Code pour ouvrir la fenêtre avec tous les privilèges
                    String[] options = {"Consultation des tables", "Insertion dans les tables", "Suppression dans les tables", "Les requêtes"};
                    int choice = JOptionPane.showOptionDialog(null,
                            "Choisissez une action",
                            "Actions BDDAdmin",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (choice == 0) {
                        consultationTables();
                    } else if (choice == 1) {
                        insertionTables();
                    } else if (choice == 2) {
                        suppressionTables();
                    } else if (choice == 3) {
                        // Les requêtes
                        String[] queryOptions = {"Afficher les libellés des unités d'enseignement sans étudiants inscrits",
                                "Afficher la moyenne par unité d'enseignement pour chaque étudiant"};
                        int queryChoice = JOptionPane.showOptionDialog(null,
                                "Choisissez une requête à exécuter",
                                "Les requêtes",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                queryOptions,
                                queryOptions[0]);

                        if (queryChoice == 0) {
                            afficherLibellesUnitesSansEtudiant();
                        } else if (queryChoice == 1) {
                            afficherMoyenneParUniteEtudiant();
                        }
                    }


                }else if (username.equals("Etudiant") && password.equals("TPEtudiant")) {
                    // Pour l'utilisateur "Etudiant", seule la consultation de la table étudiant est autorisée
                    String matricule = JOptionPane.showInputDialog(null, "Bienvenue, Etudiant ! Veuillez saisir votre matricule :");
                    if (matricule != null && !matricule.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Consultation autorisée pour l'étudiant avec matricule " + matricule);
                        // Code pour ouvrir la fenêtre de consultation de la table étudiant
                        consultationEtudiant(matricule);
                    } else {
                        JOptionPane.showMessageDialog(null, "Matricule invalide !");
                    }
                } else if (username.equals("Enseignant") && password.equals("TPEnseignant")) {
                    // Pour l'utilisateur "Enseignant", l'insertion dans la table Enseignant est autorisée ainsi que la consultation
                    JOptionPane.showMessageDialog(null, "Bienvenue, Enseignant !");
                    int choix = JOptionPane.showOptionDialog(null, "Que voulez-vous faire ?", "Menu Enseignant",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                            new Object[]{"Ajouter un enseignant", "Consulter les enseignants"}, null);

                    if (choix == 0) {
                        // Ajout d'un enseignant
                        ajouterEnseignant();
                    } else if (choix == 1) {
                        // Consultation des enseignants
                        consultationEnseignants();
                    }
                } else {
                    // Utilisateur non reconnu, afficher un message d'erreur
                    JOptionPane.showMessageDialog(null, "Nom d'utilisateur ou mot de passe incorrect !");
                }
            }
        });

        mainPanel = new JPanel();
        mainPanel.add(usernameLabel);
        mainPanel.add(usernameField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(connectButton);

        add(mainPanel);

        setVisible(true);
    }

    public static void consultationEtudiant(String matricule) {
        // Connexion à la base de données
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String utilisateurBDD = "BDDAdmin";
        String motDePasseBDD = "TPAdmin";

        try {
            // Charger le pilote JDBC Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Établir la connexion à la base de données
            Connection connexion = DriverManager.getConnection(url, utilisateurBDD, motDePasseBDD);

            // Créer l'objet Statement pour exécuter la requête
            Statement statement = connexion.createStatement();

            // Exécuter la requête pour la table "Etudiant"
            String requeteEtudiant = "SELECT * FROM Etudiant WHERE matricule_etu = '" + matricule + "'";
            ResultSet resultSetEtudiant = statement.executeQuery(requeteEtudiant);

            // Afficher les résultats de la table "Etudiant"
            StringBuilder resultsEtudiant = new StringBuilder();
            while (resultSetEtudiant.next()) {
                resultsEtudiant.append("Matricule: ").append(resultSetEtudiant.getString("matricule_etu")).append("\n");
                resultsEtudiant.append("Nom: ").append(resultSetEtudiant.getString("nom_etu")).append("\n");
                resultsEtudiant.append("Prénom: ").append(resultSetEtudiant.getString("prenom_etu")).append("\n");
                resultsEtudiant.append("Date de naissance: ").append(resultSetEtudiant.getString("date_naissance")).append("\n");
                // Ajouter les autres colonnes si nécessaire
                resultsEtudiant.append("\n");
            }
            JOptionPane.showMessageDialog(null, resultsEtudiant.toString(), "Résultats de la requête - Etudiant", JOptionPane.INFORMATION_MESSAGE);
            // Fermer les ressources
            resultSetEtudiant.close();
            statement.close();
            connexion.close();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void consultationEnseignants() {
        // Connexion à la base de données
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String utilisateurBDD = "BDDAdmin";
        String motDePasseBDD = "TPAdmin";

        try {
            // Charger le pilote JDBC Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Établir la connexion à la base de données
            Connection connexion = DriverManager.getConnection(url, utilisateurBDD, motDePasseBDD);

            // Créer l'objet Statement pour exécuter la requête
            Statement statement = connexion.createStatement();

            // Exécuter la requête pour la table "Enseignant"
            String requeteEnseignant = "SELECT * FROM Enseignant";
            ResultSet resultSetEnseignant = statement.executeQuery(requeteEnseignant);

            // Afficher les résultats de la table "Enseignant"
            StringBuilder resultsEnseignant = new StringBuilder();
            while (resultSetEnseignant.next()) {
                resultsEnseignant.append("Matricule: ").append(resultSetEnseignant.getString("matricule_ens")).append("\n");
                resultsEnseignant.append("Nom: ").append(resultSetEnseignant.getString("nom_ens")).append("\n");
                resultsEnseignant.append("Prénom: ").append(resultSetEnseignant.getString("prenom_ens")).append("\n");
                // Ajouter les autres colonnes si nécessaire
                resultsEnseignant.append("\n");
            }
            JOptionPane.showMessageDialog(null, resultsEnseignant.toString(), "Résultats de la requête - Enseignant", JOptionPane.INFORMATION_MESSAGE);
            // Fermer les ressources
            resultSetEnseignant.close();
            statement.close();
            connexion.close();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void ajouterEnseignant() {
        try {
            // Charger le pilote JDBC Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Informations de connexion à la base de données Oracle
            String url = "jdbc:oracle:thin:@localhost:1521:XE";
            String utilisateurBDD = "BDDAdmin";
            String motDePasseBDD = "TPAdmin";

            // Requête d'insertion SQL
            String requeteInsertion = "INSERT INTO Enseignant (matricule_ens, nom_ens, prenom_ens) VALUES (?, ?, ?)";

            // Établir la connexion à la base de données
            Connection connexion = DriverManager.getConnection(url, utilisateurBDD, motDePasseBDD);

            // Créer l'objet PreparedStatement avec la requête d'insertion
            PreparedStatement preparedStatement = connexion.prepareStatement(requeteInsertion);

            // Demander les informations sur l'enseignant à ajouter
            int matricule = Integer.parseInt(JOptionPane.showInputDialog(null, "Veuillez saisir la matricule de l'enseignant :"));
            String nom = JOptionPane.showInputDialog(null, "Veuillez saisir le nom de l'enseignant :");
            String prenom = JOptionPane.showInputDialog(null, "Veuillez saisir le prénom de l'enseignant :");

            // Affecter les valeurs saisies par l'utilisateur aux paramètres de la requête
            preparedStatement.setInt(1, matricule);
            preparedStatement.setString(2, nom);
            preparedStatement.setString(3, prenom);

            // Exécuter la requête d'insertion
            int lignesAjoutees = preparedStatement.executeUpdate();

            if (lignesAjoutees > 0) {
                JOptionPane.showMessageDialog(null, "Le tuple a été ajouté avec succès.");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du tuple.");
            }

            // Fermer les ressources
            preparedStatement.close();
            connexion.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void afficherLibellesUnitesSansEtudiant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String requete = "SELECT libelle " +
                    "FROM Unite " +
                    "WHERE code_Unite NOT IN (" +
                    "SELECT DISTINCT code_Unite FROM EtudiantUnite)";
            Statement statement = connexion.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            StringBuilder resultat = new StringBuilder();
            resultat.append("Libellés des unités d'enseignement sans étudiant inscrit :\n");
            while (resultSet.next()) {
                resultat.append(resultSet.getString("libelle")).append("\n");
            }
            JOptionPane.showMessageDialog(null, resultat.toString(), "Libellés des unités sans étudiants", JOptionPane.INFORMATION_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afficherMoyenneParUniteEtudiant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String requete = "SELECT E.nom_etu, E.prenom_etu, U.libelle, AVG(UE.note_examen) AS moyenne " +
                    "FROM Etudiant E, EtudiantUnite UE, Unite U " +
                    "WHERE E.matricule_etu = UE.matricule_etu " +
                    "AND UE.code_Unite = U.code_Unite " +
                    "GROUP BY E.nom_etu, E.prenom_etu, U.libelle";
            Statement statement = connexion.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            StringBuilder resultat = new StringBuilder();
            resultat.append("Moyenne par unité d'enseignement pour chaque étudiant :\n");
            while (resultSet.next()) {
                resultat.append("Nom : ").append(resultSet.getString("nom_etu")).append("\n");
                resultat.append("Prénom : ").append(resultSet.getString("prenom_etu")).append("\n");
                resultat.append("Unité d'enseignement : ").append(resultSet.getString("libelle")).append("\n");
                resultat.append("Moyenne : ").append(resultSet.getDouble("moyenne")).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, resultat.toString(), "Moyenne par unité pour chaque étudiant", JOptionPane.INFORMATION_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void consultationTables() {
        String[] options = {"Etudiant", "Enseignant", "Unite", "EtudiantUnite"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choisissez la table à consulter",
                "Consultation des tables",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            consultationTableEtudiant();
        } else if (choice == 1) {
            consultationTableEnseignant();
        } else if (choice == 2) {
            consultationTableUnite();
        } else if (choice == 3) {
            consultationTableEtudiantUnite();
        }
    }

    public static void consultationTableEtudiant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String requete = "SELECT * FROM Etudiant";
            Statement statement = connexion.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            StringBuilder resultat = new StringBuilder();
            resultat.append("Consultation de la table Etudiant :\n");
            while (resultSet.next()) {
                resultat.append("Matricule : ").append(resultSet.getInt("matricule_etu")).append("\n");
                resultat.append("Nom : ").append(resultSet.getString("nom_etu")).append("\n");
                resultat.append("Prénom : ").append(resultSet.getString("prenom_etu")).append("\n");
                resultat.append("Date de naissance : ").append(resultSet.getString("date_naissance")).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, resultat.toString(), "Table Etudiant", JOptionPane.INFORMATION_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void consultationTableEnseignant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String requete = "SELECT * FROM Enseignant";
            Statement statement = connexion.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            StringBuilder resultat = new StringBuilder();
            resultat.append("Consultation de la table Enseignant :\n");
            while (resultSet.next()) {
                resultat.append("Matricule : ").append(resultSet.getInt("matricule_ens")).append("\n");
                resultat.append("Nom : ").append(resultSet.getString("nom_ens")).append("\n");
                resultat.append("Prénom : ").append(resultSet.getString("prenom_ens")).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, resultat.toString(), "Table Enseignant", JOptionPane.INFORMATION_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void consultationTableUnite() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String requete = "SELECT * FROM Unite";
            Statement statement = connexion.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            StringBuilder resultat = new StringBuilder();
            resultat.append("Consultation de la table Unite :\n");
            while (resultSet.next()) {
                resultat.append("Code : ").append(resultSet.getString("code_unite")).append("\n");
                resultat.append("Libellé : ").append(resultSet.getString("libelle")).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, resultat.toString(), "Table Unite", JOptionPane.INFORMATION_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void consultationTableEtudiantUnite() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String requete = "SELECT * FROM EtudiantUnite";
            Statement statement = connexion.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            StringBuilder resultat = new StringBuilder();
            resultat.append("Consultation de la table EtudiantUnite :\n");
            while (resultSet.next()) {
                resultat.append("Matricule étudiant : ").append(resultSet.getInt("matricule_etu")).append("\n");
                resultat.append("Code unité : ").append(resultSet.getString("code_unite")).append("\n");
                resultat.append("Note examen : ").append(resultSet.getDouble("note_examen")).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, resultat.toString(), "Table EtudiantUnite", JOptionPane.INFORMATION_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertionTables() {
        String[] options = {"Etudiant", "Enseignant", "Unite", "EtudiantUnite"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choisissez la table pour l'insertion",
                "Insertion dans les tables",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            insertionTableEtudiant();
        } else if (choice == 1) {
            insertionTableEnseignant();
        } else if (choice == 2) {
            insertionTableUnite();
        } else if (choice == 3) {
            insertionTableEtudiantUnite();
        }
    }

    public static void insertionTableEtudiant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String matricule = JOptionPane.showInputDialog(null, "Entrez le matricule de l'étudiant:");
            String nom = JOptionPane.showInputDialog(null, "Entrez le nom de l'étudiant:");
            String prenom = JOptionPane.showInputDialog(null, "Entrez le prénom de l'étudiant:");
            String dateNaissance = JOptionPane.showInputDialog(null, "Entrez la date de naissance de l'étudiant:");

            String requete = "INSERT INTO Etudiant(matricule_etu, nom_etu, prenom_etu, date_naissance) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connexion.prepareStatement(requete);
            statement.setInt(1, Integer.parseInt(matricule));
            statement.setString(2, nom);
            statement.setString(3, prenom);
            statement.setString(4, dateNaissance);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "L'étudiant a été ajouté avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout de l'étudiant !");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertionTableEnseignant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String matricule = JOptionPane.showInputDialog(null, "Entrez le matricule de l'enseignant:");
            String nom = JOptionPane.showInputDialog(null, "Entrez le nom de l'enseignant:");
            String prenom = JOptionPane.showInputDialog(null, "Entrez le prénom de l'enseignant:");

            String requete = "INSERT INTO Enseignant(matricule_ens, nom_ens, prenom_ens) VALUES (?, ?, ?)";
            PreparedStatement statement = connexion.prepareStatement(requete);
            statement.setInt(1, Integer.parseInt(matricule));
            statement.setString(2, nom);
            statement.setString(3, prenom);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "L'enseignant a été ajouté avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout de l'enseignant !");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertionTableUnite() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String code = JOptionPane.showInputDialog(null, "Entrez le code de l'unité:");
            String libelle = JOptionPane.showInputDialog(null, "Entrez le libellé de l'unité:");
            String matriculeEns = JOptionPane.showInputDialog(null, "Entrez le matricule de l'enseignant du module:");

            // Vérifier si le matricule_ens existe dans la table Enseignant
            String selectQuery = "SELECT COUNT(*) FROM Enseignant WHERE matricule_ens = ?";
            PreparedStatement selectStatement = connexion.prepareStatement(selectQuery);
            selectStatement.setString(1, matriculeEns);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                // Le matricule_ens existe, procéder à l'insertion dans la table Unite
                String insertQuery = "INSERT INTO Unite(code_unite, libelle, matricule_ens) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connexion.prepareStatement(insertQuery);
                insertStatement.setString(1, code);
                insertStatement.setString(2, libelle);
                insertStatement.setString(3, matriculeEns);

                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "L'unité a été ajoutée avec succès !");
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout de l'unité !");
                }

                insertStatement.close();
            } else {
                JOptionPane.showMessageDialog(null, "Le matricule de l'enseignant n'existe pas !");
            }

            selectStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertionTableEtudiantUnite() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String matricule = JOptionPane.showInputDialog(null, "Entrez le matricule de l'étudiant:");
            String codeUnite = JOptionPane.showInputDialog(null, "Entrez le code de l'unité:");
            String noteExamen = JOptionPane.showInputDialog(null, "Entrez la note de l'examen:");

            // Vérifier si le matricule_etu existe dans la table Etudiant
            String selectQuery = "SELECT COUNT(*) FROM Etudiant WHERE matricule_etu = ?";
            PreparedStatement selectStatement = connexion.prepareStatement(selectQuery);
            selectStatement.setInt(1, Integer.parseInt(matricule));
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                // Le matricule_etu existe, procéder à l'insertion dans la table EtudiantUnite
                String insertQuery = "INSERT INTO EtudiantUnite(matricule_etu, code_unite, note_examen) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connexion.prepareStatement(insertQuery);
                insertStatement.setInt(1, Integer.parseInt(matricule));
                insertStatement.setString(2, codeUnite);
                insertStatement.setDouble(3, Double.parseDouble(noteExamen));

                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "L'étudiant a été inscrit à l'unité avec succès !");
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'inscription de l'étudiant à l'unité !");
                }

                insertStatement.close();
            } else {
                JOptionPane.showMessageDialog(null, "Le matricule de l'étudiant n'existe pas !");
            }

            selectStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void suppressionTables() {
        String[] options = {"Etudiant", "Enseignant", "Unite", "EtudiantUnite"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choisissez la table pour la suppression",
                "Suppression dans les tables",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            suppressionTableEtudiant();
        } else if (choice == 1) {
            suppressionTableEnseignant();
        } else if (choice == 2) {
            suppressionTableUnite();
        } else if (choice == 3) {
            suppressionTableEtudiantUnite();
        }
    }

    public static void suppressionTableEtudiant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String matricule = JOptionPane.showInputDialog(null, "Entrez le matricule de l'étudiant à supprimer:");

            String requete = "DELETE FROM Etudiant WHERE matricule_etu = ?";
            PreparedStatement statement = connexion.prepareStatement(requete);
            statement.setInt(1, Integer.parseInt(matricule));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "L'étudiant a été supprimé avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'étudiant !");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void suppressionTableEnseignant() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String matricule = JOptionPane.showInputDialog(null, "Entrez le matricule de l'enseignant à supprimer:");

            String requete = "DELETE FROM Enseignant WHERE matricule_ens = ?";
            PreparedStatement statement = connexion.prepareStatement(requete);
            statement.setInt(1, Integer.parseInt(matricule));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "L'enseignant a été supprimé avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'enseignant !");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void suppressionTableUnite() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String code = JOptionPane.showInputDialog(null, "Entrez le code de l'unité à supprimer:");

            String requete = "DELETE FROM Unite WHERE code_unite = ?";
            PreparedStatement statement = connexion.prepareStatement(requete);
            statement.setString(1, code);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "L'unité a été supprimée avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'unité !");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void suppressionTableEtudiantUnite() {
        try (Connection connexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "BDDAdmin", "TPAdmin")) {
            String matricule = JOptionPane.showInputDialog(null, "Entrez le matricule de l'étudiant:");
            String codeUnite = JOptionPane.showInputDialog(null, "Entrez le code de l'unité:");

            String requete = "DELETE FROM EtudiantUnite WHERE matricule_etu = ? AND code_unite = ?";
            PreparedStatement statement = connexion.prepareStatement(requete);
            statement.setInt(1, Integer.parseInt(matricule));
            statement.setString(2, codeUnite);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "L'étudiant a été désinscrit de l'unité avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la désinscription de l'étudiant de l'unité !");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Interface();
            }
        });
    }
}