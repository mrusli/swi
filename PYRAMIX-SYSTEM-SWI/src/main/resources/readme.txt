3.8.0 - 28/12/2021
- Upgrade 'Batal' for the Settlement
- Change 'Batal' indicator in VoucherGiroReceipt, Giro, VoucherPayment, Settlement, CustomerReceivable

3.7.20-SWI - 27/11/2021
- Add edit capability for Giro in 'Voucher: ... Daftar Giro Gantung' (Informasi Giro)

3.7.19B-SWI - 24/10/2021
- Add dates selection in VoucherGiroReceipt
- Modify InventoryType and InventoryCode updates

3.7.19A-SWI - 21/10/2021
- Check all Piutang data, making sure they are accurate
- Reverse the order of the piutang, piutang history (penjualan langganan), laporan penjualan
- Add filter: customer and date, in CustomerOrderListInfo

3.7.18-SWI - 12/10/2021
- Add sorting order for the Daily Sales Report, Piutang Langganan, Laporan Penjualan Langganan


3.7.17-SWI - 14/09/2021
- Set session-timeout in web.xml to -1 indicating session never expires thereby no timeout
- Add remember-me to store login credentials in cookie, thereby auto-login user into the app

3.7.16-SWI - 08/09/2021
- Add details CustomerOrderProduct in CustomerReceivable
	- Use listbox with master/detail concept

3.7.15-SWI - 31/08/2021
- Bug Fixed JournalVoucher
	- During save JournalVoucher, GL not created properly, COA not saved into the database table
- Some of the JournalVoucher transactions have been fixed manually
- Inventory - Qty width widen to accomodate qty to thousands
- InventoryProcess - Penyelesaian - Thickness box in the details not disabled (user freely change the thickness)
- VoucherGiroReceipt - Added the 'Tgl-Jth.Tempo'

3.7.14-SWI - 25/08/2021
- Bug Fixed Settlement - VoucherPayment 
	- During 'Check' - journal amount may NOT be the same as the voucher amount
	- Index#2 in the DbCr is always adjustment
- Bug Fixed Edit VoucherJournal
	- Seperate routine for the 'createDbCr' and 'updateDbCr'
3.7.13-SWI - 23/08/2021
- InventoryProcess:
	- Allow user to change the status of the process
	- Hide 'Penyelesaian' when the status is 'Selesai'
- Settlement - VoucherPayment:
	- Allow user to select COA
	- Allow user to update Journal description
	- Allow user to change debit/credit values
	- Allow user to add / remove rows
	- Provide a 'Check' button to make sure everything is in order before saving
- During Settlement, if the payment is less than the receivable amount (with remaining amount), after the journal is completed:
	- Create a receivable entry in the AccountReceivableActivity to indicate a payment of the remaining amount
3.7.12-SWI - 09/08/2021
- Use CustomerReceivableActivityDao to update the status
- Add dialog to manually update the status to BATAL if not BATAL yet
- Add gl entry to offset the 'Piutang Langganan' account
- Test SUCCESS to BATAL with 'Tunai' payment
- Check all gl entry for BATAL

3.7.11-SWI - 30/07/2021
- log4j.properties
	1. increase the log file size to 1MB
	2. increase the rotation file to 5 (MaxBackupIndex)
- change the GeneralLedger posting date to transaction date in:
	- VoucherJournalDialogControl
	- VoucherPaymentDialogControl
	- VoucherSalesDialogControl
	- VoucherGiroReceiptDialogControl
- use each attribue's dao to update the dcoumentType to 'BATAL' in the CustomerOrderDialogBatalControl
	- EmployeeCommissionsDao
	- SuratJalanDao
	- VoucherSalesDao
	- CustomerReceivableDao
	- getCustomerOrderDao
3.7.10-swi - 24/07/2021
- use log4j.properties for Production
	1. adding new properties in log4j.properties
	2. all production logs are available in /swilogs/log4j.log
3.7.9-swi - 19/07/2021
- Laporan Penjualan Langganan
	1. Allows viewing of historical sales transactions
	2. For each transaction, user can view the CustomerOrder, SuratJalan, D/O, Faktur
- CustomerOrder listinfo
	1. CustomerOrder batal, disable all posting and printing buttons
3.7.8-swi - 01/07/2021
- InventoryListInfoControl
	1. Added filter: Status, Kode, Ketebalan, Lokasi
	2. All packing will have Status, Kode, Ketebalan and Lokasi filter
	3. Default to 'Semua' when the Window is created
	4. All packing type inventory list default to status 'Semua'
	5. When user select the packing type, a query is sent to find distinct kode (i.e., kode is different for every packing type)
	6. When user select the kode, a query is sent to find distinct ketebalan (i.e., ketebalan is different for every kode) 
	NOTE: using alias in the hibernate raises exception using the production database.  InventoryCode alias for orderby temporarily commented.
3.7.7-SWI - 28/06/2021
- CustomerReceivableListInfoControl
	1. Call findCustomerReceivableWithNonZeroTotalReceivable() to display non-zero TotalReceivable
	2. Call sumAmountSalesReceivableActivities(customerReceivable) to use hibernate for the sum instead of looping the ReceivableActivities
	3. Call sumAmountPaymentReceivableActivities(customerReceivable) to use hibernate for the sum instead of looping the ReceivableActivities
	4. Total Piutang stays with a fixed number, regardless of user navigation in the Listbox
3.7.6-SWI - 24/06/2021
- ReportSalesInfoControl
	1. Call sumTotalOrderPaymentTypeTunai(startDate, endDate) and sumTotalOrderPaymentTypeAll(startDate, endDate) in
	   the CustomerOrderHibernate, instead of accumulating the TotalOrder during ListitemRenderer.
	2. Added sales person dropdown list to list sales person CustomerOrder
- CustomerOrder table
	1. Add a new 'employee_sales_id_fk' to store the sales employee id
	2. Add a new index for the 'employee_sales_id_fk'
	3. Add a new constraint to reference the 'employee' table
	4. Update CustomerOrder table using references from EmployeeCommissions table
	(see customer-order-joing-employee-commissions-02.csv)
- CustomerOrder.java
	1. Add a new @OneOnOne attribute
	2. Make it a 'Lazy' fetch
- CustomerOrderDialogControl.java
	1. Modify existing CustomerOrder record (during edit) EmployeeSales is displayed - no longer using EmployeeCommission
	2. Modify during save and edit (see onClick save event)
- CustomerOrderListInfo.java
	1. Modify listitemrenderer using EmployeeSales, instead of EmployeeCommission 

3.7.5-SWI - 16/06/2021
- Editing CustomerOrderProduct.  User allow to:
	1. Add new CustomerOrderProduct and Save as usual.
	2. Edit existing non-posting CustomerOrder and add new CustomerOrderProduct.
	3. Editing existing CustomerOrderProduct not allowed.
	4. When user save the CustomerOrder and CustomerOrderProduct, the system
	   updates the Inventory, check whether D/O is required, update the Receivable
	   (in case customer changes)

3.7.4-SWI - 10/06/2021
- Print Laporan Penjualan. Canceled (Batal) CustomerOrder printed with:
	* prefix 'BATAL' in the company name.
	* price and subtotal set to 0 (zero)
	* ppn set to 0 (zero)
	* since the subtotal is set to 0 (zero), Canceled (Batal) CustomerOrder not accumulated
- Cancel (Batal) Settlement prior to VoucherPayment / GiroVoucherPayment posting is fixed:
	* Exception resolved in the proxy
	* CustomerOrder marked with 'Lunas' reversed to not paid
	* CustomerReceivableActivity marked with payment amount reversed to not paid

3.7.3-SWI - 07/06/2021
- Print SuratJalan option: Qty(Sht)+Qty(Kg); Qty(Sht) ONLY; Qty(Kg) ONLY
- Print SuratJalan with note (containing customer's PurchaseOrder number) from CustomerOrder
- Print Faktur with note (containing customer's PurchseOrder number) from CustomerOrder

3.7.2-SWI - 29/05/2021
- Fix failure in Batal CustomerOrder.  Each entity must be updated via CustomerOrderDao seperately.  User must check inventory return manually.
	* Must look into Batal with more efficient update.
- Fix multiple ReceivableActivity.  This arise from multiple GL and SalesVoucher.  Not sure of the cause of error.  Temporarily reset the amount to 0 (zero).

3.7.0-SWI - 17/05/2021
- Update database production - auth_user, settings, employee
- Fix error in the Ending Balance - works in the local database, but not in production DB - solution: comment out the 'order by ...' in the EndingBalance.java
- Fix column alignment
- Update database production - deliveryorder, faktur

3.5.5 - 11/03/2019
- Fixed:
	- Inventory search (for specific packing type) - the problem is with the sorting, but the fix include the search as well:
		- remove non-relevant tag in Inventory.java and InventoryCode.java
		- use onFields (instead of onField) for the search fields
		- added SortField.FIELD_SCORE in the sort
		- added sort for the inventoryCode.productCode (use field names instead of sort name)
	- Inventory search (for status) - changing of status references the search box

3.5.4 - 24/02/2019
- Added period (Semua, Hari-Ini, Minggu-Ini, Bulan-Ini) for InventoryTransfer, BukaPeti, Process
- Added 'Transfer' status in InventoryStatus.  When the transfer is complete, the InventoryStatus changes to 'ready'.
- Inventory with Status 'Transfer' will not come up again when user add new InventoryTransfer
- Added checking for null or empty values before savings.  Preventing errors in the ListInfo. 	

3.5.3 - 08/02/2019
- Bug:
	- Login user with more than one UserRole causes VoucherJournalDebitCredit to display twice. 
- Fixed:
	- userCreate attribute fetch changes to LAZY
	- uses proxy to access the userCreate attribute as required
- Added user with ROLE_MANAGER uncheck the checkbox cancelling the user edit for a VoucherJournal	

3.5.2 - 04/02/2019
- Added the edit function for VoucherJournal, allowing user (the creator) to make changes even though the voucher had been posted
	- Rule: 
		1. a user with ROLE_MANAGER (id=1) must give permission to the userCreate (the creator) by clicking on the check next to the username
		2. the userCreate (the creator) 'view' button changes to 'edit'
		3. the userCreate make changes to the VoucherJournal and save.  Once saved, the VoucherJournal cannot be edited anymore.
		4. the user with ROLE_MANAGER will see that the VoucherJournal had been updated.
- Added columns for userCreate and allowEdit
- Added function to check for whether the login user has ROLE_MANAGER
- Added function to get the User object using UserDao.  Requires the userName from the Authentication object.
- Make changes to User class:
	- orphanRemoval must be set to 'false', if not, once the role is removed from the user, the role will be deleted in the database table
- Connect to the mysql server in 192.168.100.213, c3p0 complains deadlock
	- remove the dead connection from the Client Connections in the Management page of the MySQL
	
3.5.1 - 22/01/2019
- Fixed VoucherJournal listinfo link in the main menu.

3.5.0 - 21/01/2019
- Fixed paymentDate sorting in CustomerReceivableActivity causes null pointer exception, because not all receivables are paid,
  change the sorting field to paymentDueDate 
- Added JournalVoucher for General Journal and Petty Cash voucher
- Added General Ledger and Print Journal Activities
- Added auto-posting to create General Ledger after savings of:
	- VoucherSales
	- VoucherGiroReceipt (from Settlement and VoucherGiroReceipt listinfo)
	- VoucherPayment
	- VoucherJournal
- Fixed listing of Coa dialog for 'active' coa only

3.4.1 - 15/01/2019
- Fixed VoucherGiroReceiptListInfo and GiroListInfo error / exception after adding new VoucherGiroReceipt (without CustomerOrder)

3.4.0 - 02/01/2019
- Added 'BATAL' for CustomerOrder and Settlement
- Added DocumentStatus indicating the document (row) in the table is either 'NORMAL' or 'BATAL':
	CustomerOrder
	EmployeeCommissions
	SuratJalan
	DeliveryOrder
	Faktur
	VoucherSales
	ReceivableActivity
	Settlement
	VoucherGiroReceipt
	Giro
	VoucherPayment
- For each document (row) that is 'BATAL', there will be a red background
	- 'BATAL' document can only be viewed
- Added dialog for Settlement
- Fixed exception "A different object with the same identifier value was already associated with the session..."
	- CustomerOrder - VoucherSales
- Added User

3.3.2 - 09/12/2018
- Added Inventory Transfer
- Fixed issues with status
	- remove indicator for process
	- remove indicator for bukapeti
	- consolidate everything into status
	- after process is created, inventory status changes to 'process' (1)
	- after bukapeti is created, inventory status changes to 'bukapeti' (4)
	- added combobox for inventory status
	- [ONLY IN 'SEMUA'] the status column in inventory indicates process / bukapeti as follows:
		- process / bukapeti [M] - permohonan
		- process / bukapeti [P] - processing	
		- process / bukapeti [S] - selesai
	- the status column in 'Coil','Petian','Lembaran' should indicate status of 'ready':
		- if the product is transfered (thru' Inventory Transfer), the 'ready' status indicates:
			- ready [TM] - transfer permohonan
			- ready [TP] - transfer process
- Added no.coil (marking) as one of the sorted column
- Added this text file in the /resources
- Added no time out: http://forum.zkoss.org/question/77332/about-session-timeout/
- Added kendaraan and kendaraangolongan
- Added association from Employee to Kendaraan	
- Added 'Supir' and 'Kendaraan' before Transfer order is printed

3.3.1 - 21/11/2018
- Added Inventory Buka Peti
- Fixed issues with Inventory Searching
- Fixed issues with sort order
- Fixed issues with inventory package selection

3.3.0 - 11/10/2018
- Added Inventory Process
- Added Inventory Code edit
- Fix error during save / update in Inventory Type listinfo

14/03/2018:
- Leak connections solved - proxy voucher and proxy customer did not close the session.
- Leak connections also found in indexing for inventory, customer and inventory code - indexing is commented out
- Pyramix Logo Image is displayed
- Buttons are lined up in the CustomerOrder
- CustomerOrder Edit ONLY ALLOWED when neither Voucher nor SuratJalan is created (i.e., only when Voucher and SuratJalan are null, user can edit the CustomerOrder)

3.2.5 - 15/04/2018:
- Ready to replace IPFS
1. Experimental data delated
2. Check all receivables in IPFS -> enter the sales data in CustomerOrder
- Major updates to most of the pages:
1. activate the tabbox (Semua, Hari-ini,...)
2. info label now reflects the data
3. voucher dialogs:
	- ref and description
	- includes in debit/credit description
	- allows description for adjustment in GiroReceipt
	- checks for empty db/cr before savings
	- includes total for db and cr
- Move the VoucherPayment create to GiroListInfo
- GiroListInfo sorted by Giro due date
- Fix the customerorder product dialog.  When user cliks the calculate weight / sheet button, the reference MUST use the inventory id instead of customerorderproduct id (because the 1st time it does not exist). 
- Implement CustomerReceivables:
1. Duplicates in Customer (see customer-set-unique.sql) are removed.  MySql is set to reject duplicates company_legal_name.
2. Backup e021_swi_core schema using the Export feature.
3. Create the CustomerReceivable and its associated tables as well as all the join tables (see receivable.sql)
4. Run the local pyramix-app against the production database.
	- Create receivable accounts by running the Receivables routine (click the 'Receivables' button) in the CustomerListInfo.zul
5. Run the CustomerListInfo.zul:
	- test add New customer
	- test edit existing customer

3.2.6 - 22 / 04 / 2018
- All customer receivables are completed
- Printing SuratJalan is now ready
- Inventory Type is included
a. user can add / edit inventory type
b. due to error during saving / edit in the inventory code, the auto indexer is set to manual in hibernate.properties:
  hibernate.search.indexing_strategy = manual
c. Others inventory type (OT) is added into the inventory type, and OT is added as the inventory code.

- All Inventory data is the same as IPFS
a. Manually transfer inventory data in sqlserver into pyramix database
b. Unknown inventory code is assigned OT (id=101)

- Search problem
	after deployment, search not functioning because indexing is not created.  User should edit one of the inventory item and save.  This indexes the Inventory, InventoryCode, and Customer object.
- Wrong formatring in Customer Order edit item (during create)
	all decimalbox must use setLocale() to allow ',' as the decimal places instead of '.'
- Inventory List not the same as the one in adding inventory dialog to the customer order.
- Inventory Packing Tab is not placed correctly when user search for blanks (searchTextbox is empty) signifying reseting the search to display all items.
- Fix ppn calculation bug when user clicks edit in each row of customer order
- Add ppn / non-ppn list in CustomerOrderListInfo.zul

3.2.7 - 20/05/2018
Work on EmployeeCommissions:
	- List by descending order CustomerOrder order date
	- List by Employees
Added non-ppn numbering for: CustomerOrder; SuratJalan; Faktur