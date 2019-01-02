package org.wing.mocker.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wing.mocker.core.MockData;
import org.wing.mocker.http.model.PomLocation;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockerHttpApplicationTests {

    @Autowired
    private MavenRepositoryService mavenRepositoryService;
    PomLocation pomLocation = new PomLocation("com.qccr.shprod", "shprod-facade", "3.9.9.9-SNAPSHOT");

    @Test
    public void contextLoads() {
        System.out.println(mavenRepositoryService.getJarClassURL(pomLocation));
    }

    @Test
    public void allTest() throws IOException, ClassNotFoundException {
        System.out.println("start");
        String jarUrl = mavenRepositoryService.getJarClassURL(pomLocation);
        JarService jarService=new JarService(jarUrl);
        List<Class> classes= jarService.getClassList();
        for (Class aClass : classes) {
            try {
                new ObjectMapper().writeValueAsString(new MockData().mock(aClass));
                System.out.println(aClass+"成功");
            }catch (Throwable e){
                System.err.println(aClass+"失败"+e.getMessage());
            }
        }
    }

    



}

