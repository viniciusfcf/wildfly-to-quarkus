package com.github.viniciusfcf.wildfly;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

//interceptor-api
public class MeuInterceptor {

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception {
		System.out.println("=====MeuInterceptor===intercept() "+ctx.getMethod().getName());
		return ctx.proceed();
	}

}
