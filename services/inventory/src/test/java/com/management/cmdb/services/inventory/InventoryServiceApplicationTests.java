package com.management.cmdb.services.inventory;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class InventoryServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@Disabled
	void verifiesModularStructure() {
		ApplicationModules modules = ApplicationModules.of(InventoryServiceApplication.class);
		modules.verify();
	}

	@Test
	void createModuleDocumentation() {
		ApplicationModules modules = ApplicationModules.of(InventoryServiceApplication.class);
		new Documenter(modules)
				.writeDocumentation()
				.writeIndividualModulesAsPlantUml();
	}

}
