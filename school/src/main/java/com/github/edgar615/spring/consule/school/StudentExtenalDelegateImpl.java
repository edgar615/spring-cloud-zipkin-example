package com.github.edgar615.spring.consule.school;

import brave.ScopedSpan;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class StudentExtenalDelegateImpl implements StudentExtenalDelegate {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private Tracer tracer;

  @Override
  public String callStudentServiceAndGetData(String schoolname) {
      ScopedSpan span = tracer.startScopedSpan("local");
      span.tag("foo", "bar");
      span.finish();

      String response = restTemplate
          .exchange("http://student-service/students/{schoolname}", HttpMethod.GET, null,
              new ParameterizedTypeReference<String>() {
              }, schoolname).getBody();

      return "School Name -  " + schoolname + " :::  Student Details " + response + " -  "
          + new Date();
  }

}
