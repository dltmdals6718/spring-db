package hello.springdb.exception.translator;

import hello.springdb.connection.ConnectionConst;
import hello.springdb.connection.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            Assertions
                    .assertThat(e.getErrorCode())
                    .isEqualTo(42122);

            int errorCode = e.getErrorCode();
            log.info("errorCode={}", errorCode);
            log.info("error={}", e);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            Assertions
                    .assertThat(e.getErrorCode())
                    .isEqualTo(42122);

            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException dataAccessException = translator.translate("select", sql, e);
            log.info("Exception", dataAccessException);
            Assertions
                    .assertThat(dataAccessException.getClass())
                    .isEqualTo(BadSqlGrammarException.class);
        }
    }
}
