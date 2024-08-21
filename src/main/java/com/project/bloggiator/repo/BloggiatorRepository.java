package com.project.bloggiator.repo;

import com.project.bloggiator.entity.BlogEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BloggiatorRepository extends MongoRepository<BlogEntity, ObjectId> {}
