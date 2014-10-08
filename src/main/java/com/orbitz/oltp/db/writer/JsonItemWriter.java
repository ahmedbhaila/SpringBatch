package com.orbitz.oltp.db.writer;

import java.util.List;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;

import com.orbitz.oltp.db.model.MemberTrip;

public class JsonItemWriter extends CompositeItemWriter<MemberTrip> {

	JdbcBatchItemWriter<? super MemberTrip> writer1;
	JdbcBatchItemWriter<? super MemberTrip> writer2;
	
	@Override
	public void write(List<? extends MemberTrip> memberTrip) throws Exception {
		writer1.write(memberTrip);
		writer2.write(memberTrip);
	}

	public JdbcBatchItemWriter<? super MemberTrip> getWriter1() {
		return writer1;
	}

	public void setWriter1(JdbcBatchItemWriter<? super MemberTrip> writer1) {
		this.writer1 = writer1;
	}

	public JdbcBatchItemWriter<? super MemberTrip> getWriter2() {
		return writer2;
	}

	public void setWriter2(JdbcBatchItemWriter<? super MemberTrip> writer2) {
		this.writer2 = writer2;
	}
}
