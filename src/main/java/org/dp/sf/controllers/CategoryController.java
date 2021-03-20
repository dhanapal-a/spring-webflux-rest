package org.dp.sf.controllers;

import org.dp.sf.domain.Category;
import org.dp.sf.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CategoryController {

	private final CategoryRepository categoryRepository;

	public CategoryController(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@GetMapping("/api/v1/categories")
	public Flux<Category> getCategoies() {
		return categoryRepository.findAll();
	}

	@GetMapping("/api/v1/categories/{id}")
	public Mono<Category> findCategory(@PathVariable String id) {
		return categoryRepository.findById(id);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/api/v1/categories")
	public Mono<Void> createCategory(@RequestBody Publisher<Category> categoryStream) {
		return categoryRepository.saveAll(categoryStream).then();
	}

	@PutMapping("/api/v1/categories/{id}")
	public Mono<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
		category.setId(id);
		return categoryRepository.save(category);
	}

	
	@PatchMapping("/api/v1/categories/{id}")
	public Mono<Category> patchCategory(@PathVariable String id, @RequestBody Category category) {
		Category categoryFound = categoryRepository.findById(id).block();
		if(categoryFound != null && category.getDescription() != null) {
			categoryFound.setDescription(category.getDescription());
			categoryRepository.save(categoryFound);
		}
		return Mono.just(categoryFound);
	}
}
