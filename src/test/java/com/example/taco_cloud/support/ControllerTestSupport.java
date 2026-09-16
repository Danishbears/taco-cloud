package com.example.taco_cloud.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public final class ControllerTestSupport {

    private ControllerTestSupport() {
    }

    public static MockMvc standaloneSetup(Object... controllers) {
        return MockMvcBuilders.standaloneSetup(controllers)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .setViewResolvers(nonForwardingViewResolver())
                .build();
    }

    private static ViewResolver nonForwardingViewResolver() {
        return (viewName, locale) -> {
            if (viewName.startsWith("redirect:")) {
                return new RedirectView(viewName.substring("redirect:".length()));
            }
            return new View() {
                @Override
                public void render(Map<String, ?> model,
                                   jakarta.servlet.http.HttpServletRequest request,
                                   jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
                    response.setContentType("text/html");
                    response.getWriter().write(viewName);
                }

                @Override
                public String getContentType() {
                    return "text/html";
                }
            };
        };
    }
}
