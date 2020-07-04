package com.selfStudy.quicksaleevent.vo;


import com.selfStudy.quicksaleevent.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginVo {
    /**
     * the class represent data transcript object during user login
     */

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 5) // for password plain-text
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
