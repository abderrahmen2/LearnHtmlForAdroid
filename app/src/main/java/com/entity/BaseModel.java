package com.entity;

/**
 * 公用模型
 * @author novice
 *
 */
public class BaseModel {

	private String creator;            // 创建人
	private String createTime;         // 创建时间
	private String mender;             // 修改人
	private String mendTime;           // 修改时间
	private String remark;             // 备注
	public String getCreator ( )
	{
		return creator;
	}
	public void setCreator ( String creator )
	{
		this.creator = creator;
	}
	public String getCreateTime ( )
	{
		return createTime;
	}
	public void setCreateTime ( String createTime )
	{
		this.createTime = createTime;
	}
	public String getMender ( )
	{
		return mender;
	}
	public void setMender ( String mender )
	{
		this.mender = mender;
	}
	public String getMendTime ( )
	{
		return mendTime;
	}
	public void setMendTime ( String mendTime )
	{
		this.mendTime = mendTime;
	}
	public String getRemark ( )
	{
		return remark;
	}
	public void setRemark ( String remark )
	{
		this.remark = remark;
	}

}
