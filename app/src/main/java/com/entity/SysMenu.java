package com.entity;

public class SysMenu extends BaseModel{
	
	private String menuCode;      //菜单编号
	private String menuName;	  //菜单名称
	private String upMenuCode;	  //父级菜单编号
	private String menuPage;	  //菜单地址
	private int menuLevel;		  //菜单等级
	private int sort;			  //排序
	
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getUpMenuCode() {
		return upMenuCode;
	}
	public void setUpMenuCode(String upMenuCode) {
		this.upMenuCode = upMenuCode;
	}
	public String getMenuPage() {
		return menuPage;
	}
	public void setMenuPage(String menuPage) {
		this.menuPage = menuPage;
	}
	public int getMenuLevel() {
		return menuLevel;
	}
	public void setMenuLevel(int menuLevel) {
		this.menuLevel = menuLevel;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}

}
