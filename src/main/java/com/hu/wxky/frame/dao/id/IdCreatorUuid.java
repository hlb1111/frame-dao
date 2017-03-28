package com.hu.wxky.frame.dao.id;

import java.util.UUID;

public class IdCreatorUuid implements IdCreator{

	@Override
	public Object getId() {
		UUID u = UUID.randomUUID();
		String r = u.toString();
		r = r.replaceAll("-", "");
		return r;
	}
	
	
}
