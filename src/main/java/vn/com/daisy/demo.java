package vn.com.daisy;

import java.util.Date;

import vn.com.daisy.DAO.BvlCollateInfoDAO;

public class demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BvlCollateInfoDAO dao = new BvlCollateInfoDAO();
		System.err.println(dao.getBvlCollateInfo(new Date(), "MATCH", "NO").getBciCollateResult());
	}

}
