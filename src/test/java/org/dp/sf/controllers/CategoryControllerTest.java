package org.dp.sf.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

import org.dp.sf.domain.Category;
import org.dp.sf.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class CategoryControllerTest {

	WebTestClient webTestClient;
	CategoryRepository categoryRepository;
	CategoryController categoryController;

	@BeforeEach
	void setUp() throws Exception {

		categoryRepository = Mockito.mock(CategoryRepository.class);
		categoryController = new CategoryController(categoryRepository);
		webTestClient = WebTestClient.bindToController(categoryController).build();
	}

	@Test
	void testGetCategoies() {

		BDDMockito.given(categoryRepository.findAll()).willReturn(
				Flux.just(Category.builder().description("1").build(), Category.builder().description("2").build()));

		webTestClient.get().uri("/api/v1/categories").exchange().expectBodyList(Category.class).hasSize(2);
	}

	@Test
	void testFindCategory() {
		BDDMockito.given(categoryRepository.findById("some_id"))
				.willReturn(Mono.just(Category.builder().description("").build()));

		webTestClient.get().uri("/api/v1/categories/some_id").exchange().expectBody(Category.class);
	}

	@Test
	public void testCreateCategory() {
		BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
				.willReturn(Flux.just(Category.builder().description("desc").build()));

		Mono<Category> categoryStream = Mono.just(Category.builder().description("kitty").build());

		webTestClient.post().uri("/api/v1/categories").body(categoryStream, Category.class).exchange().expectStatus()
				.isCreated();

	}

	@Test
	public void testUpdateCategroy() {
		BDDMockito.given(categoryRepository.save(any(Category.class)))
				.willReturn(Mono.just(Category.builder().description("data to update").build()));

		Mono<Category> categoryToSave = Mono.just(Category.builder().description("desc").build());
		webTestClient.put().uri("/api/v1/categories/some_id").body(categoryToSave, Category.class).exchange()
				.expectStatus().isOk();
	}
	
	@Test
	public void testPatchCategroyWithData() {
		BDDMockito.given(categoryRepository.findById(anyString()))
		.willReturn(Mono.just(Category.builder().description("update this").build()));
		
		BDDMockito.given(categoryRepository.save(any(Category.class)))
				.willReturn(Mono.just(Category.builder().description("data to update").build()));

		Mono<Category> categoryToSave = Mono.just(Category.builder().description("desc").build());
		
		webTestClient.patch().uri("/api/v1/categories/some_id").body(categoryToSave, Category.class).exchange()
				.expectStatus().isOk();
		
		BDDMockito.verify(categoryRepository).save(any());
	}
	
	@Test
	public void testPatchCategroyWithOutData() {
		BDDMockito.given(categoryRepository.findById(anyString()))
		.willReturn(Mono.just(Category.builder().build()));
		
		BDDMockito.given(categoryRepository.save(any(Category.class)))
				.willReturn(Mono.just(Category.builder().description("data to update").build()));

		Mono<Category> categoryToSave = Mono.just(Category.builder().build());
		
		webTestClient.patch().uri("/api/v1/categories/some_id").body(categoryToSave, Category.class).exchange()
				.expectStatus().isOk();
		
		BDDMockito.verify(categoryRepository, never()).save(any());
	}
}
