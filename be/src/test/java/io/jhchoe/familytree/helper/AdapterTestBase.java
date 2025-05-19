package io.jhchoe.familytree.helper;

import io.jhchoe.familytree.config.TestAuditConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
public class AdapterTestBase {

}
