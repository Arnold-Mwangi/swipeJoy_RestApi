package blog.posts.p1.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counter")
public class CounterController {

    private int counter = 0;

    @GetMapping("/counts")
    public int getCounter() {
        return counter;
    }

    @PostMapping("/increment")
    public int incrementCounter(@RequestParam int amount) {
        // Simulate some processing time (to exaggerate the race condition effect)
        try {
            Thread.sleep(100); // Simulate delay to exaggerate race condition effect
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counter += amount;
        return counter;
    }
}
