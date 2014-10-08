package com.orbitz.oltp.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.orbitz.oltp.db.model.MemberTrip;

public class MemberRowMapper implements RowMapper<MemberTrip> {

	public MemberTrip mapRow(ResultSet rs, int rowNum) throws SQLException {
		MemberTrip memberTrip = new MemberTrip();
		//memberTrip.setEmail(rs.getString("PII_EMAIL"));
		memberTrip.setPosId(rs.getInt("REF_POINT_OF_SALE_ID"));
		memberTrip.setTripLocator(rs.getString("ORBITZLOCATORCODE"));
		memberTrip.setTravelPlanId(rs.getString("CUSTOMER_CLASSIC_TRAVELPLAN_ID"));
		memberTrip.setMemberId(rs.getInt("MEMBERID"));
		return memberTrip;
	}

}
