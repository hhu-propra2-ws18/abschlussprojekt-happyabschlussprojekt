package com.propra.happybay.Service;


import com.propra.happybay.Service.UserServices.PictureService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class PictureServiceTest {

    @InjectMocks
    PictureService pictureService;

    @Test
    public void renturnList_of_pic() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        MultipartFile[] files = {file,file,file,file};
        when(file.getBytes()).thenReturn("fake".getBytes());
        Assertions.assertThat(pictureService
                    .returnListOfPictures(files)
                    .size())
                    .isEqualTo(4);

    }





}