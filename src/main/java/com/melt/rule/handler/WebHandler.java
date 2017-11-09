package com.melt.rule.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.melt.rule.RuleEnginner;

public abstract class WebHandler implements RuleEnginner{

	abstract boolean preHandle(HttpServletRequest request,HttpServletResponse reponse) ;
	
	abstract boolean postHandle(HttpServletRequest request,HttpServletResponse reponse) ;
	
	
}
