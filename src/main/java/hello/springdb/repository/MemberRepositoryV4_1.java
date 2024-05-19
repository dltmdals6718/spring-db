package hello.springdb.repository;

import hello.springdb.domain.Member;
import hello.springdb.repository.exception.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLExcpetion 제거
 */

@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)";

        Connection conn = null; // java.sql.Connection : 연결
        PreparedStatement pstmt = null; // java.sql.Statement : SQL을 담을 내용 - Statement를 상속 받은 PreparedStatement


        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }

    }

    @Override
    public Member findById(String memberId) {
        String sql = "SELECT * FROM member where member_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery();// <- 데이터 조회할때
            //preparedStatement.executeUpdate(); <- 데이터 변경할때

            if(resultSet.next()) { // resultset의 커서를 한번 옮겨야 실제 데이터 위치로 이동함
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found member_id = " + memberId);
            }
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultSize = preparedStatement.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            JdbcUtils.closeStatement(preparedStatement);
            //connection은 여기서 닫지 않는다.
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "DELETE FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            int resultSize = preparedStatement.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(connection, preparedStatement, null);
        }

    }


    private void close(Connection connection, Statement statement, ResultSet resultSet) {

        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다.
        DataSourceUtils.releaseConnection(connection, dataSource);
//        JdbcUtils.closeConnection(connection);

    }

    private Connection getConnection() throws SQLException {

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다.
        Connection connection = DataSourceUtils.getConnection(dataSource);

        log.info("get connection={}, class={}", connection, connection.getClass());
        return connection;
    }

}
