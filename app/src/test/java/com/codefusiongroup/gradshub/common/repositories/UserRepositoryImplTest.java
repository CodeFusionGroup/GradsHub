package com.codefusiongroup.gradshub.common.repositories;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.common.models.User;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class UserRepositoryImplTest {
    @InjectMocks
    UserRepositoryImpl userRepository;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void registerUser() {
       MutableLiveData mutableLiveData = mock(MutableLiveData.class);
        User user  = new User("fname", "lname", "tester141414fhfvbd@gmail.com", "0123456789", "Honors", "pass","fcomToken");
        //doNothing().when(mutableLiveData).setValue(true);
        UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();
        userRepository.registerUser(user);
    }
}