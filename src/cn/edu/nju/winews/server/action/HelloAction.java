package cn.edu.nju.winews.server.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

@Controller("helloAction")
@Scope("prototype")
public class HelloAction extends ActionSupport {

	private static final long serialVersionUID = 9026533527322851032L;

	private static Log logger = LogFactory.getLog(HelloAction.class);

	public String execute() {

		ServletActionContext.getRequest().setAttribute("message", "struts,spring测试");
		return SUCCESS;
	}
}