package com.heima.freemarker.test;


import com.heima.freemarker.FreemarkerDemoApplication;
import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Description: Todo
 * Class Name: FreemarkerTest
 * Date: 2023/7/7 13:47
 *
 * @author Hao
 * @version 1.1
 */

@SpringBootTest(classes = FreemarkerDemoApplication.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {
    @Autowired
    private Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        // freemarker 的模板对象，获取模板
        Template template = configuration.getTemplate("02-list.ftl");
        Map params = getData();

        /**
         * 合成方法
         * 参数：数据模型
         * 参数：输出流
         */
        template.process(params, new FileWriter("d:/list.html"));
    }


    private Map getData() {
        Map<String, Object> map = new HashMap<>();

        Student stu1 = new Student();
        stu1.setName("Jisoo");
        stu1.setAge(25);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        Student stu2 = new Student();
        stu2.setName("Jennie");
        stu2.setMoney(200.1f);
        stu2.setAge(24);

        List<Student> stuList = new ArrayList<>();
        stuList.add(stu1);
        stuList.add(stu2);
        // 存放 List 集合数据
        map.put("stuList", stuList);

        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        // 存放 Map 集合数据
        map.put("stuMap", stuMap);

        return map;
    }
}