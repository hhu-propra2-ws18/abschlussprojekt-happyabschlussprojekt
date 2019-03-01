package com.propra.happybay.Service.UserServices;


import com.propra.happybay.Repository.TransactionRepository;
import com.propra.happybay.Service.UserServices.PictureService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class PictureServiceTest {

    @InjectMocks
    PictureService pictureService;

    @Test
    public void renturnListOfPic() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        MultipartFile[] files = {file,file,file,file};
        when(file.getBytes()).thenReturn("fake".getBytes());
        Assertions.assertThat(pictureService
                    .returnListOfPictures(files)
                    .size())
                    .isEqualTo(4);

    }
}