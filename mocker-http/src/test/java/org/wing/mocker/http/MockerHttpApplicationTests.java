package org.wing.mocker.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wing.mocker.core.MockData;
import org.wing.mocker.http.model.PomLocation;
import org.wing.mocker.http.service.JarService;
import org.wing.mocker.http.service.MavenRepositoryService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockerHttpApplicationTests {

    @Autowired
    private MavenRepositoryService mavenRepositoryService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private String shprodJar="http://192.168.0.107:8081/nexus/service/local/repositories/snapshots/content/com/qccr/shprod/shprod-facade/3.9.9.9-SNAPSHOT/shprod-facade-3.9.9.9-20181215.195710-450.jar";

    @Test
    public void testPomClass() throws ClassNotFoundException, JsonProcessingException, MalformedURLException {
        PomLocation pomLocation = new PomLocation("org.xiafei", "maven-view", "1.0-SNAPSHOT");
        String jarUrl = mavenRepositoryService.getJarClassURL(pomLocation);
        remoteMockTest(jarUrl, "com.nkz.test.ro.StoreRO");
    }

    @Test
    public void testRemoteClassMock() throws MalformedURLException, ClassNotFoundException, JsonProcessingException {
        String jar = "http://192.168.0.107:8081/nexus/content/groups/public/org/xiafei/maven-view/1.0-SNAPSHOT/maven-view-1.0-20190104.052538-1.jar";
        remoteMockTest(jar, "com.nkz.test.ro.StoreRO");
    }

    @Test
    public void testRemoteClassMock2() throws MalformedURLException, ClassNotFoundException, JsonProcessingException {
        remoteMockTest(shprodJar, "com.qccr.shprod.facade.entity.drawmoney.CanDrawAndRewardRO");
    }



    @Test
    public void testShprod() throws IOException, ClassNotFoundException {
        jarAllClassTest(shprodJar);
    }



    public void jarAllClassTest(String jarUrl) throws IOException, ClassNotFoundException {
        System.out.println("start");
        JarService jarService = new JarService(jarUrl);
        List<Class> classes = jarService.getClassList();
        int success=0;
        int fail=0;
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        for (Class aClass : classes) {
            try {
                String json=objectMapper.writeValueAsString(new MockData().mock(aClass));
                System.out.println(aClass + " 成功" +json);
                success++;
            } catch (Throwable e) {
                System.err.println(aClass + "失败" + e.getMessage());
                fail++;
            }
        }
        System.out.println("成功:"+success+"个  失败:"+fail+"个");
    }


    private void remoteMockTest(String url, String className) {
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(url)});
            Class clazz = urlClassLoader.loadClass(className);
            Object result = new MockData().mock(clazz);
            System.out.println(objectMapper.writeValueAsString(result));
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


}

