
package model;

public abstract class Person {

   
    private int    personId;   
    private String name;       
    private String email;      
    private String phone;      
    private String address;    

        public Person() {}

    public Person(int personId, String name, String email,
                  String phone, String address) {
        this.personId = personId;
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.address  = address;
    }

        public abstract String getRole();

    
    public int    getPersonId() { return personId; }
    public String getName()     { return name;     }
    public String getEmail()    { return email;    }
    public String getPhone()    { return phone;    }
    public String getAddress()  { return address;  }


    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person{" +
               "id="      + personId +
               ", name='" + name     + '\'' +
               ", email='"+ email    + '\'' +
               ", role='" + getRole()+ '\'' +
               '}';
    }
}
