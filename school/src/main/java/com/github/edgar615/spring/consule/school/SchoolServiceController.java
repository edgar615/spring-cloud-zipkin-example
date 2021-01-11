package com.github.edgar615.spring.consule.school;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class SchoolServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchoolServiceController.class);

    @Autowired
    StudentExtenalDelegate studentExtenalDelegate;

    @GetMapping("/students/{schoolname}")
    public String getStudents(@PathVariable String schoolname, HttpServletRequest request)
    {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            System.out.println(String.format("%s:%s", name, request.getHeader(name)));
        }
        LOGGER.info("Going to call student service to get data!");
        return studentExtenalDelegate.callStudentServiceAndGetData(schoolname);
    }

}
