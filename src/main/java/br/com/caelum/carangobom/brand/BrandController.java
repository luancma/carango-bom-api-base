package br.com.caelum.carangobom.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/brands")
public class BrandController {
	
	@Autowired
    private BrandRepository brandRepository;

    @Autowired
    public BrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }
    
    @GetMapping("/paged")
    public Page<Brand> findAllPaged(
			@PageableDefault(sort = "name", direction = Direction.ASC, page = 0, size = 10)
			Pageable pageable
		) {
	    return brandRepository.findAll(pageable);
    }

    @GetMapping
    public ResponseEntity<List<Brand>> findAll() {
        List<Brand> brands = brandRepository.findAll();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> findById(@PathVariable Long id) {
        Optional<Brand> m1 = brandRepository.findById(id);
        if (m1.isPresent()) {
            return ResponseEntity.ok(m1.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Brand> save(@Valid @RequestBody Brand m1, UriComponentsBuilder uriBuilder) {
        Brand m2 = brandRepository.save(m1);
        URI h = uriBuilder.path("/brands/{id}").buildAndExpand(m1.getId()).toUri();
        return ResponseEntity.created(h).body(m2);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Brand> update(@PathVariable Long id, @Valid @RequestBody Brand m1) {
        Optional<Brand> m2 = brandRepository.findById(id);
        if (m2.isPresent()) {
            Brand m3 = m2.get();
            m3.setName(m1.getName());
            return ResponseEntity.ok(m3);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Brand> delete(@PathVariable Long id) {
        Optional<Brand> m1 = brandRepository.findById(id);
        if (m1.isPresent()) {
            Brand m2 = m1.get();
            brandRepository.delete(m2);
            return ResponseEntity.ok(m2);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}