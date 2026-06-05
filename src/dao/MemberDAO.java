package dao;

import model.Member;
import exception.MemberNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {


    private static final String INSERT_MEMBER =
        "INSERT INTO members (name, email, phone, address) " +
        "VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL_MEMBERS =
        "SELECT * FROM members ORDER BY name";

    private static final String SELECT_BY_ID =
        "SELECT * FROM members WHERE member_id = ?";

    private static final String SELECT_BY_NAME =
        "SELECT * FROM members WHERE name LIKE ?";

    private static final String SELECT_BY_EMAIL =
        "SELECT * FROM members WHERE email = ?";

    private static final String UPDATE_MEMBER =
        "UPDATE members SET name=?, email=?, phone=?, address=? " +
        "WHERE member_id=?";

    private static final String DELETE_MEMBER =
        "DELETE FROM members WHERE member_id = ?";

    private static final String COUNT_MEMBERS =
        "SELECT COUNT(*) FROM members";

    public boolean registerMember(Member member) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_MEMBER);

            stmt.setString(1, member.getName());    
            stmt.setString(2, member.getEmail());   
            stmt.setString(3, member.getPhone());   
            stmt.setString(4, member.getAddress()); 

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error registering member: " + e.getMessage());
            return false;
        }
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(SELECT_ALL_MEMBERS);

            while (rs.next()) {
                members.add(extractMember(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error fetching members: " + e.getMessage());
        }

        return members;
    }

    public Member getMemberById(int memberId) throws MemberNotFoundException {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setInt(1, memberId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Member member = extractMember(rs);
                rs.close();
                stmt.close();
                return member;
            } else {
                throw new MemberNotFoundException(
                    "Member with ID " + memberId + " not found."
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Error finding member: " + e.getMessage());
            throw new MemberNotFoundException(
                "Database error while finding member ID: " + memberId
            );
        }
    }

    public List<Member> searchByName(String name) {
        List<Member> members = new ArrayList<>();

        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NAME);
            stmt.setString(1, "%" + name + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(extractMember(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error searching members: " + e.getMessage());
        }

        return members;
    }

    public boolean emailExists(String email) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EMAIL);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next(); 

            rs.close();
            stmt.close();

            return exists;

        } catch (SQLException e) {
            System.out.println("❌ Error checking email: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMember(Member member) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_MEMBER);

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getAddress());
            stmt.setInt   (5, member.getPersonId()); 

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating member: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMember(int memberId) {
        try {
            Connection        conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_MEMBER);
            stmt.setInt(1, memberId);

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error deleting member: " + e.getMessage());
            return false;
        }
    }

    public int getTotalMemberCount() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery(COUNT_MEMBERS);

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error counting members: " + e.getMessage());
        }
        return 0;
    }

    private Member extractMember(ResultSet rs) throws SQLException {
        Member member = new Member();

        member.setPersonId (rs.getInt   ("member_id"));
        member.setName     (rs.getString("name"));
        member.setEmail    (rs.getString("email"));
        member.setPhone    (rs.getString("phone"));
        member.setAddress  (rs.getString("address"));

        member.setMemberId  (String.valueOf(rs.getInt("member_id")));
        member.setJoinedDate(rs.getString("joined_date"));

        return member;
    }
}
