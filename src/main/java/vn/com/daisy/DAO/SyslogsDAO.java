package vn.com.daisy.DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SyslogsDAO {
	public void addData(Syslogs sysLogs) {
		Transaction trans = null;
		Session sess = null;
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			sess.saveOrUpdate(sysLogs);
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

}
