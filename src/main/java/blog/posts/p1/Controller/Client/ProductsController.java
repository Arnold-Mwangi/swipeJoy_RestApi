package blog.posts.p1.Controller.Client;

import blog.posts.p1.service.ClientProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductsController {

    private final ClientProductService clientProductService;



    @GetMapping
    public ResponseEntity<?> products () {
        return ResponseEntity.ok(clientProductService.getAllProducts());
    }
}
