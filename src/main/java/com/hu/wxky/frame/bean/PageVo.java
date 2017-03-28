package com.hu.wxky.frame.bean;

import java.util.List;

/**
 * 分页对象
 * 
 * @author Hulb
 * @date 2015年2月11日
 * @param <E>
 */
public class PageVo<E> extends BaseBean {
	private static final long serialVersionUID = -7377073589171847262L;
	/** 当前页 **/
	private int page = 1;
	/** 没有显示的条数 默认是30 **/
	private int size = 30;
	// limit实际参数
	private int start;
	/** 总的记录数 **/
	private int allCounts;
	/** 总的页数 **/
	private int allPages;
	/** 当前页的内容对象 **/
	private List<E> list;
	/** 业务查询是否成功 **/
	private boolean success;
	/** 如果失败，表示失败的原因 **/
	private String msg;
	/** 排序字段 */
	private String order;
	
	public PageVo() {
	}

	public PageVo(int page, int size) {
		this.page = page;
		this.size = size;
		buildStart();
	}
	
	private void buildStart() {
		this.start = (getPage()-1)*getSize();
	}

	public PageVo(int page, int size, String order) {
		this.page = page;
		this.size = size;
		this.order = order;
		buildStart();
	}

	public <T> PageVo(PageVo<T> oriPage) {
		if (null == oriPage) {
			return;
		}

		setAllCounts(oriPage.getAllCounts());
		setMsg(oriPage.getMsg());
		setSuccess(oriPage.getSuccess());
		setOrder(oriPage.getOrder());
		setPage(oriPage.getPage());
		setSize(oriPage.getSize());
		buildStart();
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getPage() {
		return page <= 0 ? 1 : page;
	}

	public void setPage(int page) {
		this.page = page;
		buildStart();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		buildStart();
	}

	public int getAllCounts() {
		return allCounts;
	}

	public void setAllCounts(int allCounts) {
		this.allCounts = allCounts;
		allPages = allCounts / size;
		if (allCounts % size > 0) {
			allPages++;
		}
		if (allPages == 0) {
			allPages = 1;
		}
	}

	public int getAllPages() {
		return allPages;
	}

	public void setAllPages(int allPages) {
		if (allPages == 0) {
			allPages = 1;
		}
		this.allPages = allPages;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public boolean getSuccess() {
		return success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}


}
