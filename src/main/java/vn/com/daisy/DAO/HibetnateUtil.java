package vn.com.daisy.DAO;

import java.io.File;

///import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import vn.com.daisy.Common;

public class HibetnateUtil {
	private static SessionFactory sessionFactory = null;

	@SuppressWarnings("deprecation")
	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				String workingDir = System.getProperty("user.dir");
				File file = null;
				Common.print("getSessionFactory()", workingDir);
				if (workingDir.contains("\\")) {
					Common.print("getSessionFactory()", "WIN OS");
					file = new File(workingDir + "\\hibernate.cfg.xml");
				}
				if (workingDir.contains("/")) {
					file = new File(workingDir + "/hibernate.cfg.xml");
					Common.print("getSessionFactory()", "LINUX Distributor");
				}
				// sessionFactory = new
				// Configuration().configure("/vn/com/daisy/hibernateconfig/hibernate.cfg.xml")
				// .buildSessionFactory();
				sessionFactory = new Configuration().configure(file).buildSessionFactory();
				System.err.println("Connect to database successful");
			} catch (Exception ex) {
				// Make sure you log the exception, as it might be swallowed
				System.err.println("sessionFactory error " + ex);
				System.err.println("Cannot connect to database. Connect refuse!!!");
				System.exit(1);
			}
		}

		return sessionFactory;
	}

	public static Session getCurrentSession() {
		return getSessionFactory().getCurrentSession();
	}

	public static Session openSession() {
		return getSessionFactory().openSession();
	}
}
