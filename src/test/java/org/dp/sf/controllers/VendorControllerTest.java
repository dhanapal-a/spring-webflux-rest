package org.dp.sf.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

import org.dp.sf.domain.Vendor;
import org.dp.sf.repositories.CategoryRepository;
import org.dp.sf.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class VendorControllerTest {

	WebTestClient webTestClient;

	VendorRepository vendorRepository;

	VendorController vendorController;

	@BeforeEach
	void setUp() throws Exception {
		vendorRepository = Mockito.mock(VendorRepository.class);
		vendorController = new VendorController(vendorRepository);
		webTestClient = WebTestClient.bindToController(vendorController).build();
	}

	@Test
	void testGetVendors() {
		BDDMockito.given(vendorRepository.findAll()).willReturn(
				Flux.just(Vendor.builder().firstName("1").build(), Vendor.builder().firstName("2").build()));

		webTestClient.get().uri("/api/v1/vendors").exchange().expectBodyList(Vendor.class).hasSize(2);
	}

	@Test
	void testFindVendor() {
		BDDMockito.given(vendorRepository.findById("some_id"))
				.willReturn(Mono.just(Vendor.builder().firstName("2").build()));

		webTestClient.get().uri("/api/v1/vendors/some_id").exchange().expectBody(Vendor.class);
	}
	
	@Test
	void testCreateVendor() {
		BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
		.willReturn(Flux.just(Vendor.builder().firstName("Name1").build()));
		
		Mono<Vendor> vendorStream = Mono.just(Vendor.builder().firstName("first1").build());
		webTestClient.post().uri("/api/v1/vendors")
		.body(vendorStream , Vendor.class)
		.exchange()
		.expectStatus()
		.isCreated();
		
	}
	
	@Test
	void testUpdateVendor() {
		BDDMockito.given(vendorRepository.save(any(Vendor.class)))
		.willReturn(Mono.just(Vendor.builder().firstName("first").build()));
		
		Mono<Vendor> vendorStream = Mono.just(Vendor.builder().firstName("first1").build());
		webTestClient.put().uri("/api/v1/vendors/some_id")
		.body(vendorStream , Vendor.class)
		.exchange()
		.expectStatus()
		.isOk();
		
	}
	
	@Test
	void testUpdateVendorwithData() {
		BDDMockito.given(vendorRepository.findById(anyString()))
		.willReturn(Mono.just(Vendor.builder().build()));
		
		BDDMockito.given(vendorRepository.save(any(Vendor.class)))
		.willReturn(Mono.just(Vendor.builder().firstName("first").build()));
		
		Mono<Vendor> vendorStream = Mono.just(Vendor.builder().firstName("first1").build());
		webTestClient.patch().uri("/api/v1/vendors/some_id")
		.body(vendorStream , Vendor.class)
		.exchange()
		.expectStatus()
		.isOk();
		
		BDDMockito.verify(vendorRepository).save(any());
	}

	
	@Test
	void testUpdateVendorWithOutData() {
		BDDMockito.given(vendorRepository.findById(anyString()))
		.willReturn(Mono.just(Vendor.builder().firstName("first").build()));
		
		BDDMockito.given(vendorRepository.save(any(Vendor.class)))
		.willReturn(Mono.just(Vendor.builder().firstName("first").build()));
		
		Mono<Vendor> vendorStream = Mono.just(Vendor.builder().build());
		
		webTestClient.patch().uri("/api/v1/vendors/some_id")
		.body(vendorStream , Vendor.class)
		.exchange()
		.expectStatus()
		.isOk();
		
		BDDMockito.verify(vendorRepository,never()).save(any());
	}
}
