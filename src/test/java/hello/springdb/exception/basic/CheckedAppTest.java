package hello.springdb.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;


/**
 *
 * 체크 에외를 사용시 문제점.
 *
 * - 예외에 대한 의존관계 문제
 * 향후 리포지토리를 JDBC 기술이 아닌걸로 변경한다면
 * 컨트롤러까지 올라오는 모든 SQLException 예외를 JpaException으로 변경해야한다.
 *
 * 그렇다고 Exception으로 thows를 해버리면 중요한 예외 구분을 못함
 * 그래서 보통 언체크 예외를 주로 사용함.
 */
public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions
                .assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }



    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }

    }

    static class Service {

        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }

    }

    static class NetworkClient {

        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }


    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }


}
