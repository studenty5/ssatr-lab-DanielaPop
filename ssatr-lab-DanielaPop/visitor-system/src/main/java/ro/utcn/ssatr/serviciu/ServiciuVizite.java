package ro.utcn.ssatr.serviciu;

import ro.utcn.ssatr.db.DatabaseManager;
import ro.utcn.ssatr.model.Vizita;

import java.sql.*;
import java.util.UUID;

public class ServiciuVizite {

    public void adaugaVizita(Vizita vizita) {

        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = """
                INSERT INTO visits
                (id, visitor_name, email, host_name, visitor_type,
                 start_time, expiration_time, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setObject(1, vizita.getId());
            stmt.setString(2, vizita.getVizitator().getNume());
            stmt.setString(3, vizita.getVizitator().getEmail());
            stmt.setString(4, vizita.getGazda().getNume());
            stmt.setString(5, vizita.getVizitator().getTip().name());
            stmt.setTimestamp(6, Timestamp.valueOf(vizita.getDataInceput()));
            stmt.setTimestamp(7, Timestamp.valueOf(vizita.getDataSfarsit()));
            stmt.setString(8, vizita.getStatus().name());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void proceseazaIntrare(UUID idVizita) {

        try (Connection conn = DatabaseManager.getConnection()) {

            // Verificăm dacă vizita există și luăm datele
            String checkSql = """
            SELECT expiration_time, status
            FROM visits
            WHERE id = ?
        """;

            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setObject(1, idVizita);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Vizita nu exista in sistem!");
                return;
            }

            Timestamp expiration = rs.getTimestamp("expiration_time");
            String status = rs.getString("status");

            // Dacă este deja finalizată
            if (status.equals("IESITA")) {
                System.out.println("Vizita deja finalizata.");
                return;
            }

            // Dacă este deja în clădire
            if (status.equals("IN_CLADIRE")) {
                System.out.println("Vizitator deja in cladire.");
                return;
            }

            // Verificăm expirarea
            if (java.time.LocalDateTime.now()
                    .isAfter(expiration.toLocalDateTime())) {

                System.out.println("Vizita expirata! Acces refuzat.");

                PreparedStatement expireStmt = conn.prepareStatement(
                        "UPDATE visits SET status = 'EXPIRATA' WHERE id = ?");
                expireStmt.setObject(1, idVizita);
                expireStmt.executeUpdate();

                return;
            }

            // Permitem accesul
            PreparedStatement updateStmt = conn.prepareStatement("""
            UPDATE visits
            SET status = ?, entry_time = ?
            WHERE id = ?
        """);

            updateStmt.setString(1, "IN_CLADIRE");
            updateStmt.setTimestamp(2,
                    Timestamp.valueOf(java.time.LocalDateTime.now()));
            updateStmt.setObject(3, idVizita);

            updateStmt.executeUpdate();

            System.out.println("Acces permis.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void proceseazaIesire(UUID idVizita) {

        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = """
                UPDATE visits
                SET status = ?, exit_time = ?
                WHERE id = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "IESITA");
            stmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setObject(3, idVizita);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumarPersoaneInCladire() {

        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = "SELECT COUNT(*) FROM visits WHERE status = 'IN_CLADIRE'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}