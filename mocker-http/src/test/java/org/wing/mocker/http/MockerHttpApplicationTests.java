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
    ObjectMapper objectMapper;

    @Autowired
    private MavenRepositoryService mavenRepositoryService;


    private String shprodJar = "http://192.168.0.107:8081/nexus/service/local/repositories/snapshots/content/com/qccr/shprod/shprod-facade/3.9.9.9-SNAPSHOT/shprod-facade-3.9.9.9-20181215.195710-450.jar";
    private String goodscenter = "http://192.168.0.107:8081/nexus/content/groups/public/com/qccr/goodscenter/goodscenter-facade/4.1.3.18/goodscenter-facade-4.1.3.18.jar";



    @Test
    public void testTotalResult() throws IOException, ClassNotFoundException {
        singleClassTest(shprodJar, "com.qccr.shprod.facade.entity.role.TotalResultRo");
    }

    @Test
    public void ByteError() {
        singleClassTest(shprodJar, "com.qccr.shprod.facade.entity.construction.RoyaltyConstructionRO");
    }

    @Test
    public void testClass3() {
        singleClassTest(goodscenter, "com.qccr.goodscenter.facade.ro.query.category.CategoryGradeQueryOption");

    }

    @Test
    public void testClass2() {

        singleClassTest(goodscenter, "com.qccr.goodscenter.facade.ro.item.ItemAreaPriceRO");

    }

    @Test
    public void testPomClass() throws ClassNotFoundException, JsonProcessingException, MalformedURLException {
        PomLocation pomLocation = new PomLocation("org.xiafei", "maven-view", "1.0-SNAPSHOT");
        String jarUrl = mavenRepositoryService.getJarClassURL(pomLocation);
        singleClassTest(jarUrl, "com.nkz.test.ro.StoreRO");
    }



    @Test
    public void testRemoteClassMock2() throws MalformedURLException, ClassNotFoundException, JsonProcessingException {
        singleClassTest(shprodJar, "com.qccr.shprod.facade.entity.drawmoney.CanDrawAndRewardRO");
    }


    @Test
    public void testMerchantCenter() throws IOException, ClassNotFoundException {
        PomLocation pomLocation = new PomLocation("com.qccr.merchantcenter", "merchantcenter-facade", "6920-test-SNAPSHOT");
        allClassTest(mavenRepositoryService.getJarClassURL(pomLocation));
    }

    @Test
    public void testGoodscenter() throws IOException, ClassNotFoundException {
        allClassTest(goodscenter);
    }

    @Test
    public void testShprod() throws IOException, ClassNotFoundException {
        allClassTest(shprodJar);
    }


    public void allClassTest(String jarUrl) throws IOException, ClassNotFoundException {
        System.out.println("start");
        JarService jarService = new JarService(jarUrl);
        List<Class> classes = jarService.getClassList();
        int success = 0;
        int fail = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        for (Class aClass : classes) {
            try {
                String json = objectMapper.writeValueAsString(new MockData().mock(aClass));
                System.out.println(aClass + " 成功" + json);
                success++;
            } catch (Throwable e) {
                System.err.println(aClass + "失败" + e.toString());
                fail++;
            }
        }
        System.out.println("成功:" + success + "个  失败:" + fail + "个");
    }


    private void singleClassTest(String url, String className) {
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

