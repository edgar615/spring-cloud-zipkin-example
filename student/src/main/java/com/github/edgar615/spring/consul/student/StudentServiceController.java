package com.github.edgar615.spring.consul.student;

import brave.CurrentSpanCustomizer;
import brave.Tracer;
import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.github.edgar615.grpc.GreeterGrpc;
import com.github.edgar615.grpc.HelloRequest;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class StudentServiceController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceController.class);

  private static Map<String, List<Student>> schooDB = new HashMap<>();

  @Autowired
  private DataSource dataSource;

  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;

  @Autowired
  private KafkaTemplate<String, String> template;

  @Autowired
  private Tracer tracer;

  @Autowired
  private Tracing tracing;

  static {
    schooDB = new HashMap<String, List<Student>>();
    List<Student> lst = new ArrayList<Student>();
    Student std = new Student("Sajal", "Class IV");
    lst.add(std);
    std = new Student("Lokesh", "Class V");
    lst.add(std);

    schooDB.put("abcschool", lst);

    lst = new ArrayList<Student>();
    std = new Student("Kajal", "Class III");
    lst.add(std);
    std = new Student("Sukesh", "Class VI");
    lst.add(std);

    schooDB.put("xyzschool", lst);

  }

  @GetMapping(value = "/students/{schoolname}")
  public List<Student> getStudents(@PathVariable String schoolname, HttpServletRequest request) {
    LOGGER.info("receive request");
    CurrentSpanCustomizer spanCustomizer = CurrentSpanCustomizer.create(tracing);
    tracing.currentTraceContext().get().traceId();
//    template.send("myTopic", "message1");
//    grpc();
    List<Student> studentList = schooDB.get(schoolname);
      if (studentList == null) {
        studentList = new ArrayList<Student>();
        Student std = new Student("Not Found", "N/A");
        studentList.add(std);
      }
      return studentList;
  }

  private void redisSet(String schoolname) {
    redisTemplate.opsForValue().set(schoolname, schoolname, 100, TimeUnit.SECONDS);
    Map<String, Object> map = new HashMap<>();
    map.put("schoolname", schoolname);
    redisTemplate.opsForHash().putAll("h:" + schoolname, map);
  }

  private void grpc() {
    GrpcTracing grpcTracing = GrpcTracing.newBuilder(tracing)
            .grpcPropagationFormatEnabled(false)
            .build();
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9998)
            .intercept(grpcTracing.newClientInterceptor())
            .usePlaintext()
            .build();
    HelloRequest helloRequest = HelloRequest.newBuilder().setName("edgar").build();
    GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(managedChannel);
    stub.sayHello(helloRequest);
  }

  private void insert() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    User user = new User();
    user.setUserId(0L);
    user.setNickname("edgar");
    user.setUsername("edgar");
    user.setMail("edgar" + "@github.com");
    user.setMobile("18000000000");
    SQLBindings sqlBindings = SqlBuilder.insert(user);
    jdbcTemplate.update(sqlBindings.sql(), sqlBindings.bindings().toArray());
  }

  private void qeuery() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    SQLBindings sqlBindings = SqlBuilder.findById(User.class, 11L);
    List<User> users = jdbcTemplate.query(sqlBindings.sql(), sqlBindings.bindings().toArray(), BeanPropertyRowMapper.newInstance(User.class));
    System.out.println(users.size());
  }

  private void delete() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    SQLBindings sqlBindings = SqlBuilder.deleteById(User.class, 0L);
    jdbcTemplate.update(sqlBindings.sql(), sqlBindings.bindings().toArray());
  }
}
