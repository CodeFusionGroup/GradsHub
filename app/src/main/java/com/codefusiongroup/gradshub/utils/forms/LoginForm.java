package com.codefusiongroup.gradshub.utils.forms;

import com.codefusiongroup.gradshub.databinding.FragmentLoginBinding;

public class LoginForm {

    private static LoginForm instance;
    private FragmentLoginBinding loginBinding;

    // constructor private so that only one instance of LoginForm object is created and no other
    // class can instantiate it directly
    private LoginForm() { }

    // singleton pattern
    public static LoginForm getInstance() {
        if (instance == null) {
            instance = new LoginForm();
        }
        return instance;
    }

    public void setLoginBinding(FragmentLoginBinding binding) {
        this.loginBinding = binding;
    }

    public FragmentLoginBinding getLoginBinding() { return loginBinding; }

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static class LoginFormBuilder {

        private String email;
        private String password;

        public LoginFormBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public LoginFormBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public LoginForm build() {
            LoginForm loginForm = new LoginForm();
            loginForm.email = this.email;
            loginForm.password = this.password;
            return loginForm;
        }

    }

}
