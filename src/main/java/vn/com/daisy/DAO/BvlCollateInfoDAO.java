package vn.com.daisy.DAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BvlCollateInfoDAO {
	public BvlCollateInfo getBvlCollateInfo(Date bciDate, String bciCollateResult, String bciExportStatus) {
		Transaction trans = null;
		BvlCollateInfo bvlCollateInfo =null;
		Session sess = null;
		String sqlQuery = "FROM BvlCollateInfo WHERE bciCollateResult= :bciCollateResult AND bciExportStatus= :bciExportStatus AND id.bciDate> :bciDate";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			DateFormat sf = new SimpleDateFormat("dd-MMM-yy");
			String date = sf.format(bciDate);
			
			System.out.println(date);
			Query query = sess.createQuery(sqlQuery);
			query.setString("bciCollateResult", bciCollateResult);
			query.setString("bciExportStatus", bciExportStatus.trim());
			query.setString("bciDate", date);

			bvlCollateInfo = (BvlCollateInfo) query.uniqueResult();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bvlCollateInfo;

	}

	public void addBvlCollateInfos(BvlCollateInfo bc) {
		Transaction trans = null;
		Session sess = null;
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			sess.saveOrUpdate(bc);
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

	@SuppressWarnings("unchecked")
	public List<BvlCollateInfo> getBvlCollateInfos() {
		Transaction trans = null;
		List<BvlCollateInfo> bvlCollateInfos = new ArrayList<>();
		Session sess = null;
		String sqlQuery = "FROM BvlCollateInfo";
		sess = HibetnateUtil.openSession();
		try {
			trans = sess.beginTransaction();
			Query query = sess.createQuery(sqlQuery);
			bvlCollateInfos = query.list();
			trans.commit();
		} catch (RuntimeException e) {
			if (trans != null) {
				trans.rollback();
			}
		} finally {
			sess.flush();
			sess.close();
		}

		return bvlCollateInfos;

	}
}
