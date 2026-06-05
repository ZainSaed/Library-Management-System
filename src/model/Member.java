package model;

public class Member extends Person {


    private String memberId;    
    private String joinedDate;  

        public Member() {
        super();
    }

        public Member(int personId, String name, String email,
                  String phone, String address,
                  String memberId, String joinedDate) {

    
        super(personId, name, email, phone, address);

        this.memberId   = memberId;
        this.joinedDate = joinedDate;
    }

    @Override
    public String getRole() {
        return "Library Member";
    }


    public String getMemberId()   { return memberId;   }
    public String getJoinedDate() { return joinedDate; }


    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    @Override
    public String toString() {
        return "Member{" +
               "memberId='"   + memberId   + '\'' +
               ", name='"     + getName()  + '\'' +
               ", email='"    + getEmail() + '\'' +
               ", phone='"    + getPhone() + '\'' +
               ", joinedDate='"+ joinedDate + '\'' +
               ", role='"     + getRole()  + '\'' +
               '}';
    }
}
