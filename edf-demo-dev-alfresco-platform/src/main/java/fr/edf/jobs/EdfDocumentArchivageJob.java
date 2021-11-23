package fr.edf.jobs;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class EdfDocumentArchivageJob extends AbstractScheduledLockedJob {
	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobData = context.getJobDetail().getJobDataMap();

		// Extract the Job executer to use
		Object executerObj = jobData.get("jobExecuter");
		if (executerObj == null || !(executerObj instanceof EdfDocumentArchivageJobExecuter)) {
			throw new AlfrescoRuntimeException("ScheduledJob data must contain valid 'Executer' reference");
		}

		final EdfDocumentArchivageJobExecuter jobExecuter = (EdfDocumentArchivageJobExecuter) executerObj;
		
		jobExecuter.execute();
	}
}
