package com.codefusiongroup.gradshub.mainUnitTests.createpost;

import android.content.Intent;
import android.net.Uri;

import com.codefusiongroup.gradshub.main.createpost.CreatePostFragment;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static android.app.Activity.RESULT_OK;

public class createpostUnitTest {
    @Mock
    CreatePostFragment createPostFragment;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    /*@Test
    public void someTest(){

    }
*/
    @Test
    public void onActivityResutTest(){
        CreatePostFragment createPost = new CreatePostFragment();
        final int i = 1;
        Intent data = new Intent();
        String s = "content://https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        Uri uri = Uri.parse(s);
        data.setData(uri);
        createPost.onActivityResult(1,RESULT_OK,  data);


    }

}
