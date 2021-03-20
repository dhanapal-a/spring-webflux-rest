package org.dp.sf.repositories;

import org.dp.sf.domain.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository  extends ReactiveMongoRepository<Category, String>{

}
