package com.treatmentunit.restservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello %s !";
    private static final AtomicLong counter = new AtomicLong();

    @GetMapping("/wesh")
    public String greetin(@RequestParam(value = "id") int id) {
        return "On the rockzz " + id;
    }

    /*
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting2")
    public Greeting greeting2(@RequestParam(value = "name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    */
}