package hello.springdb.repository;

import hello.springdb.domain.Member;

import java.sql.SQLException;

public class MemberRepositoryExImpl implements MemberRepositoryEx {

    @Override
    public Member save(Member member) throws SQLException {
        return null;
    }

    @Override
    public Member delete(String memberId) throws SQLException {
        return null;
    }
}
