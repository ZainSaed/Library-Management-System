package service;

import dao.MemberDAO;
import model.Member;
import exception.MemberNotFoundException;

import java.util.List;

public class MemberService {

    private MemberDAO memberDAO;

    public MemberService() {
        this.memberDAO = new MemberDAO();
    }

    public String registerMember(Member member) {

        if (member.getName() == null || member.getName().trim().isEmpty()) {
            return "❌ Member name cannot be empty.";
        }

        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            return "❌ Email address cannot be empty.";
        }

        if (!member.getEmail().contains("@")) {
            return "❌ Please enter a valid email address.";
        }

        if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
            return "❌ Phone number cannot be empty.";
        }

        if (memberDAO.emailExists(member.getEmail().trim())) {
            return "❌ Email '" + member.getEmail() + "' is already registered.";
        }

        member.setName (member.getName().trim());
        member.setEmail(member.getEmail().trim());
        member.setPhone(member.getPhone().trim());

        boolean success = memberDAO.registerMember(member);

        return success
            ? "✅ Member '" + member.getName() + "' registered successfully."
            : "❌ Failed to register member. Please try again.";
    }

    public List<Member> getAllMembers() {
        return memberDAO.getAllMembers();
    }

    public Member getMemberById(int memberId) {
        try {
            return memberDAO.getMemberById(memberId);
        } catch (MemberNotFoundException e) {
            System.out.println("Service: " + e.getMessage());
            return null;
        }
    }

    public List<Member> searchMembers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMembers();
        }
        return memberDAO.searchByName(keyword.trim());
    }

    public String updateMember(Member member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            return "❌ Member name cannot be empty.";
        }

        if (member.getEmail() == null || !member.getEmail().contains("@")) {
            return "❌ Please enter a valid email address.";
        }

        boolean success = memberDAO.updateMember(member);
        return success
            ? "✅ Member updated successfully."
            : "❌ Failed to update member.";
    }

    public String deleteMember(int memberId) {
        if (memberId <= 0) {
            return "❌ Invalid member ID.";
        }

        boolean success = memberDAO.deleteMember(memberId);
        return success
            ? "✅ Member deleted successfully."
            : "❌ Failed to delete member.";
    }

    public int getTotalMemberCount() {
        return memberDAO.getTotalMemberCount();
    }

     
    public Object[][] getMembersAsTableData(List<Member> members) {
        Object[][] data = new Object[members.size()][5];

        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            data[i][0] = m.getPersonId();   // ID (inherited from Person)
            data[i][1] = m.getName();        // inherited from Person
            data[i][2] = m.getEmail();       // inherited from Person
            data[i][3] = m.getPhone();       // inherited from Person
            data[i][4] = m.getJoinedDate();  // Member-specific field
        }

        return data;
    }

    public Object[][] getMembersAsTableData() {
        return getMembersAsTableData(getAllMembers());
    }

    public String[] getMemberTableColumns() {
        return new String[]{"ID", "Name", "Email", "Phone", "Joined Date"};
    }
}
