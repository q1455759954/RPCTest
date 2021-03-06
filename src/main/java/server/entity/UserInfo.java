package server.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserInfo implements Serializable {


    public UserInfo() {
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                '}';
    }

    private Integer id;

    private String nickname;

    private String avatar;

    private String gender;

    private Integer age;

    private String identifier;

    private String birthday;

    /**
     * 这个标志位判断是否改变了头像
     */
    private boolean ifChangedAvatar = false;

    private String school;

    private String introduce;

    private int attends;

    private int fans;

    /**
     * 判断有没有关注
     */
    private boolean ifAttend;

    public boolean isIfAttend() {
        return ifAttend;
    }

    public void setIfAttend(boolean ifAttend) {
        this.ifAttend = ifAttend;
    }

    public int getAttends() {
        return attends;
    }

    public void setAttends(int attends) {
        this.attends = attends;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        if (birthday.equals("0")||birthday.equals("")){
            birthday="2019-9-14";
        }else {
            this.birthday = birthday;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd");
            Date date = formatter.parse(birthday);
            this.age = getAgeByBirth(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public boolean isIfChangedAvatar() {
        return ifChangedAvatar;
    }

    public void setIfChangedAvatar(boolean ifChangedAvatar) {
        this.ifChangedAvatar = ifChangedAvatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    //计算年龄
    private static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }
}