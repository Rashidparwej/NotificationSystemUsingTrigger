package com.fny.reports.service.persistence.dao;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;               

import com.fny.reports.commons.entity.BlairDO;
import com.fny.reports.commons.entity.ChunckDO;
import com.fny.reports.commons.entity.NateDO;
import com.fny.reports.commons.entity.NotificationQueueDO;

@Repository
@Service
public class GossiphGetEmailsDao {
	

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private final static Log LOG = LogFactory.getLog(GossiphGetEmailsDao.class);

	
		public List<String> queryForChangeInNotificationqueue() throws Exception {
		
			String sql = "select email from gossip_master where character_id in (select subscribed_character_id from Gossiphgirl.notification_queue "+" inner join Gossiphgirl.subscription onnotification_queue.character_id=subscription.character_id  where sent=0)";
	        LOG.info(sql);                                         
		
			List<String> eml = this.jdbcTemplate.query(sql, new ResultSetExtractor<List<String>>() {

				public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<String> eml = new ArrayList<String>();
					while (rs.next()) {
		            
						eml.add(rs.getString("email"));
						
					}
					removeDuplicate(eml);
					LOG.info("getting email ids "+eml);
					return eml;
				}

			});
			return eml;
		 }
		public static <String> void removeDuplicate(List<String> list) {
			LOG.info("removing Duplictes");
			HashSet<String> h = new HashSet<String>(list);
			list.clear();
			list.addAll(h);
		}
		
		
		
					
	
}
