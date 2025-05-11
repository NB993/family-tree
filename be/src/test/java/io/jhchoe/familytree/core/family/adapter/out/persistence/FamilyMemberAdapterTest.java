package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.config.TestAuditConfig;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("[Unit Test] FamilyMemberAdapterTest")
public class FamilyMemberAdapterTest {

}
