package com.company.cellphone.model;

import java.io.Serializable;
import java.util.Date;


import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class CellPhone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer empId;
	private String empName;
	private Date purchaseDate;
	private String model;

}
