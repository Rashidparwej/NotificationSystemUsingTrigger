package com.fny.reports.service.persistence.dao;

import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.fny.reports.commons.entity.ChunckDO;
import com.fny.reports.commons.entity.NateDO;
@Repository
@Service
@EnableScheduling
public class SheduledJobUsingTrigger {
	private static ExecutorService emailExecutorService = Executors.newFixedThreadPool(100);
	
	@Autowired
	private EmailSenderService emailSenderServiceImpl;
	
    @Autowired
    private GossiphGetEmailsDao gossiphGetEmailsDao;
	@Autowired                                              
	VelocityEngine velocityEngine;     

	@Value("${reports.main.alerttemplate}")                           
	private String  alerttemplate;
	@Value("${reports.main.counttemplate}")                        
	private String countTemplate;                                          
	@Value("${reports.main.subject}")
	private String subject;
	@Value("${reports.main.sender}")                                          
	private String sender;
	@Value("${reports.main.senderAlert}")
	private String subjectAlert;
	private final static Log LOG = LogFactory.getLog(SheduledJobUsingTrigger.class);

	@Scheduled(fixedRate =600000)  
	public void checkForUpdates() throws Exception
	{
		Template vTemplate = velocityEngine.getTemplate(countTemplate);
		VelocityContext context = new VelocityContext();
		StringWriter writer = new StringWriter();
        
		vTemplate.merge(context, writer);

		String body = writer.toString();    
		LOG.info("inside sheduled function");
	         List<String>emails=gossiphGetEmailsDao.queryForChangeInNotificationqueue();                                        
	
		   for(int i=0;i<emails.size();i++)
		   {
	    		sendReportMail(emails.get(i), subject, sender, body); 
	    		LOG.info("Gtting emails"+ emails.get(i));
			   //send email to these ids

	       }
	  }
		   private void sendReportMail(final String to, final String subject, final String sender, final String body) {
				@SuppressWarnings("unchecked")
				FutureTask futureTask = new FutureTask(new Callable() { 
					@Override
					public Object call() throws Exception {
						try {
							emailSenderServiceImpl.send(to, subject, sender, body);
							LOG.info("Snding mail");
						} catch (Exception ex) {
							LOG.error("Exception = ", ex);
						}
						return null;
					}
				});
				emailExecutorService.execute(futureTask);
			}
	}
