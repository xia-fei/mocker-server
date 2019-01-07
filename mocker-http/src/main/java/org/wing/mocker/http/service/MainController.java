package org.wing.mocker.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wing.mocker.core.MockData;
import org.wing.mocker.http.model.PageDataRo;
import org.wing.mocker.http.model.PomLocation;
import org.wing.mocker.http.model.QccrResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Controller
public class MainController {

    private final Environment environment;
    private Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final MavenRepositoryService mavenRepositoryService;

    @Autowired
    public MainController(MavenRepositoryService mavenRepositoryService, Environment environment) {
        this.mavenRepositoryService = mavenRepositoryService;
        this.environment = environment;
    }

    @RequestMapping("/{groupId}/{artifactId}/{version}/{objectClass:.+}")
    @ResponseBody
    public Object mock(HttpServletRequest httpServletRequest,
                       @PathVariable("groupId") String groupId,
                       @PathVariable("artifactId") String artifactId,
                       @PathVariable("version") String version,
                       @PathVariable("objectClass") String objectClass,
                       String depth, String listSize, String struct) {
        MockData mockData = builderMockData(depth, listSize);

        PomLocation pomLocation = new PomLocation(groupId, artifactId, version);
        String jarUrl = mavenRepositoryService.getJarClassURL(pomLocation);
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarUrl)}, Thread.currentThread().getContextClassLoader());
            Class mockClass = urlClassLoader.loadClass(objectClass);

            return QccrResult.create(mockStructData(mockData, mockClass, struct));

        } catch (MalformedURLException | ClassNotFoundException e) {
            LOGGER.error("mock错误 {}", objectClass, e);
            return e.toString();
        }
    }


    private Object mockStructData(MockData mockData, Class mockClass, String struct) {
        if (struct != null) {
            if (struct.equals("page")) {
                return PageDataRo.create(warpList(mockData, mockClass));
            } else if (struct.equals("list")) {
                return warpList(mockData, mockClass);
            }
        }
        return mockData.mock(mockClass);
    }

    private MockData builderMockData(String depth, String listSize) {
        MockData mockData = new MockData();
        if (depth != null) {
            mockData.getMockSettings().setDepth(Integer.parseInt(depth));
        }
        if (listSize != null) {
            mockData.getMockSettings().setListSize(Integer.parseInt(listSize));
        }
        if (environment.getProperty("mock.string") != null) {
            mockData.getMockSettings().setRandomStringSource(environment.getProperty("mock.string"));
        }
        return mockData;
    }

    private List<Object> warpList(MockData mockData, Class mockClass) {
        List<Object> list = new LinkedList<>();
        for (int i = 0; i < mockData.getMockSettings().getListSize(); i++) {
            list.add(mockData.mock(mockClass));
        }
        return list;
    }


    @GetMapping("/getJarClass")
    @ResponseBody
    public Object getJarClass(String g, String a, String v) throws IOException {
        Map<String, Object> map = new HashMap<>();
        PomLocation pomLocation = new PomLocation(g, a, v);
        String jarUrl = mavenRepositoryService.getJarClassURL(pomLocation);
        JarService jarService = new JarService(jarUrl);
        List<Class> classList = jarService.getClassList();
        List<String> classNameList = new LinkedList<>();
        for (Class className : classList) {
            classNameList.add(className.getName());
        }
        map.put("jarUrl", jarUrl);
        map.put("classList", classNameList);
        return map;
    }


}
