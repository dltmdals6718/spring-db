package hello.springdb.repository;

import hello.springdb.connection.DBConnectionUtil;
import hello.springdb.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

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

    private void close(Connection connection, Statement statement, ResultSet resultSet) {

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
