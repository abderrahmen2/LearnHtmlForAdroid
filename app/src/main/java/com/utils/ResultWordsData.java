package com.utils;

import java.util.List;

public class ResultWordsData<T> {
	private boolean success;
	private List<T> list;
	private String message;
	private int pageNumber;    //页码

	public ResultWordsData( ) {

	}

	public ResultWordsData( List<T> list, int pageNumber) {
		super();
		this.success = success;
		this.list = list;
		this.pageNumber = pageNumber;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public ResultWordsData(boolean success, List<T> list) {
		this.success = success;
		this.list = list;
	}

	public ResultWordsData(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	

}
