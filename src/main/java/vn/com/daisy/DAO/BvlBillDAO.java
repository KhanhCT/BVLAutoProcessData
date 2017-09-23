package vn.com.daisy.DAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BvlBillDAO {
	public void addData(BvlBill bill) {
		Transaction trans = null;
		Session sess = null;
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			sess.saveOrUpdate(bill);
			sess.getTransaction().commit();
		} catch (RuntimeException ex) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}
	}

	public void addMultiRows(List<BvlBill> bills) {
		Transaction trans = null;
		Session sess = null;
		sess = HibetnateUtil.openSession();

		try {
			trans = sess.beginTransaction();
			for (int i = 0; i < bills.size(); i++) {
				sess.saveOrUpdate(bills.get(i));
				if (i % 20 == 0) {
					sess.flush();
					sess.clear();
				}
			}
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}
	}

	public BvlBill getBill(String bbiInvoiceNumber, String bbiBarcode) {
		Transaction trans = null;
		BvlBill bill = null;
		Session sess = null;
		String sqlQuery = "FROM BvlBill WHERE bbiInvoiceNumber= :bbiInvoiceNumber AND bbiBarcode= :bbiBarcode";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bbiBarcode", bbiBarcode);
			query.setString("bbiInvoiceNumber", bbiInvoiceNumber);
			bill = (BvlBill) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bill;

	}

	public BvlBill getBill(String bbiBarcode) {
		Transaction trans = null;
		BvlBill bill = null;
		Session sess = null;
		String sqlQuery = "FROM BvlBill WHERE  id.bbiBarcode= :bbiBarcode";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bbiBarcode", bbiBarcode);
			bill = (BvlBill) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bill;
	}
	
	@SuppressWarnings("unchecked")
	public List<BvlBill> getBills() {
		Transaction trans = null;
		List<BvlBill> bills = new ArrayList<>();
		Session sess = null;
		String sqlQuery = "FROM BvlBill";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			bills = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bills;
	}


	@SuppressWarnings("unchecked")
	public List<BvlBill> getAllBills(String bbiStatus) {
		Transaction trans = null;
		List<BvlBill> bills = new ArrayList<BvlBill>();
		Session sess = null;
		String sqlQuery = "FROM BvlBill WHERE bbiStatus= :bbiStatus";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bbiStatus", bbiStatus);
			bills = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bills;

	}

	@SuppressWarnings("unchecked")
	public List<BvlBill> getAllBills(Date bbiEffectiveDate, String bbiDataSource, String bbiBankPerform,
			String bbiInvoiceType) {
		Transaction trans = null;
		List<BvlBill> bills = new ArrayList<BvlBill>();
		Session sess = null;
		String sqlQuery = "FROM BvlBill WHERE bbiDataSource= :bbiDataSource AND bbiInvoiceType= :bbiInvoiceType AND bbiEffectiveDate> :bbiEffectiveDate AND bbiBankPerform= :bbiBankPerform";
		sess = HibetnateUtil.openSession();
		try {
			DateFormat sf = new SimpleDateFormat("dd-MMM-yy");
			String date = sf.format(bbiEffectiveDate);
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bbiDataSource", bbiDataSource.trim());
			query.setString("bbiInvoiceType", bbiInvoiceType);
			query.setString("bbiEffectiveDate", date);
			query.setString("bbiBankPerform", bbiBankPerform);
			bills = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bills;

	}

	@SuppressWarnings("unchecked")
	public List<BvlBill> getAllBills(Date bbiEffectiveDate, String bbiBankPerform) {
		Transaction trans = null;
		List<BvlBill> bills = new ArrayList<BvlBill>();
		Session sess = null;
		String sqlQuery = "FROM BvlBill WHERE bbiEffectiveDate> :bbiEffectiveDate AND bbiBankPerform= :bbiBankPerform";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			DateFormat sf = new SimpleDateFormat("dd-MMM-yy");
			String date = sf.format(bbiEffectiveDate);
			System.out.println(date);
			query.setString("bbiEffectiveDate", date);
			query.setString("bbiBankPerform",bbiBankPerform );
			bills = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bills;

	}

	@SuppressWarnings("unchecked")
	public List<BvlBill> getAllBill(Date bbiEffectiveDate) {
		Transaction trans = null;
		List<BvlBill> bills = null;
		Session sess = null;
		String sqlQuery = "FROM BvlBill WHERE bbiEffectiveDate> :bbiEffectiveDate";
		DateFormat sf = new SimpleDateFormat("dd-MMM-yy");
		String a = sf.format(bbiEffectiveDate);
		System.out.println(a);
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bbiEffectiveDate", a);
			bills = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bills;

	}
}
