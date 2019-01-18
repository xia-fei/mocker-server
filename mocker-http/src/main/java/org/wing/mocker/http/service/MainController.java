package org.wing.mocker.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.wing.mocker.core.MockData;
import org.wing.mocker.http.model.PageDataRo;
import org.wing.mocker.http.model.PomLocation;
import org.wing.mocker.http.model.QccrResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Controller
public class MainController implements DisposableBean, InitializingBean {

    private final Environment environment;
    private Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final MavenRepositoryService mavenRepositoryService;
    private ApiDataService apiDataService;
    private final String filePath = "/data/file";

    @Autowired
    public MainController(MavenRepositoryService mavenRepositoryService, Environment environment, ApiDataService apiDataService) {
        this.mavenRepositoryService = mavenRepositoryService;
        this.environment = environment;
        this.apiDataService = apiDataService;
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

    @PostMapping("/saveData")
    @ResponseBody
    public Object saveData(@RequestBody String data) {
        return Collections.singletonMap("id", apiDataService.save(data));
    }

    @GetMapping("/data/{id}")
    @ResponseBody
    public String getData(@PathVariable("id") Long id) {
        return apiDataService.get(id);
    }

    @RequestMapping({"/", "*.html"})
    public ModelAndView vue(HttpServletRequest request) {
        return new ModelAndView(new FixedHtmlView("public/dist/index.html"));
    }

    @RequestMapping("/quick-start.html")
    public ModelAndView quickStart() {
        return new ModelAndView(new FixedHtmlView("static/quick-start.html"));
    }



    @Override
    public void destroy() throws Exception {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path);
            byte[] data = SerializationUtils.serialize(apiDataService);
            StreamUtils.copy(data, Files.newOutputStream(path.resolve("1.db")));
            LOGGER.info("序列化文件保存成功");
        } catch (Exception e) {
            LOGGER.error("序列化文件保存失败", e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            Path path = Paths.get(filePath).resolve("1.db");
            if (Files.exists(path)) {
                apiDataService = (ApiDataService) SerializationUtils.deserialize(StreamUtils.copyToByteArray(Files.newInputStream(path)));
                LOGGER.info("序列化文件读取成功");
            }
        } catch (Exception e) {
            LOGGER.error("序列化文件读取失败");
        }

    }


}
