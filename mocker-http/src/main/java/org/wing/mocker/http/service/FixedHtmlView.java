package org.wing.mocker.http.service;

import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class FixedHtmlView implements View {
    
    private final String filePath;

    public FixedHtmlView(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.setContentType(getContentType());
            FileCopyUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath),
                    response.getOutputStream());
    }
}
