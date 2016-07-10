package com.ali.anonybrains;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * @author ֪zhanlijun.zlj
 * @date 2016-07-10
 */
@RestController
@SpringBootApplication
public class SpringbootAnonyBrainsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootAnonyBrainsApplication.class, args);
    }
    
	@RequestMapping("/hello")
	public String helloworld(){
		return "hello world";
	}
}
