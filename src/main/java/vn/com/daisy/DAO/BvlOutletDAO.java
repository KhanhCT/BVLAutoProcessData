package vn.com.daisy.DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BvlOutletDAO {
	public void addData(BvlOutlet bo) {
		Transaction trans = null;
		Session sess = null;
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			sess.saveOrUpdate(bo);
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

	public String getoutletBvlifeId(String outletNumber) {
		String result = null;
		Transaction trans = null;
		Session sess = null;
		String sqlQuery = "SELECT outletBvlifeId FROM BvlOutlet WHERE id.outletNumber= :outletNumber";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("outletNumber", outletNumber);
			result = (String) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return result;

	}
}
