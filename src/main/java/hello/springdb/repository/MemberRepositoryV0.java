package hello.springdb.repository;

import hello.springdb.connection.DBConnectionUtil;
import hello.springdb.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */

@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }

    }

    public Member findById(String memberId) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    public void update(String memberId, int money) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public void delete(String memberId) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }

    }


    private void close(Connection connection, Statement statement, ResultSet resultSet) {

        // Connection -> Statement -> ResultSet으로 획득했으니 close는 역순!
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("error", e);
                throw new RuntimeException(e);
            }

        }

        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("error", e);
                throw new RuntimeException(e);
            }
        }

        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.info("error", e);
                throw new RuntimeException(e);
            }
        }



    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
