package hello.springdb.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
class DBConnectionUtilTest {

    @Test
    @DisplayName("connection")
    void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        Assertions
                .assertThat(connection)
                .isNotNull();
    }

}
