package com.edoc.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.edoc.models.Group;
import com.edoc.repository.GroupRepository;

/** Ensures the groups required by public registration exist in a fresh database. */
@Component
public class RequiredGroupsInitializer implements ApplicationRunner {
    private final GroupRepository groupRepository;

    public RequiredGroupsInitializer(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        groupRepository.findByName("candidat").orElseGet(() -> {
            Group group = new Group();
            group.setName("candidat");
            return groupRepository.save(group);
        });
    }
}
