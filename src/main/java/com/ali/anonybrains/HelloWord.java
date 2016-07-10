package com.ali.anonybrains;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhanlijun.zlj on 2016/7/10.
 */

@RestController
public class HelloWord {
    @RequestMapping("/helloWorld")
    public String helloworld(){
        return "hello world";
    }

    @RequestMapping("/")
    public String phone(Model model) {
        return "phone";
    }
}
