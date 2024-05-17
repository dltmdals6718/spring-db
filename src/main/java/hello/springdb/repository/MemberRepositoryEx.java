package hello.springdb.repository;

import hello.springdb.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryEx {

    // 구현체에서 체크 예외를 던지려면, 인터페이스에서도 throws를 선언되어 있어야 던질 수 있다.
    Member save(Member member) throws SQLException;

    // 구현 클래스에서 던지는 예외는 하위 클래스도 가능하다.
    Member delete(String memberId) throws Exception;

}
