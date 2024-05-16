package hello.springdb.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;


/**
 * 복구할 수 없는 예외들을 Runtime 예외로 변경하여 예외 의존 관계 해소
 * 이 예외들을 공통 처리 부분에서 한번에 처리하면 좋음.
 */

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions
                .assertThatThrownBy(() -> controller.request())
                .isInstanceOf(RuntimeSQLException.class);
    }

    @Test
    void printEx() {

        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }

    }



    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }

    }

    static class Service {

        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {

        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }


    }

    static class Repository {
        public void call()  {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }


    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

}
