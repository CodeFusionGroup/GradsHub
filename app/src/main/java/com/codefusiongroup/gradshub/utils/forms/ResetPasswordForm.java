package com.codefusiongroup.gradshub.utils.forms;

import com.codefusiongroup.gradshub.databinding.FragmentResetPasswordBinding;

public class ResetPasswordForm {

    private static ResetPasswordForm instance;
    private FragmentResetPasswordBinding resetPasswordBinding;

    // constructor private so that only one instance of LoginForm object is created and no other
    // class can instantiate it directly
    private ResetPasswordForm() { }

    // singleton pattern
    public static ResetPasswordForm getInstance() {
        if (instance == null) {
            instance = new ResetPasswordForm();
        }
        return instance;
    }

    public void setResetPasswordBinding(FragmentResetPasswordBinding binding) {
        this.resetPasswordBinding = binding;
    }

    public FragmentResetPasswordBinding getResetPasswordBinding() { return resetPasswordBinding; }

}
