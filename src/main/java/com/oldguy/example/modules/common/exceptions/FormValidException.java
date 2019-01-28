package com.oldguy.example.modules.common.exceptions;

/**
 * @Description: 表单校验异常
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/26 0026 14:44
 */
public class FormValidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FormValidException(String msg) {
		super(msg);
	}

}
