package vn.com.daisy.DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BvlCompanyDAO {
	public String getBCOBankAccount(String bcoCode) {
		String account = null;
		Transaction trans = null;
		Session sess = null;
		String sqlQuery = "SELECT bcoBankAccount FROM BvlCompany WHERE id.bcoCompanyCode= :bcoCode";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bcoCode", bcoCode.trim());
			account = (String) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return account;

	}
	public String getbcoCompanyBvlifeId(String bcoCode) {
		String account = null;
		Transaction trans = null;
		Session sess = null;
		String sqlQuery = "SELECT bcoCompanyBvlifeId FROM BvlCompany WHERE id.bcoCompanyCode= :bcoCode";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("bcoCode", bcoCode.trim());
			account = (String) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return account;

	}
}
