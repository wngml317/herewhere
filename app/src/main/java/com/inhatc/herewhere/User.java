package com.inhatc.herewhere;

public class User {
    String ID;
    String PW;
    String phone;
    String phone2;
    String name;
    String birth;
    String height;
    String weight;
    String bloodType;
    String gender;
    String motionSensor;
    String guardianMessage;

    public User(){}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPW() {
        return PW;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMotionSensor() {
        return motionSensor;
    }

    public void setMotionSensor(String motionSensor) {
        this.motionSensor = motionSensor;
    }

    public String getGuardianMessage() {
        return guardianMessage;
    }

    public void setGuardianMessage(String guardianMessage) {
        this.guardianMessage = guardianMessage;
    }

    public User(String ID, String PW, String phone, String phone2, String name, String birth,
                String height, String weight, String bloodType, String gender, String motionSensor, String guardianMessage) {
        this.ID = ID;
        this.PW = PW;
        this.phone = phone;
        this.phone2 = phone2;
        this.name = name;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.gender = gender;
        this.motionSensor = motionSensor;
        this.guardianMessage = guardianMessage;
    }

}
