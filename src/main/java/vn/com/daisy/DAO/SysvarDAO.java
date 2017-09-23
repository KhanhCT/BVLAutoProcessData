package vn.com.daisy.DAO;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SysvarDAO {
	
	public void addData(Sysvar sysVar) {
		Transaction trans = null;
		Session sess = null;
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			sess.saveOrUpdate(sysVar);
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
	
	public String getValue(String varName) {
		Transaction trans = null;
		String value = null;
		Session sess = null;
		String sqlQuery = "SELECT varValue FROM Sysvar WHERE varName= :varName";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			query.setString("varName", varName.trim());
			value = (String) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return value;

	}

	public Sysvar getSysvar(String varName) {
		Transaction trans = null;
		Sysvar sysVar =  new Sysvar();
		Session sess = null;
		String sqlQuery = "FROM Sysvar WHERE varName= :varName";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			
			Query query = sess.createQuery(sqlQuery);
			query.setString("varName", varName);
			System.out.println(sqlQuery);
			sysVar = (Sysvar) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return sysVar;

	}

	@SuppressWarnings("unchecked")
	public List<Sysvar> getAllSysvar() {
		Transaction trans = null;
		List<Sysvar> sysVars = new ArrayList<>();
		Session sess = null;
		String sqlQuery = "FROM Sysvar";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			sysVars = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return sysVars;

	}

}
