package org.dp.sf.controllers;

import org.dp.sf.domain.Vendor;
import org.dp.sf.repositories.VendorRepository;
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
public class VendorController {

	private final VendorRepository vendorRepository;

	public VendorController(VendorRepository vendorRepository) {
		this.vendorRepository = vendorRepository;
	}

	@GetMapping("/api/v1/vendors")
	public Flux<Vendor> getVendors() {
		return vendorRepository.findAll();
	}

	@GetMapping("/api/v1/vendors/{id}")
	public Mono<Vendor> findVendor(@PathVariable String id) {
		return vendorRepository.findById(id);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/api/v1/vendors")
	public Mono<Void> createVendor(@RequestBody Publisher<Vendor> vendorStream) {
		return vendorRepository.saveAll(vendorStream).then();
	}

	@PutMapping("/api/v1/vendors/{id}")
	public Mono<Vendor> updateVendor(@PathVariable String id, @RequestBody Vendor vendor) {
		vendor.setId(id);
		return vendorRepository.save(vendor);
	}

	@PatchMapping("/api/v1/vendors/{id}")
	public Mono<Vendor> patchVendor(@PathVariable String id, @RequestBody Vendor vendor) {
		Vendor vendorFound = vendorRepository.findById(id).block();
		if (vendorFound != null) {
			if (vendor.getFirstName() != null && !vendor.getFirstName().equalsIgnoreCase(vendorFound.getFirstName())) {
				vendorFound.setFirstName(vendor.getFirstName());
				return vendorRepository.save(vendorFound);
			} else if (vendor.getLastName() != null
					&& vendor.getLastName().equalsIgnoreCase(vendorFound.getLastName())) {
				vendorFound.setLastName(vendor.getLastName());
				return vendorRepository.save(vendorFound);
			}

		}
		return Mono.just(vendorFound);
	}

}
