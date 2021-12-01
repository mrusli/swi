package com.pyramix.swi.domain;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPeti;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiEndProduct;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiMaterial;
import com.pyramix.swi.domain.inventory.bukapeti.InventoryBukaPetiProduct;
import com.pyramix.swi.domain.inventory.process.InventoryProcess;
import com.pyramix.swi.domain.inventory.process.InventoryProcessMaterial;
import com.pyramix.swi.domain.inventory.process.InventoryProcessProduct;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransfer;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferEndProduct;
import com.pyramix.swi.domain.inventory.transfer.InventoryTransferMaterial;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;

public class App_Process {

	public static void main(String[] args)
	{
		System.out.println( "Hello World!" );

		Configuration configObj = new Configuration();
		configObj.configure("hibernate.cfg.xml");
		
		ServiceRegistry serviceRegistryObj = 
				new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();
		
		@SuppressWarnings("unused")
		Metadata metadata = new MetadataSources(serviceRegistryObj)
				.addAnnotatedClass(InventoryProcess.class)
				.addAnnotatedClass(DocumentSerialNumber.class)
				.addAnnotatedClass(Company.class)
				.addAnnotatedClass(InventoryProcessMaterial.class)
				.addAnnotatedClass(InventoryCode.class)
				.addAnnotatedClass(InventoryType.class)
				.addAnnotatedClass(InventoryProcessProduct.class)
				.addAnnotatedClass(Customer.class)
				.addAnnotatedClass(CustomerReceivable.class)
				.addAnnotatedClass(CustomerReceivableActivity.class)
				.addAnnotatedClass(Inventory.class)
				.addAnnotatedClass(InventoryBukaPeti.class)
				.addAnnotatedClass(InventoryTransfer.class)
				.addAnnotatedClass(InventoryTransferMaterial.class)
				.addAnnotatedClass(InventoryTransferEndProduct.class)
				.addAnnotatedClass(InventoryBukaPetiMaterial.class)
				.addAnnotatedClass(InventoryBukaPetiProduct.class)
				.addAnnotatedClass(InventoryBukaPetiEndProduct.class)
				.addResource("hibernate.cfg.xml")
				.getMetadataBuilder()
				.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
				.build();				
		
		// SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();	
		
		// Session session = sessionFactory.openSession();
		
		// InventoryProcess process = session.get(InventoryProcess.class, new Long(1));
		
		// System.out.println(process);
	}
}
