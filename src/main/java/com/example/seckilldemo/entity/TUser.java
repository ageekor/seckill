package com.example.seckilldemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;


@Getter
@TableName("t_user")
public class TUser implements Serializable {

    private Long id;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;

    @Override
    public String toString() {
        return "TUser{" +
                "id=" + id +
                ", nickname=" + nickname +
                ", password=" + password +
                ", salt=" + salt +
                ", head=" + head +
                ", registerDate=" + registerDate +
                ", lastLoginDate=" + lastLoginDate +
                ", loginCount=" + loginCount +
                "}";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

}
