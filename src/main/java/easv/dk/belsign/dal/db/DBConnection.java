package easv.dk.belsign.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class DBConnection {
    public Connection getConnection() throws SQLServerException {
        SQLServerDataSource ds;
        ds = new SQLServerDataSource();
        ds.setDatabaseName("ExamProjectCSe24GroupX");
        ds.setUser("CSe2024b_e_6");
        ds.setPassword("CSe2024bE6!24");
        ds.setServerName("EASV-DB4");
        ds.setPortNumber(1433);
        ds.setTrustServerCertificate(true);
        return ds.getConnection();
    }
}
