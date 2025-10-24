package ru.kata.spring.boot_security.demo.dao;




import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;

import java.util.Arrays;

public class UserDao {
    private Long id;
//    private String name;

    private  String firstName;
    private  String lastName;

    private String password;
    private Integer age;
    private String[] roles;
    private String email;

    public UserDao() {}

    public UserDao(User user) {
        email = user.getEmail();
        id = user.getId();
//        name = user.getName();

        firstName = user.getFirstName();
        lastName = user.getLastName();

        password = user.getPassword();
        age = user.getAge();
        Object[] objectArr = user.getRoles().stream().map(Role::getNameRole).toArray();
        this.roles = Arrays.copyOf(objectArr, objectArr.length, String[].class);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getStringRole(){
        return Arrays.toString(roles);
    }

    @Override
    public String toString() {
        return "UserDao{" +
                "id=" + id +
                ", firsName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", roles=" + Arrays.toString(roles) +
                ", email='" + email + '\'' +
                '}';
    }
}

