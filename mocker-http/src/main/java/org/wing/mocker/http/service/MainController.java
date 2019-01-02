package org.wing.mocker.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wing.mocker.core.MockData;
import org.wing.mocker.http.model.PageDataRo;
import org.wing.mocker.http.model.PomLocation;
import org.wing.mocker.http.model.QccrResult;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;


@Controller
public class MainController {

    private Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final MavenRepositoryService mavenRepositoryService;

    @Autowired
    public MainController(MavenRepositoryService mavenRepositoryService) {
        this.mavenRepositoryService = mavenRepositoryService;
    }

    @RequestMapping("/{groupId}/{artifactId}/{version}/{objectClass}")
    @ResponseBody
    public Object mock(HttpServletRequest httpServletRequest,
                       @PathVariable("groupId") String groupId,
                       @PathVariable("artifactId") String artifactId,
                       @PathVariable("version") String version,
                       @PathVariable("objectClass") String objectClass,
                       String depth, String listLimit, String struct) {

        MockData mockData = new MockData();
        if (depth != null) {
            mockData.getMockSettings().setDepth(Integer.parseInt(depth));
        }
        if (listLimit != null) {
            mockData.getMockSettings().setListLimit(Integer.parseInt(listLimit));
        }

        PomLocation pomLocation = new PomLocation(groupId, artifactId, version);
        String jarUrl = mavenRepositoryService.getJarClassURL(pomLocation);
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarUrl)});
            Class mockClass = urlClassLoader.loadClass(objectClass);
            Object data;
            if (struct == null) {
                data = mockData.mock(mockClass);

            } else if (struct.equals("page")) {
                data = PageDataRo.create(warpList(mockData, mockClass));
            } else if (struct.equals("list")) {
                data = warpList(mockData, mockClass);
            } else {
                data = mockData.mock(mockClass);
            }
            return QccrResult.create(data);

        } catch (MalformedURLException | ClassNotFoundException e) {
            LOGGER.error("mock错误 {}", objectClass, e);
            return e.toString();
        }
    }

    private List<Object> warpList(MockData mockData, Class mockClass) {
        List<Object> list = new LinkedList<>();
        for (int i = 0; i < mockData.getMockSettings().getListLimit(); i++) {
            list.add(mockData.mock(mockClass));
        }
        return list;
    }

}
