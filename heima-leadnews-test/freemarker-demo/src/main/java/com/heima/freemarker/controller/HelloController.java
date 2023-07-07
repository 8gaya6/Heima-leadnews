package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Description: Todo
 * Class Name: HelloController
 * Date: 2023/7/5 21:38
 *
 * @author Hao
 * @version 1.1
 */

@Controller
public class HelloController {

    @GetMapping("/basic")
    public String hello(Model model) {
        // 1. 纯文本形式的参数
        model.addAttribute("name", "Jisoo");

        // 2. 实体类相关的参数
        Student student = new Student();
        student.setName("Jisoo");
        student.setAge(25);
        model.addAttribute("stu", student);
        return "01-basic";
    }

    @GetMapping("/list")
    public String list(Model model) {

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
        // 向 model 中存放 List 集合数据
        model.addAttribute("stuList", stuList);

        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        // 向 model 中存放 Map 集合数据
        model.addAttribute("stuMap", stuMap);

        // 向 model 中存放日期数据
        model.addAttribute("today", new Date());
        return "02-list";
    }
}
